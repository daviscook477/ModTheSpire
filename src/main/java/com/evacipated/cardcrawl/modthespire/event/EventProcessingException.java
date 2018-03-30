package com.evacipated.cardcrawl.modthespire.event;

import com.evacipated.cardcrawl.modthespire.lib.Event;

public class EventProcessingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7462939639867373523L;
	
	public Exception originalException;
	public Object owner;
	public Event failedEvent;
	public Class<? extends Event> expectedType;
	
	public EventProcessingException(Exception e, Object owner, Event failedEvent, Class<? extends Event> expectedType) {
		this.originalException = e;
		this.owner = owner;
		this.failedEvent = failedEvent;
		this.expectedType = expectedType;
	}
	
	@Override
	public String toString() {
		originalException.printStackTrace();
		return originalException.getClass().getName() + ": " + originalException.toString() + ", owner: " + 
				owner + ", event: " + failedEvent.toString() + ", expected type: " + expectedType.getName(); 
	}
	
}
