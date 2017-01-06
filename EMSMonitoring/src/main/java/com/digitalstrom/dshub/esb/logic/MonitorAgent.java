package com.digitalstrom.dshub.esb.logic;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.apache.log4j.Logger;

import com.digitalstrom.dshub.esb.contract.IMonitorStatisticsProvider;
import com.digitalstrom.dshub.esb.contract.INotifier;
import com.digitalstrom.dshub.esb.util.ReadConfigs;
import com.tibco.tibjms.admin.TibjmsAdmin;
import com.tibco.tibjms.admin.TibjmsAdminException;

public class MonitorAgent {

	int polling_time_is_sec;
	private int message_count_threshold;
	
	final static Logger logger = Logger.getLogger("MonitorAgent");
	private IMonitorStatisticsProvider msp_queues = null;
	private IMonitorStatisticsProvider msp_topics = null;
	Runnable poller = null;
	private Map<String, String> map = null;
	
	public MonitorAgent() {
		super();
		try {
			logger.info("Establishing connection to the server..");
			this.map = ReadConfigs.getInstance();
			this.polling_time_is_sec = Integer.parseInt(map.get("serverpollingtimeinsec")); 
			this.message_count_threshold = Integer.parseInt(map.get("messagecountthreshold"));
			
			msp_queues = new MonitorStatisticsProvider("QueuesInfoProvider");
			msp_topics = new MonitorStatisticsProvider("TopicsInfoProvider");
			poller = new MonitorPoller(this.msp_queues, this.msp_topics, this.polling_time_is_sec, this.message_count_threshold);
		}catch (NumberFormatException e) {
			logger.error("Error converting from String to int type. Hint: can be wrong 'serverPollingTimeInSec' or 'messageCountThreshold'. Details: " + e);
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
		logger.debug("Main is going to die.");
	}
}
