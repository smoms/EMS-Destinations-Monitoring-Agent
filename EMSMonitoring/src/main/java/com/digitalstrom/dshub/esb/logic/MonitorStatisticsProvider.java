package com.digitalstrom.dshub.esb.logic;

import java.util.HashMap;
import java.util.Map;

import com.digitalstrom.dshub.esb.contract.IDestinationInfoProvider;
import com.digitalstrom.dshub.esb.contract.IMonitorStatisticsProvider;
import com.tibco.tibjms.admin.DestinationInfo;
import com.tibco.tibjms.admin.TibjmsAdminException;

public class MonitorStatisticsProvider implements IMonitorStatisticsProvider {

	DestinationInfo[] destInfo = null;
	IDestinationInfoProvider desProvider = null;

	public MonitorStatisticsProvider(String destInfo) throws TibjmsAdminException, Exception {
		super();
		this.desProvider = DestinationsInfoFactory.getDestinationProvider(destInfo);
		//this.destInfo = desProvider.getDestinationsInfo();
	}

	public Map<String, Long> getDestinationsPendingMessageCount() throws TibjmsAdminException {
		Map<String, Long> map = new HashMap();
		this.destInfo = desProvider.getDestinationsInfo();

		for (DestinationInfo des : destInfo) {
			if(!des.getName().startsWith("$sys."))
				map.put(des.getName(), des.getPendingMessageCount());
		}
		return map;
	}

	public Map<String, Long> getDestinationsMessageSize() throws TibjmsAdminException {
		Map<String, Long> map = new HashMap();
		this.destInfo = desProvider.getDestinationsInfo();

		for (DestinationInfo des : destInfo) {
			if(!des.getName().startsWith("$sys."))
				map.put(des.getName(), des.getPendingMessageSize());
		}
		return map;
	}

}
