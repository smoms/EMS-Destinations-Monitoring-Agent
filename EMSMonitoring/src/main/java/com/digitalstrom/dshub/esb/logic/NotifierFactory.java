package com.digitalstrom.dshub.esb.logic;

import org.apache.log4j.Logger;

import com.digitalstrom.dshub.esb.contract.INotifier;

public abstract class NotifierFactory {
	
	public abstract INotifier getNotifier();

	public static NotifierFactory getFactory(String factory) throws Exception {
		if (factory == null) {
			return null;
		}
		if (factory.equalsIgnoreCase("EmailNotifierFactory")) {
			return null;

		} else if (factory.equalsIgnoreCase("ConsoleNotifierFactory")) {
			return new ConsoleNotifierFactory();
		}
		return null;
		// return (DestinationsInfoFactory ) Class.forName(f).newInstance();
	}
}
