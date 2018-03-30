package com.evacipated.cardcrawl.modthespire.lib;

import com.evacipated.cardcrawl.modthespire.event.EventListener;

/**
 * superclass of all events
 */
public class Event {

	public static final boolean DEFAULT_CANCELED = false;

	private EventListener subscribers;
	private boolean isCancelled;
	
	public Event() {
		this(DEFAULT_CANCELED);
	}
	
	public Event(boolean isCancelled) {
		this.isCancelled = isCancelled;
		
		subscribers = ModTheSpire.EVENT_BUS.getListener(this.getClass());
	}
	
	public boolean isCancelled() {
		return isCancelled;
	}
	
	public void setCancelled(boolean val) {
		this.isCancelled = val;
	}
	
	public EventListener getSubscribers() {
		return subscribers;
	}
	
}
