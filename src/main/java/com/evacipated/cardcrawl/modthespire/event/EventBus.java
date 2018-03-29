package com.evacipated.cardcrawl.modthespire.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.evacipated.cardcrawl.modthespire.ReflectionHelper;
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
	
	private Map<String, Invocable> registerMethods(Object object, Method[] methods, boolean ignoreAutomatic) throws EventRegistrationException {
		Map<String, Invocable> retMap = new HashMap<>();
		
		for (Method method : methods) {
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
			
			Invocable invocable = register(parameters[0].asSubclass(Event.class), object, method, ignoreAutomatic);
			
			retMap.put(method.getName(), invocable);
		}
		
		return retMap;
	}
	
	/**
	 * register all static methods on clazz
	 */
	public Map<String, Invocable> register(Class<?> clazz) throws EventRegistrationException {
		return this.register(clazz, true);
	}
	
	public Map<String, Invocable> register(Class<?> clazz, boolean ignoreAutomatic) throws EventRegistrationException {
		Method[] methods = clazz.getMethods();
		
		// get static methods
		Method[] validMethods = ReflectionHelper.getStaticMethods(methods, true);
		
		// get methods with proper annotation
		Method[] annotatedMethods = ReflectionHelper.getAnnotatedMethods(validMethods, EventHandler.class);
		
		return registerMethods(clazz, annotatedMethods, ignoreAutomatic);
	}
	
	/**
	 * register all non static methods on object
	 */
	public Map<String, Invocable> register(Object object) throws EventRegistrationException {
		Method[] methods = object.getClass().getMethods();
		
		// get non static methods
		Method[] validMethods = ReflectionHelper.getStaticMethods(methods, false);
		
		// get methods with proper annotation
		Method[] annotatedMethods = ReflectionHelper.getAnnotatedMethods(validMethods, EventHandler.class);
		
		return registerMethods(object, annotatedMethods, true);
	}
	
	public Invocable register(Class<? extends Event> eventType, Object owner, Method method, boolean ignoreAutomatic) throws EventRegistrationException {
		EventListener listener = listeners.get(eventType);
		
		// checks to make sure there aren't problems in event registration
		if (Modifier.isStatic(method.getModifiers()) && owner != null) throw new EventRegistrationException("static event methods cannot be registered to an owner, provided owner was: " + owner.getClass().getName());
		
		if (!Modifier.isStatic(method.getModifiers()) && owner == null) throw new EventRegistrationException("non static event methods must be registered to an owner, provided owner was: null");
		
		if (listener == null) throw new EventRegistrationException("could not register event for type: " + eventType.getName() + " because it does not have a corresponding listener");
		
		EventHandler annotation = method.getAnnotation(EventHandler.class);
		
		if (annotation == null) throw new EventRegistrationException("method provided for registered event must have @EventHandler annotation provided, " + method.getName() + " did not");
		
		if (!annotation.automatic() && !ignoreAutomatic) return null;
		
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
		
		return toInvoke;
	}
	
	public void unregister(Invocable invocable) {
		Iterator<EventListener> iter = listeners.values().iterator();
		while (iter.hasNext()) {
			iter.next().removeSubscriber(invocable);
		}
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
