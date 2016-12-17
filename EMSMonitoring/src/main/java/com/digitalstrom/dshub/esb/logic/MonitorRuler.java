package com.digitalstrom.dshub.esb.logic;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

import com.digitalstrom.dshub.esb.contract.INotifier;

public class MonitorRuler implements Runnable {
	
	private Map map_queues_mg_count = null;
	private Map map_queues_mg_count_tmp = null;
	private Map map_topics_mg_count = null;
	private Map map_topics_mg_count_tmp = null;
	private BlockingQueue<Integer> queue = null;
	private INotifier notifier = null;
	private int message_count_threshold = 0;

	public MonitorRuler(Map map_queues_mg_count, Map map_queues_mg_count_tmp, Map map_topics_mg_count,
			Map map_topics_mg_count_tmp, BlockingQueue<Integer> queue, int message_count_threshold, INotifier notifier) {
		super();
		this.map_queues_mg_count = map_queues_mg_count;
		this.map_queues_mg_count_tmp = map_queues_mg_count_tmp;
		this.map_topics_mg_count = map_topics_mg_count;
		this.map_topics_mg_count_tmp = map_topics_mg_count_tmp;
		this.queue = queue;
		this.message_count_threshold = message_count_threshold;
		this.notifier = notifier;
	}

	public void run() {
		while (true) {
			try {
				queue.take();
				System.out.println("notified");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	//TODO
	private void applyRules(){}
	
}
