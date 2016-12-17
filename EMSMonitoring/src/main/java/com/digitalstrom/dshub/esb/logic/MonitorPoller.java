package com.digitalstrom.dshub.esb.logic;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

import com.digitalstrom.dshub.esb.contract.IMonitorStatisticsProvider;

public class MonitorPoller implements Runnable {

	private Map map_queues_mg_count_tmp = null;
	private Map map_topics_mg_count_tmp = null;
	private Map map_queues_mg_count = null;
	private Map map_topics_mg_count = null;
	private BlockingQueue<Integer> queue = null;
	private IMonitorStatisticsProvider msp_queues = null;
	private IMonitorStatisticsProvider msp_topics = null;
	int polling_time_is_msec = 0;

	public MonitorPoller(Map map_queues_mg_count, Map map_topics_mg_count, Map map_queues_mg_count_tmp,
			Map map_topics_mg_count_tmp, IMonitorStatisticsProvider msp_queues, IMonitorStatisticsProvider msp_topics,
			int polling_time_is_sec, BlockingQueue<Integer> queue) {
		super();
		this.map_queues_mg_count_tmp = map_queues_mg_count_tmp;
		this.map_topics_mg_count_tmp = map_topics_mg_count_tmp;
		this.map_queues_mg_count = map_queues_mg_count;
		this.map_topics_mg_count = map_topics_mg_count;
		this.queue = queue;
		this.msp_queues = msp_queues;
		this.msp_topics = msp_topics;
		this.polling_time_is_msec = polling_time_is_sec*1000;
	}

	public void run() {

		while (true) {
			try {
				if (queue.isEmpty()) {
					this.map_queues_mg_count_tmp = msp_queues.getDestinationsPendingMessageCount();
					this.map_topics_mg_count_tmp = msp_topics.getDestinationsPendingMessageCount();
					queue.put(1);
				}else
					System.out.println("The Ruler has not completed or not running. Poller going to sleep again!");
				Thread.sleep(polling_time_is_msec);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
