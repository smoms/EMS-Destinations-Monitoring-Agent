package com.digitalstrom.dshub.esb.logic;

import java.util.Map;

import org.apache.log4j.Logger;

import com.digitalstrom.dshub.esb.contract.IAdminProvider;
import com.digitalstrom.dshub.esb.util.ReadConfigs;
import com.tibco.tibjms.admin.TibjmsAdmin;
import com.tibco.tibjms.admin.TibjmsAdminException;

public class AdminProvider implements IAdminProvider {

	private TibjmsAdmin admin = null;
	private Map<String, String> map = null;
	final static Logger logger = Logger.getLogger("AdminProvider");

	private static volatile IAdminProvider instance;

	private AdminProvider() {
			try {
				this.map = ReadConfigs.getInstance();
				this.admin = new TibjmsAdmin(map.get("serverurl"), map.get("username"), map.get("password"));
				logger.info("ConfigProvider instance created");
			} catch (Exception e) {
				logger.error("Error in AdminProvider class while creating the instance");
				e.printStackTrace();
				throw new RuntimeException("Error in AdminProvider class while creating the instance");
			}
	}

	//Singleton
	public static IAdminProvider getInstance() throws TibjmsAdminException {
	    if (instance == null) {
	      synchronized(AdminProvider.class){
	        if (instance == null) {
	        	instance = new AdminProvider();
	        }
	      }
	    }
	    return instance;
	  }

	public TibjmsAdmin getAdminConnection() {
		return admin;
	}

}
