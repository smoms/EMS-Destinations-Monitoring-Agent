package com.digitalstrom.dshub.esb.logic;

import com.digitalstrom.dshub.esb.contract.INotifier;

public class ConsoleNotifier implements INotifier {

	public void SendNotification(String title, String env, String msgBody, String receivers) {

		System.out.println("Notification sent to receivers: "+receivers);
		System.out.println("Environment: "+receivers);
		System.out.println("Title: "+title);
		System.out.println("Message: "+msgBody);
	}

}
