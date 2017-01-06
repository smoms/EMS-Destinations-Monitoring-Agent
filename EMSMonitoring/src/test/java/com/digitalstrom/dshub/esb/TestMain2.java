package com.digitalstrom.dshub.esb;

import java.util.Arrays;
import java.util.Map;

import com.digitalstrom.dshub.esb.contract.IMonitorStatisticsProvider;
import com.digitalstrom.dshub.esb.logic.MonitorStatisticsProvider;
import com.tibco.tibjms.admin.TibjmsAdminException;

public class TestMain2 {

	public static void main(String[] args) throws TibjmsAdminException {

		IMonitorStatisticsProvider eng = null;
		Map map_count = null;
		Map map_size = null;

		try {
			eng = new MonitorStatisticsProvider("QueuesInfoFactory");
		} 
		catch (Exception e) {
			System.out.println("error"+e.getMessage());
		}
		map_count = eng.getDestinationsPendingMessageCount();
		map_size = eng.getDestinationsMessageSize();
		System.out.println(Arrays.toString(map_count.entrySet().toArray()));
		System.out.println(Arrays.toString(map_size.entrySet().toArray()));
		
	}
}
