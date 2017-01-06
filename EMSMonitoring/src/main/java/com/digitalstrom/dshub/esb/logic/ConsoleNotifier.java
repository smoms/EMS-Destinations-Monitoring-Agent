package com.digitalstrom.dshub.esb.logic;

import java.util.Map;

import org.apache.log4j.Logger;

import com.digitalstrom.dshub.esb.contract.INotifier;

public class ConsoleNotifier implements INotifier {

	final static Logger logger = Logger.getLogger("ConsoleNotifier");

	public void SendNotification(Map notificationBacklog, boolean isDelatBacklog, String title, String env) {

		logger.info("Title: " + title);
		if (notificationBacklog != null)
			if (isDelatBacklog)
				logger.info("Message Destination Delta Backlog: " + notificationBacklog.toString());
			else
				logger.info("Message Destination Backlog: " + notificationBacklog.toString());
		logger.info("Notification sent. ");
	}

}
