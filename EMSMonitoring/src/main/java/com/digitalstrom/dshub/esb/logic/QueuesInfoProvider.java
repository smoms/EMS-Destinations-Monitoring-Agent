package com.digitalstrom.dshub.esb.logic;

import org.apache.log4j.Logger;

import com.digitalstrom.dshub.esb.contract.IDestinationInfoProvider;
import com.tibco.tibjms.admin.DestinationInfo;
import com.tibco.tibjms.admin.ServerInfo;
import com.tibco.tibjms.admin.TibjmsAdmin;
import com.tibco.tibjms.admin.TibjmsAdminException;

public class QueuesInfoProvider implements IDestinationInfoProvider{
	final static Logger logger = Logger.getLogger("QueuesInfoProvider");
	private TibjmsAdmin admin = null;

	public QueuesInfoProvider() throws TibjmsAdminException {
		try {
			this.admin = AdminProvider.getInstance().getAdminConnection();
			//test connection:
			this.admin.getSystemConnections();
		} catch (TibjmsAdminException e) {
			e.printStackTrace();
			logger.error("Error while getting server connection. Connection re-try..");
			AdminProvider.resetInstance();
			this.admin = AdminProvider.getInstance().getAdminConnection();
		}
		logger.info("connectionId: "+ this.admin.getConnectionId());
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
