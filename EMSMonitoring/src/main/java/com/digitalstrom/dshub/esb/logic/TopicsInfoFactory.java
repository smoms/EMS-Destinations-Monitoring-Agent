package com.digitalstrom.dshub.esb.logic;

import org.apache.log4j.Logger;

import com.tibco.tibjms.admin.DestinationInfo;
import com.tibco.tibjms.admin.TibjmsAdminException;

public class TopicsInfoFactory extends DestinationsInfoFactory{
	final static Logger logger = Logger.getLogger("TopicsInfoFactory");

	public TopicsInfoFactory() throws TibjmsAdminException {
		super();
	}

	@Override
	public DestinationInfo[] getDestinationsInfo() throws TibjmsAdminException {
		logger.info("getDestinationsInfo for topics called");
		return this.admin.getTopics();
	}
	
	public DestinationInfo[] getStatisticalDestinationsInfo() throws TibjmsAdminException {
		logger.info("getStatisticalDestinationsInfo for topics called");
		return this.admin.getTopicsStatistics();
	}


}


