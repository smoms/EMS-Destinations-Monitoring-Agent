package com.digitalstrom.dshub.esb.logic;

import java.util.Map;

import org.apache.log4j.Logger;

import com.digitalstrom.dshub.esb.contract.INotifier;

public class ConsoleNotifier implements INotifier {

	final static Logger logger = Logger.getLogger("ConsoleNotifier");

	public void SendNotification(Map notificationBacklog, Map notificationDeltaBacklog, String title, 
			String env) {

		Map<String, String> map = notificationBacklog;
		Map<String, String> mapDelta = notificationDeltaBacklog;
		logger.info("The following destinations have high pending backlog:");
		for (Map.Entry<String, String> entry : map.entrySet()) {
			logger.info(entry.getKey() + " <--> " + entry.getValue());
		}
		logger.info("The following destinations are increasing the pending backlog:");
		for (Map.Entry<String, String> entry : mapDelta.entrySet()) {
			logger.info(entry.getKey() + " <--> " + entry.getValue());
		}
		logger.info("Notification sent. ");
		logger.info("Title: " + title);
		logger.info("Message Destination Backlog: " + notificationBacklog.toString());
		logger.info("Message Destination Delta: " + notificationDeltaBacklog.toString());
	}
}
