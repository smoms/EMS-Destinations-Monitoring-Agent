package com.digitalstrom.dshub.esb.logic;

import org.apache.log4j.Logger;

import com.digitalstrom.dshub.esb.contract.INotifier;

public abstract class NotifierFactory {

	public abstract INotifier getNotifier();

	public static NotifierFactory getFactory(String factory) throws Exception {
		if (factory == null) {
			return null;
		}
		if (factory.equalsIgnoreCase("email"))
			return new EmailNotifierFactory();

		return new ConsoleNotifierFactory(); // default

	}

}
