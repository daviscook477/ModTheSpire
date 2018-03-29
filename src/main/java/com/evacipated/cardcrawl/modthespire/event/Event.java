package com.evacipated.cardcrawl.modthespire.event;

/**
 * superclass of all events
 */
public class Event {

	public static final boolean DEFAULT_CANCELED = false;
	
	public static class EventInfo {
		
		public void setupEvent() {};
		
	}
	
	public static EventInfo eventInfo = new EventInfo();
	
	private EventListener subscribers;
	private boolean isCancelled;
	
	public Event() {
		this(DEFAULT_CANCELED);
	}
	
	public Event(boolean isCancelled) {
		this.isCancelled = isCancelled;
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
