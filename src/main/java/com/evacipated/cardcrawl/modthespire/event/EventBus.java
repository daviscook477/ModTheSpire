package com.evacipated.cardcrawl.modthespire.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.evacipated.cardcrawl.modthespire.lib.EventHandler;

public class EventBus {

	private Map<Class<? extends Event>, EventListener> listeners;
	
	public EventBus() {
		listeners = new HashMap<>();
	}
	
	public void setupListeners(Set<Class<? extends Event>> events) {
		for (Class<? extends Event> event : events) {
			listeners.put(event, new EventListener());
		}
	}
	
	public Method[] getStaticMethods(Method[] allMethods, boolean getStatic) {
		List<Method> matchedMethods = new ArrayList<>();
		for (int i = 0; i< allMethods.length; i++) {
			Method curr = allMethods[i];
			if (Modifier.isStatic(curr.getModifiers()) == getStatic) {
				matchedMethods.add(curr);
			}
		}
		Method[] retArr = new Method[matchedMethods.size()];
		return matchedMethods.toArray(retArr);
	}
	
	/**
	 * register all static methods on clazz
	 */
	public void register(Class clazz) {
		Method[] methods = clazz.getMethods();
		
		// get static methods
		Method[] validMethods = getStaticMethods(methods, true);
	}
	
	/**
	 * register all non static methods on object
	 */
	public void register(Object object) throws EventRegistrationException {
		Method[] methods = object.getClass().getMethods();
		
		// get non static methods
		Method[] validMethods = getStaticMethods(methods, false);
		
		for (Method method : validMethods) {
			Class<?>[] parameters = method.getParameterTypes();
			
			// validate parameter size
			if (parameters.length != 1) {
				StringBuilder classNames = new StringBuilder("");
				classNames.append(method.getName());
				classNames.append(" had ");
				for (int i = 0; i < parameters.length; i++) {
					classNames.append(parameters[i].getName());
					classNames.append(" ");
				}
				if (parameters.length <= 0) {
					classNames.append("0 parameters");
				} else {
					classNames.append("as its parameter types");
				}
				
				throw new EventRegistrationException("event methods must have ONLY 1 parameter, " + classNames.toString());
			}
			
			if (!Event.class.isAssignableFrom(parameters[0])) {
				throw new EventRegistrationException("the parameter to an event method must be a subclass of Event");
			}
			
			register(parameters[0].asSubclass(Event.class), object, method);
		}
	}
	
	public void register(Class<? extends Event> eventType, Object owner, Method method) throws EventRegistrationException {
		EventListener listener = listeners.get(eventType);
		
		// checks to make sure there aren't problems in event registration
		if (Modifier.isStatic(method.getModifiers()) && owner != null) throw new EventRegistrationException("static event methods cannot be registered to an owner, provided owner was: " + owner.getClass().getName());
		
		if (!Modifier.isStatic(method.getModifiers()) && owner == null) throw new EventRegistrationException("non static event methods must be registered to an owner, provided owner was: null");
		
		if (listener == null) throw new EventRegistrationException("could not register event for type: " + eventType.getName() + " because it does not have a corresponding listener");
		
		EventHandler annotation = method.getAnnotation(EventHandler.class);
		
		if (annotation == null) throw new EventRegistrationException("method provided for registered event must have @EventHandler annotation provided, " + method.getName() + " did not");
		
		// get annotated properties
		int priority = annotation.priority();
		boolean receiveIfCanceled = annotation.receiveIfCanceled();
		
		Invocable toInvoke = new Invocable(owner) {

			@Override
			public void invoke(Event event) throws EventProcessingException {
				try {
					method.invoke(owner, event);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new EventProcessingException(e, owner, event, eventType);
				}
			}

			@Override
			public boolean receiveIfCanceled() {
				return receiveIfCanceled;
			}
			
		};
		
		listener.addSubscriber(toInvoke, priority);
	}
	
	public void unregister(Object object) {
		Iterator<EventListener> iter = listeners.values().iterator();
		while (iter.hasNext()) {
			iter.next().removeSubscriberByParent(object);
		}
	}
	
	public boolean post(Event event) {
		try {
			event.getSubscribers().invokeAll(event);
		} catch (EventProcessingException e) {
			// TODO: log relevant info relevant to event call exception
		}
		return event.isCancelled();
	}
	
}
