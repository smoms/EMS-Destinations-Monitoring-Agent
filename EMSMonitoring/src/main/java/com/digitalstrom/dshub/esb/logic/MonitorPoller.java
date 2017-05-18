package com.digitalstrom.dshub.esb.logic;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import org.apache.log4j.*;

import com.digitalstrom.dshub.esb.contract.IAdminProvider;
import com.digitalstrom.dshub.esb.contract.IMonitorStatisticsProvider;
import com.digitalstrom.dshub.esb.contract.INotifier;
import com.digitalstrom.dshub.esb.util.LogUtil;
import com.digitalstrom.dshub.esb.util.ReadConfigs;
import com.tibco.tibjms.admin.TibjmsAdminException;

public class MonitorPoller implements Runnable {

	private final static Logger logger = Logger.getLogger("MonitorAgent");
	private final static int destinationQueue = 0;
	private final static int destinationTopic = 1;
	private Map<String, Long> map_queues_mg_count_tmp = null;
	private Map<String, Long> map_topics_mg_count_tmp = null;
	private Map<String, Long> map_queues_mg_count = null;
	private Map<String, Long> map_topics_mg_count = null;
	private IMonitorStatisticsProvider msp_queues = null;
	private IMonitorStatisticsProvider msp_topics = null;
	private Map<String, Long> notificationBacklog = null;
	private Map<String, Long> notificationDeltaBacklog = null;
	private int polling_time_in_msec = 30000; // default to 30s
	private Date notificationDate = null; // determines when a notification will trigger
	private Date timeNow = null;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
	private INotifier notifier = null;
	private Map<String, String> map = ReadConfigs.getInstance();
	private int message_count_threshold = 1;
	private IAdminProvider adminP;
	private String serverName;

	public MonitorPoller() throws Exception {
		super();
		this.notifier = NotifierFactory.getFactory(map.get("notifier")).getNotifier();
		this.polling_time_in_msec = Integer.parseInt(map.get("serverpollingtimeinsec")) * 1000;
		this.message_count_threshold = Integer.parseInt(map.get("messagecountthreshold"));
		this.notificationBacklog = new HashMap<String, Long>();
		this.notificationDeltaBacklog = new HashMap<String, Long>();
		this.timeNow = this.getTimeNow();
		logger.info("Connecting to the server..");
		
	}

	public void run() {
		while (true) {
			try {
				this.msp_queues = new MonitorStatisticsProvider("QueuesInfoProvider");
				this.msp_topics = new MonitorStatisticsProvider("TopicsInfoProvider");
				this.adminP = AdminProvider.getInstance();
				this.serverName = this.adminP.getAdminConnection().getInfo().getServerName();
				logger.info("Connection status: ok");
				this.map_queues_mg_count_tmp = msp_queues.getDestinationsPendingMessageCount();
				this.map_topics_mg_count_tmp = msp_topics.getDestinationsPendingMessageCount();
				logger.debug("Poller has found these queues and pending messages respectively: " + map_queues_mg_count_tmp);
				logger.debug("Poller has found these topics and pending messages respectively: " + map_topics_mg_count_tmp);
				logger.info("Poller has these queues and pending messages stored in memory: " + map_queues_mg_count);
				logger.info("Poller has these topics and pending messages stored in memory: " + map_topics_mg_count);
				logger.info("The Poller is triggering the queues checking rules..");
				this.applyDestinationRules(map_queues_mg_count, map_queues_mg_count_tmp,
						MonitorPoller.destinationQueue);
				logger.info("The Poller is triggering the topics checking rules..");
				this.applyDestinationRules(map_topics_mg_count, map_topics_mg_count_tmp,
						MonitorPoller.destinationTopic);
				timeNow = this.getTimeNow();
				if (this.notificationDate == null || this.timeNow.compareTo(this.notificationDate) >= 0) {
					this.sendNotification(this.notificationBacklog, false);
					this.notificationDate = this.upsertNotificationDate(notificationDate);
					logger.debug("Notification date refreshed: " + notificationDate);
				}
				this.sendNotification(this.notificationDeltaBacklog, true);
				logger.info("Next backlog notification scheduled at about: " + sdf.format(notificationDate.getTime()));
				logger.info("The Poller is going to sleep for seconds: " + this.polling_time_in_msec / 1000);
			} catch (ParseException e) {
				logger.error("Error in the poller loop when converting date format: ");
				e.printStackTrace();
			}catch (Exception e) {
				logger.error("Error in the Poller loop");
				e.printStackTrace();
			}
			try {
				Thread.sleep(polling_time_in_msec);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void applyDestinationRules(Map<String, Long> map_destinations_msg_count,
			Map<String, Long> map_destinations_mg_count_tmp, int destinationType) throws ParseException {

		Integer count = new Integer(0);
		Integer tmpCount = new Integer(0);
		Integer countThresholdMultiplier = new Integer(0);
		Integer countTmpThresholdMultiplier = new Integer(0);

		// Initialize map_destination_msg_count hashset
		if (map_destinations_msg_count == null) {
			if (destinationType == MonitorPoller.destinationQueue) {
				this.map_queues_mg_count = new HashMap<String, Long>();
				map_destinations_msg_count = this.map_queues_mg_count;
			} else {
				this.map_topics_mg_count = new HashMap<String, Long>();
				map_destinations_msg_count = this.map_topics_mg_count;
			}
		}
		for (Map.Entry<String, Long> entry : map_destinations_mg_count_tmp.entrySet()) {
			if (map_destinations_msg_count.containsKey(entry.getKey())) {
				count = map_destinations_msg_count.get(entry.getKey()).intValue();
				tmpCount = map_destinations_mg_count_tmp.get(entry.getKey()).intValue();
				countThresholdMultiplier = count / this.message_count_threshold;
				countTmpThresholdMultiplier = tmpCount / this.message_count_threshold;
				
				logger.debug("Previous backlog of the destination: " + entry.getKey() + count);
				logger.debug("Current backlog of the destination: " + entry.getKey() + tmpCount);
				if (countTmpThresholdMultiplier > countThresholdMultiplier)
					this.notificationDeltaBacklog.put(entry.getKey(), entry.getValue()); //update the notification backlog
				else if(tmpCount > this.message_count_threshold && countTmpThresholdMultiplier < countThresholdMultiplier
						&& this.notificationDeltaBacklog.containsKey(entry.getKey()))
				{//we remove the entry from the delta notification backlogs since the load on this destination has decreased below a multiple threshold
					logger.debug(LogUtil.lazyFormat("Removing destination %s from delta backlog because load is decreased to %s ", entry.getKey(), tmpCount));
					this.notificationDeltaBacklog.remove(entry.getKey());				
				} else if ((this.notificationDeltaBacklog.containsKey(entry.getKey())  ||  this.notificationBacklog.containsKey(entry.getKey())) && 
						tmpCount < this.message_count_threshold){
					//we remove the entry from both notification backlogs since the load on this destination is below 1x threshold
					logger.debug(LogUtil.lazyFormat("Removing destination %s from all backlogs because load is decreased to %s ", entry.getKey(), tmpCount));
					this.notificationDeltaBacklog.remove(entry.getKey());
					this.notificationBacklog.remove(entry.getKey());
				}			
				map_destinations_msg_count.put(entry.getKey(), entry.getValue());
			} else if (entry.getValue() > this.message_count_threshold) {
				this.notificationBacklog.put(entry.getKey(), entry.getValue());
				map_destinations_msg_count.put(entry.getKey(), entry.getValue());
			}
		}
	}

	private void sendNotification(Map notifBacklog, boolean isDeltaBacklog)
			throws ParseException, TibjmsAdminException {
		if (notifBacklog != null && notifBacklog.size() > 0) {
			logger.info("The Poller is triggering the notification to channel: " + map.get("notifier"));
			this.notifier.SendNotification(notifBacklog, isDeltaBacklog, map.get("emailtitle"), this.serverName,
					map.get("environment"));
		}
		if (isDeltaBacklog)
			this.notificationDeltaBacklog.clear();
	}

	private Date upsertNotificationDate(Date notifDate) {
		Calendar calNotifDate = Calendar.getInstance();
		int notifPeriod;
		try {
			notifPeriod = Integer.parseInt(map.get("notificationperiodinhours"));
		} catch (NumberFormatException e) {
			notifPeriod = 24; // default is 24h
			logger.error("notificationPeriodInHours configuration parameter is not a valid number. Fallback to default time: " + notifPeriod
					+ "h");
		}
		if (notifDate != null)
			calNotifDate.setTime(notifDate);
		else
			calNotifDate.setTime(new Date());
		//calNotifDate.add(Calendar.HOUR_OF_DAY, notifPeriod); //add notification period as offset
		calNotifDate.add(Calendar.SECOND, notifPeriod); //add notification period as offset
		return calNotifDate.getTime();
	}

	private Date getTimeNow() {
		Calendar cal = Calendar.getInstance(); // creates calendar
		cal.setTime(new Date()); // sets calendar time/date
		return cal.getTime(); // returns new date object, one hour in the future
	}
}
