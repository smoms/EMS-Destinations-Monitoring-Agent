package com.digitalstrom.dshub.esb.logic;

import org.apache.log4j.Logger;

import com.tibco.tibjms.admin.DestinationInfo;
import com.tibco.tibjms.admin.TibjmsAdminException;

public class QueuesInfoFactory extends DestinationsInfoFactory {
	final static Logger logger = Logger.getLogger("QueuesInfoFactory");

	public QueuesInfoFactory() throws TibjmsAdminException {
		super();
	}

	@Override
	public DestinationInfo[] getDestinationsInfo() throws TibjmsAdminException {
		// this.admin = new ConfigFactory().getAdminConnection();
		logger.info("getDestinationsInfo for queues called");
		return this.admin.getQueues();
	}

	@Override
	public DestinationInfo[] getStatisticalDestinationsInfo() throws TibjmsAdminException {
		// this.admin = new ConfigFactory().getAdminConnection();
		logger.info("getStatisticalDestinationsInfo for queues called");
		return this.admin.getQueuesStatistics();
	}

}
