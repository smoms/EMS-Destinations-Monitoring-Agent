package com.digitalstrom.dshub.esb.contract;

import java.util.Map;

public interface INotifier {

	public void SendNotification(Map notificationBacklog, Map notificationDeltaBacklog, String title, String env);
}
