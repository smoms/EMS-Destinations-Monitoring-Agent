package com.digitalstrom.dshub.esb.contract;

import java.util.Map;

public interface INotifier {

	public void SendNotification(Map notificationBacklog, String title, String env, String msgBody, String receivers);
}
