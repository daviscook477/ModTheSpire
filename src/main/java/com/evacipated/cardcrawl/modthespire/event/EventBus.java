package com.evacipated.cardcrawl.modthespire.event;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class EventBus {

	private List<EventListener> listeners;
	
	public EventBus() {
		listeners = new LinkedList<>();
	}
	
	public void setupListeners(Set<Class<? extends Event>> events) {
		
	}
	
	public void register(Object object) {
		
	}
	
	public void register(Class<? extends Event> eventType, Object target, Method method, Object owner) {
		
	}
	
	public void unregister(Object object) {
		Iterator<EventListener> iter = listeners.iterator();
		while (iter.hasNext()) {
			if (iter.next().getParent() == object) {
				iter.remove();
			}
		}
	}
	
	public boolean post(Event event) {
		event.getSubscribers().invokeAll(event);
		return event.isCancelled();
	}
	
}
