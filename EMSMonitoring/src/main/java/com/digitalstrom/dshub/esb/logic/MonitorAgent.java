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
	int polling_time_is_sec = 10;
	private int message_count_threshold = 100;
	
	final static Logger logger = Logger.getLogger("MonitorAgent");
	private IMonitorStatisticsProvider msp_queues = null;
	private IMonitorStatisticsProvider msp_topics = null;
	Runnable poller = null;
	
	public MonitorAgent() {
		super();
		try {
			logger.info("Establishing connection to the server..");
			msp_queues = new MonitorStatisticsProvider("QueuesInfoFactory");
			msp_topics = new MonitorStatisticsProvider("TopicsInfoFactory");
			poller = new MonitorPoller(this.msp_queues, this.msp_topics, this.polling_time_is_sec, this.message_count_threshold);
		}catch (TibjmsAdminException e) {
			logger.error("Error when instantiating the admin provider object: " + e);
		}catch(Exception e){
			logger.error("Error when establishing connection to the server: " + e);
		}
		new Thread(poller).start();
		logger.info("Poller started.");
	}

	public static void main(String[] args) {
		MonitorAgent mon = new MonitorAgent();
		logger.debug("Main method is going to die.");
	}
}
