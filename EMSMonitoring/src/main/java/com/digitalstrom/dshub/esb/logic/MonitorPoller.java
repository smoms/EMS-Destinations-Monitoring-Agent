package com.digitalstrom.dshub.esb.logic;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import com.digitalstrom.dshub.esb.contract.IAdminProvider;
import com.digitalstrom.dshub.esb.contract.IMonitorStatisticsProvider;
import com.digitalstrom.dshub.esb.contract.INotifier;
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
	int polling_time_is_msec = 0;
	private int message_count_threshold = 0;
	private Date notificationDate = null;
	private INotifier notifier = NotifierFactory.getFactory("ConsoleNotifierFactory").getNotifier();

	public MonitorPoller(IMonitorStatisticsProvider msp_queues, IMonitorStatisticsProvider msp_topics,
			int polling_time_is_sec, int message_count_threshold) throws Exception {
		super();
		this.msp_queues = msp_queues;
		this.msp_topics = msp_topics;
		this.polling_time_is_msec = polling_time_is_sec * 1000;
		this.message_count_threshold = message_count_threshold;
	}

	public void run() {
		while (true) {
			try {
				this.map_queues_mg_count_tmp = msp_queues.getDestinationsPendingMessageCount();
				this.map_topics_mg_count_tmp = msp_topics.getDestinationsPendingMessageCount();
				this.notificationBacklog = new HashMap<String, Long>();
				logger.debug("The Poller is triggering the queue rules..");
				this.applyDestinationRules(map_queues_mg_count, map_queues_mg_count_tmp,
						MonitorPoller.destinationQueue);
				logger.debug("The Poller is triggering the topic rules..");
				this.applyDestinationRules(map_topics_mg_count, map_topics_mg_count_tmp,
						MonitorPoller.destinationTopic);
				this.checkForNotification();
				logger.debug("The Poller is going to sleep for seconds: " + polling_time_is_msec / 1000);
				Thread.sleep(polling_time_is_msec);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void applyDestinationRules(Map<String, Long> map_destinations_msg_count,
			Map<String, Long> map_destinations_mg_count_tmp, int destinaitonType) {

		Integer count = new Integer(0);
		Integer tmpCount = new Integer(0);
		Integer countThresholdMultiplier = new Integer(0);
		Integer countTmpThresholdMultiplier = new Integer(0);
		this.notificationBacklog = new HashMap<String, Long>();

		// Initialize map_destination_msg_count hashset
		if (map_destinations_msg_count == null) {
			if (destinaitonType == MonitorPoller.destinationQueue) {
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
				if (countTmpThresholdMultiplier > countThresholdMultiplier
						|| (entry.getValue() > this.message_count_threshold
								&& (this.notificationDate == null || new Date().after(notificationDate)))) {
					this.notificationBacklog.put(entry.getKey(), entry.getValue());
					map_destinations_msg_count.put(entry.getKey(), entry.getValue());
				}
			} else if (entry.getValue() > this.message_count_threshold) {
				this.notificationBacklog.put(entry.getKey(), entry.getValue());
				map_destinations_msg_count.put(entry.getKey(), entry.getValue());
			}
		}
	}

	private void checkForNotification() {
		if (this.notificationBacklog != null && this.notificationBacklog.size() > 0) {
			logger.debug("The Poller is triggering the notification..");
			this.notifier.SendNotification(notificationBacklog, "todo title", "todo env", "todo msg", "todo receivers");
		}
		notificationBacklog.clear();
		notificationDate = new Date();
	}

}
