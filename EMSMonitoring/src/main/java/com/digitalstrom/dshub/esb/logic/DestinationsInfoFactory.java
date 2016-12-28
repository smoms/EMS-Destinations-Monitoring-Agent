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

	public static IDestinationInfoProvider getDestinationProvider(String providerType) throws Exception {
		if (providerType == null) {
			return null;
		}
		if (providerType.equalsIgnoreCase("QueuesInfoProvider")) {
			return new QueuesInfoProvider();

		} else if (providerType.equalsIgnoreCase("TopicsInfoProvider")) {
			return new TopicsInfoProvider();
		}
		return null;
		// return (DestinationsInfoFactory ) Class.forName(f).newInstance();
	}
}
