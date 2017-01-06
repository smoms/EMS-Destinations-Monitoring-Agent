package com.digitalstrom.dshub.esb.contract;

import java.util.Map;

import com.tibco.tibjms.admin.TibjmsAdminException;

public interface IMonitorStatisticsProvider {

	public Map<String, Long> getDestinationsPendingMessageCount() throws TibjmsAdminException;
	public Map<String, Long> getDestinationsMessageSize() throws TibjmsAdminException;
}