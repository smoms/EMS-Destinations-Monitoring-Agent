package com.digitalstrom.dshub.esb.logic;

import org.apache.log4j.Logger;

import com.digitalstrom.dshub.esb.contract.IDestinationInfoProvider;
import com.tibco.tibjms.admin.DestinationInfo;
import com.tibco.tibjms.admin.TibjmsAdmin;
import com.tibco.tibjms.admin.TibjmsAdminException;

public class QueuesInfoProvider implements IDestinationInfoProvider{
	final static Logger logger = Logger.getLogger("QueuesInfoProvider");
	TibjmsAdmin admin = null;

	public QueuesInfoProvider() throws TibjmsAdminException {
		this.admin = AdminProvider.getInstance().getAdminConnection();
		logger.info("Admin provider object obtained with connectionId: "+ this.admin.getConnectionId());
	}

	public DestinationInfo[] getDestinationsInfo() throws TibjmsAdminException {
		logger.debug("getDestinationsInfo for queues called");
		return this.admin.getQueues();
	}

	public DestinationInfo[] getStatisticalDestinationsInfo() throws TibjmsAdminException {
		// this.admin = new ConfigFactory().getAdminConnection();
		logger.debug("getStatisticalDestinationsInfo for queues called");
		return this.admin.getQueuesStatistics();
	}

}
