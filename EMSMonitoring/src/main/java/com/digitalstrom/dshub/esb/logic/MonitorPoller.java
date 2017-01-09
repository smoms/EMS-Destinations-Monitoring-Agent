package com.digitalstrom.dshub.esb.logic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import com.digitalstrom.dshub.esb.contract.IAdminProvider;
import com.digitalstrom.dshub.esb.contract.IMonitorStatisticsProvider;
import com.digitalstrom.dshub.esb.contract.INotifier;
import com.digitalstrom.dshub.esb.util.ReadConfigs;
import com.tibco.tibjms.admin.TibjmsAdminException;

public class MonitorPoller implements Runnable {

	final static Logger logger = Logger.getLogger("MonitorAgent");
	final static int destinationQueue = 0;
	final static int destinationTopic = 1;
	private Map<String, Long> map_queues_mg_count_tmp = null;
	private Map<String, Long> map_topics_mg_count_tmp = null;
	private Map<String, Long> map_queues_mg_count = null;
	private Map<String, Long> map_topics_mg_count = null;
	private IMonitorStatisticsProvider msp_queues = null;
	private IMonitorStatisticsProvider msp_topics = null;
	private Map<String, Long> notificationBacklog = null;
	private Map<String, Long> notificationDeltaBacklog = null;
	int polling_time_is_msec = 30000; //default to 30s
	private Date notificationDate = null;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private INotifier notifier = NotifierFactory.getFactory("ConsoleNotifierFactory").getNotifier();
	private Map<String, String> map = ReadConfigs.getInstance();
	private int message_count_threshold = 1;
	
	public MonitorPoller(IMonitorStatisticsProvider msp_queues, IMonitorStatisticsProvider msp_topics) throws Exception {
		super();
		this.msp_queues = msp_queues;
		this.msp_topics = msp_topics;
		this.polling_time_is_msec = Integer.parseInt(map.get("serverpollingtimeinsec")) * 1000;
		this.message_count_threshold = Integer.parseInt(map.get("messagecountthreshold"));
		this.notificationBacklog = new HashMap<String, Long>();
		this.notificationDeltaBacklog = new HashMap<String, Long>();
	}

	public void run() {
		while (true) {
			try {
				this.map_queues_mg_count_tmp = msp_queues.getDestinationsPendingMessageCount();
				this.map_topics_mg_count_tmp = msp_topics.getDestinationsPendingMessageCount();
				logger.debug("Poller got these queues and pending messages: " + map_queues_mg_count_tmp);
				logger.debug("Poller got these topics and pending messages: " + map_topics_mg_count_tmp);
				logger.debug("Poller has these queues and pending messages stored in memory: " + map_queues_mg_count);
				logger.debug("Poller has these topics and pending messages stored in memory: " + map_topics_mg_count);
				logger.debug("The Poller is triggering the queue rules..");
				this.applyDestinationRules(map_queues_mg_count, map_queues_mg_count_tmp,
						MonitorPoller.destinationQueue);
				logger.debug("The Poller is triggering the topic rules..");
				this.applyDestinationRules(map_topics_mg_count, map_topics_mg_count_tmp,
						MonitorPoller.destinationTopic);
				if(this.notificationDate == null || sdf.parse(sdf.format(new Date())).after(this.notificationDate))
					this.sendNotification(this.notificationBacklog, false);
				this.sendNotification(this.notificationDeltaBacklog, true);
				this.notificationDate = sdf.parse(sdf.format(new Date()));
				logger.debug("Date refreshed: " + notificationDate);
				logger.debug("The Poller is going to sleep for seconds: " + this.polling_time_is_msec / 1000);
				Thread.sleep(polling_time_is_msec);
			}catch(ParseException e){
				logger.error("Error in the poller loop when converting date format: ");
				e.printStackTrace();
			}catch (Exception e) {
				logger.error("Error in the poller loop: ");
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
				map_queues_mg_count = new HashMap<String, Long>();
				map_destinations_msg_count = map_queues_mg_count;
			} else {
				map_topics_mg_count = new HashMap<String, Long>();
				map_destinations_msg_count = map_topics_mg_count;
			}
		}
		for (Map.Entry<String, Long> entry : map_destinations_mg_count_tmp.entrySet()) {
			if (map_destinations_msg_count.containsKey(entry.getKey())) {
				count = map_destinations_msg_count.get(entry.getKey()).intValue();
				tmpCount = map_destinations_mg_count_tmp.get(entry.getKey()).intValue();
				countThresholdMultiplier = count / this.message_count_threshold;
				countTmpThresholdMultiplier = tmpCount / this.message_count_threshold;
				if (countTmpThresholdMultiplier > countThresholdMultiplier)
					this.notificationDeltaBacklog.put(entry.getKey(), entry.getValue());
				map_destinations_msg_count.put(entry.getKey(), entry.getValue());
			}else if (entry.getValue() > this.message_count_threshold) {
				this.notificationBacklog.put(entry.getKey(), entry.getValue());
				map_destinations_msg_count.put(entry.getKey(), entry.getValue());
			}
		}
	}

	private void sendNotification(Map notifBacklog, boolean isDeltaBacklog) throws ParseException {
		if (notifBacklog != null && notifBacklog.size() > 0) {
			logger.debug("The Poller is triggering the notification..");
			this.notifier.SendNotification(notifBacklog, isDeltaBacklog, map.get("emailtitle"), map.get("environment"));
		}
		if(isDeltaBacklog)
			this.notificationDeltaBacklog.clear();
	}
	
}
