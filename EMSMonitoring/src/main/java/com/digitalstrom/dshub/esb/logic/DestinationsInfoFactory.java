package com.digitalstrom.dshub.esb.logic;

import org.apache.log4j.Logger;

import com.digitalstrom.dshub.esb.contract.IDestinationInfoProvider;
import com.tibco.tibjms.admin.DestinationInfo;
import com.tibco.tibjms.admin.TibjmsAdminException;

public abstract class DestinationsInfoFactory {

	final static Logger logger = Logger.getLogger("com.digitalstrom.dshub.esb.logic.DestinationsInfoFactory");

	public DestinationsInfoFactory() throws TibjmsAdminException {
		super();
	}

	public abstract DestinationInfo[] getDestinationsInfo() throws TibjmsAdminException;

	public abstract DestinationInfo[] getStatisticalDestinationsInfo() throws TibjmsAdminException;

	public static IDestinationInfoProvider getFactory(String factory) throws Exception {
		if (factory == null) {
			return null;
		}
		if (factory.equalsIgnoreCase("QueuesInfoFactory")) {
			return new QueuesInfoProvider();

		} else if (factory.equalsIgnoreCase("TopicsInfoFactory")) {
			return new TopicsInfoProvider();
		}
		return null;
		// return (DestinationsInfoFactory ) Class.forName(f).newInstance();
	}
}
