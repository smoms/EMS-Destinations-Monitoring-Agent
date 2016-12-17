package com.digitalstrom.dshub.esb.logic;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.apache.log4j.Logger;

import com.digitalstrom.dshub.esb.contract.IMonitorStatisticsProvider;
import com.digitalstrom.dshub.esb.contract.INotifier;
import com.tibco.tibjms.admin.TibjmsAdminException;

public class MonitorAgent {

	//TODO: read from config file
	int polling_time_is_sec = 1;
	private int message_count_threshold = 100;
	
	final static Logger logger = Logger.getLogger("MonitorAgent");
	private IMonitorStatisticsProvider msp_queues = null;
	private IMonitorStatisticsProvider msp_topics = null;
	private Map map_queues_mg_count = null;
	private Map map_topics_mg_count = null;
	private Map map_queues_mg_count_tmp = null;
	private Map map_topics_mg_count_tmp = null;
	private BlockingQueue<Integer> queue = null;
	private INotifier notifier = null;

	public MonitorAgent() {
		super();
		try {
			logger.info("Establishing connection to the server..");
			msp_queues = new MonitorStatisticsProvider("QueuesInfoFactory");
			msp_topics = new MonitorStatisticsProvider("TopicsInfoFactory");
			notifier = NotifierFactory.getFactory("ConsoleNotifierFactory").getNotifier();
		}catch (TibjmsAdminException e) {
			logger.error("Error when instantiating the admin provider object: " + e);
		}catch(Exception e){
			logger.error("Error when establishing connection to the server: " + e);
		}
		queue = new ArrayBlockingQueue<Integer>(1);
		Runnable poller = new MonitorPoller(this.map_queues_mg_count, this.map_topics_mg_count,
				this.map_queues_mg_count_tmp, this.map_topics_mg_count_tmp, this.msp_queues, this.msp_topics,
				this.polling_time_is_sec, queue);
		Runnable ruler = new MonitorRuler(this.map_queues_mg_count, this.map_queues_mg_count_tmp,
				this.map_topics_mg_count, this.map_topics_mg_count_tmp, queue, message_count_threshold, notifier);
		
		new Thread(poller).start();
		new Thread(ruler).start();
	}

	public static void main(String[] args) {
		MonitorAgent mon = new MonitorAgent();
	}
}
