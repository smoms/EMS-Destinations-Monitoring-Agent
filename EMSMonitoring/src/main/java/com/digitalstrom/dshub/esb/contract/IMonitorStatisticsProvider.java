package com.digitalstrom.dshub.esb.contract;

import java.util.Map;

public interface IMonitorStatisticsProvider {

	public Map<String, Long> getDestinationsPendingMessageCount();
	public Map<String, Long> getDestinationsMessageSize();
}
