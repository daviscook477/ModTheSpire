package com.evacipated.cardcrawl.modthespire.event;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class EventListener {

	private Object parent;
	private Map<Integer, Set<IInvocable>> subscribers;
	
	public EventListener(Object parent) {
		subscribers = new TreeMap<>();
		this.parent = parent;
	}
	
	public Object getParent() {
		return parent;
	}
	
	public void addSubscriber(IInvocable invocable, int priority) {
		if (!subscribers.containsKey(priority)) {
			subscribers.put(priority, new HashSet<>());
		}
		
		subscribers.get(priority).add(invocable);
	}
	
	public boolean removeSubscriber(IInvocable invocable) {
		for (Integer key : subscribers.keySet()) {
			if (removeSubscriber(invocable, key)) return true;
		}
		
		return false;
	}
	
	public boolean removeSubscriber(IInvocable invocable, int priority) {
		if (!subscribers.containsKey(priority)) return false;
		
		return subscribers.get(priority).contains(invocable);
	}
	
	public void invokeAll(Event event) {
		for (Map.Entry<Integer, Set<IInvocable>> entry : subscribers.entrySet()) {
			for (IInvocable invocable : entry.getValue()) {
				if (!event.isCancelled() || invocable.receiveIfCanceled()) invocable.invoke(event);
			}
		}
	}
	
}
