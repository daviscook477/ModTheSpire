package com.evacipated.cardcrawl.modthespire.event;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class EventListener {

	private Map<Integer, Set<Invocable>> subscribers;
	
	public EventListener() {
		subscribers = new TreeMap<>();
	}
	
	public void addSubscriber(Invocable invocable, int priority) {
		if (!subscribers.containsKey(priority)) {
			subscribers.put(priority, new HashSet<>());
		}
		
		subscribers.get(priority).add(invocable);
	}
	
	public boolean removeSubscriberByParent(Object parent) {
		boolean removed = false;
		
		for (Integer key : subscribers.keySet()) {
			Iterator<Invocable> iter = subscribers.get(key).iterator();
			while (iter.hasNext()) {
				if (iter.next().getParent() == parent) {
					iter.remove();
					removed = true;
				}
			}
		}
		
		return removed;
	}
	
	public boolean removeSubscriber(Invocable invocable) {
		boolean removed = false;
		
		for (Integer key : subscribers.keySet()) {
			if (removeSubscriber(invocable, key)) removed = true;
		}
		
		return removed;
	}
	
	public boolean removeSubscriber(Invocable invocable, int priority) {
		if (!subscribers.containsKey(priority)) return false;
		
		boolean removed = false;
		
		while (subscribers.get(priority).contains(invocable)) {
			if (subscribers.get(priority).remove(invocable)) removed = true;
		}
		
		return removed;
	}
	
	public void invokeAll(Event event) throws EventProcessingException {
		for (Map.Entry<Integer, Set<Invocable>> entry : subscribers.entrySet()) {
			for (Invocable invocable : entry.getValue()) {
				if (!event.isCancelled() || invocable.receiveIfCanceled()) invocable.invoke(event);
			}
		}
	}
	
}
