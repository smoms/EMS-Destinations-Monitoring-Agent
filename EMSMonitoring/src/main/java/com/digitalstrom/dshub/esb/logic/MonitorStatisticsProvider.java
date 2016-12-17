package com.digitalstrom.dshub.esb.logic;

import java.util.HashMap;
import java.util.Map;

import com.digitalstrom.dshub.esb.contract.IMonitorStatisticsProvider;
import com.tibco.tibjms.admin.DestinationInfo;
import com.tibco.tibjms.admin.TibjmsAdminException;

public class MonitorStatisticsProvider implements IMonitorStatisticsProvider {

	DestinationInfo[] destInfo = null;
	DestinationsInfoFactory desFac = null;

	public MonitorStatisticsProvider(String destInfo) throws TibjmsAdminException, Exception {
		super();
		this.desFac = DestinationsInfoFactory.getFactory(destInfo);
		this.destInfo = desFac.getDestinationsInfo();
	}

	public Map<String, Long> getDestinationsPendingMessageCount() {
		Map<String, Long> map = new HashMap();

		for (DestinationInfo des : destInfo) {
			map.put(des.getName(), des.getPendingMessageCount());
		}
		return map;
	}

	public Map<String, Long> getDestinationsMessageSize() {
		Map<String, Long> map = new HashMap();

		for (DestinationInfo des : destInfo) {
			map.put(des.getName(), des.getPendingMessageSize());
		}
		return map;
	}

}
