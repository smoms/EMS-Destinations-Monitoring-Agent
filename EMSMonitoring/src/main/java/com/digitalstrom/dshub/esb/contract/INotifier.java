package com.digitalstrom.dshub.esb.contract;

import java.util.Map;

public interface INotifier {

	public void SendNotification(Map notificationBacklog, boolean isDelataBacklog, String title, String env);
}
