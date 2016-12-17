package com.digitalstrom.dshub.esb.logic;

import org.apache.log4j.Logger;

import com.tibco.tibjms.admin.DestinationInfo;
import com.tibco.tibjms.admin.TibjmsAdmin;
import com.tibco.tibjms.admin.TibjmsAdminException;

public abstract class DestinationsInfoFactory {

	// TODO: add instance of object:
	TibjmsAdmin admin = null;
	final static Logger logger = Logger.getLogger("com.digitalstrom.dshub.esb.logic.DestinationsInfoFactory");

	public DestinationsInfoFactory() throws TibjmsAdminException {
		super();
		this.admin = AdminProvider.getInstance().getAdminConnection();
		logger.info("Admin provider object obtained with connectionId: "+ this.admin.getConnectionId());
	}

	public abstract DestinationInfo[] getDestinationsInfo() throws TibjmsAdminException;

	public abstract DestinationInfo[] getStatisticalDestinationsInfo() throws TibjmsAdminException;

	public static DestinationsInfoFactory getFactory(String factory) throws Exception {
		if (factory == null) {
			return null;
		}
		if (factory.equalsIgnoreCase("QueuesInfoFactory")) {
			return new QueuesInfoFactory();

		} else if (factory.equalsIgnoreCase("TopicsInfoFactory")) {
			return new TopicsInfoFactory();
		}
		return null;
		// return (DestinationsInfoFactory ) Class.forName(f).newInstance();
	}
}
