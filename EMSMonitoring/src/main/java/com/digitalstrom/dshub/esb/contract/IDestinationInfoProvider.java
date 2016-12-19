package com.digitalstrom.dshub.esb.contract;

import com.tibco.tibjms.admin.DestinationInfo;
import com.tibco.tibjms.admin.TibjmsAdminException;

public interface IDestinationInfoProvider {

	public DestinationInfo[] getDestinationsInfo() throws TibjmsAdminException;
	public DestinationInfo[] getStatisticalDestinationsInfo() throws TibjmsAdminException;
}
