package com.digitalstrom.dshub.esb.logic;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.apache.log4j.Logger;

import com.digitalstrom.dshub.esb.contract.IAdminProvider;
import com.digitalstrom.dshub.esb.contract.IMonitorStatisticsProvider;
import com.digitalstrom.dshub.esb.contract.INotifier;
import com.digitalstrom.dshub.esb.util.ReadConfigs;
import com.tibco.tibjms.admin.TibjmsAdmin;
import com.tibco.tibjms.admin.TibjmsAdminException;

public class EMSMonitoringAgent {

	final static Logger logger = Logger.getLogger("MonitorAgent");
	Runnable poller = null;

	public EMSMonitoringAgent() {
		super();
		try {
			poller = new MonitorPoller();
			new Thread(poller).start();
			logger.info("Poller started");
		} catch (Exception e) {
			logger.error("Error running the Poller: " + e);
		}
	}

	public static void main(String[] args) {
		EMSMonitoringAgent mon = new EMSMonitoringAgent();
		logger.debug("Useless main thread is going to die");
	}
}
