package com.digitalstrom.dshub.esb.contract;

import com.tibco.tibjms.admin.TibjmsAdmin;

public interface IAdminProvider {

	public TibjmsAdmin getAdminConnection();
}



