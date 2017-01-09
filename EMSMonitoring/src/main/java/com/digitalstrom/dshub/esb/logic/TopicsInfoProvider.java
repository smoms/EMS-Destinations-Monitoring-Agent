package com.digitalstrom.dshub.esb.logic;

import org.apache.log4j.Logger;

import com.digitalstrom.dshub.esb.contract.IDestinationInfoProvider;
import com.tibco.tibjms.admin.DestinationInfo;
import com.tibco.tibjms.admin.ServerInfo;
import com.tibco.tibjms.admin.TibjmsAdmin;
import com.tibco.tibjms.admin.TibjmsAdminException;

public class TopicsInfoProvider implements IDestinationInfoProvider{
	final static Logger logger = Logger.getLogger("TopicsInfoFactory");
	TibjmsAdmin admin = null;

	public TopicsInfoProvider() throws TibjmsAdminException {
		this.admin = AdminProvider.getInstance().getAdminConnection();
		logger.info("Admin provider object obtained with connectionId: "+ this.admin.getConnectionId());
	}

	public DestinationInfo[] getDestinationsInfo() throws TibjmsAdminException {
		logger.debug("getDestinationsInfo for topics called");
		return this.admin.getTopics();
	}
	
	public DestinationInfo[] getStatisticalDestinationsInfo() throws TibjmsAdminException {
		logger.debug("getStatisticalDestinationsInfo for topics called");
		return this.admin.getTopicsStatistics();
	}

}


