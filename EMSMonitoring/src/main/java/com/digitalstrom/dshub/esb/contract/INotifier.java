package com.digitalstrom.dshub.esb.contract;

public interface INotifier {

	public void SendNotification(String title, String env, String msgBody, String receivers);
}
