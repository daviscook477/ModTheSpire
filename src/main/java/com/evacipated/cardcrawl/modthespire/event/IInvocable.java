package com.evacipated.cardcrawl.modthespire.event;

public interface IInvocable {

	void invoke(Event event);
	boolean receiveIfCanceled();
	
}
