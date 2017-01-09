package com.digitalstrom.dshub.esb.logic;

import org.apache.log4j.Logger;

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
