package com.digitalstrom.dshub.esb.logic;

import com.digitalstrom.dshub.esb.contract.INotifier;

public class ConsoleNotifierFactory extends NotifierFactory{

	@Override
	public INotifier getNotifier() {
		return new ConsoleNotifier();
	}

}
