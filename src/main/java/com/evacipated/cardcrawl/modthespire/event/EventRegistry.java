package com.evacipated.cardcrawl.modthespire.event;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.event.Event.EventInfo;
import com.evacipated.cardcrawl.modthespire.filters.Finder;
import com.evacipated.cardcrawl.modthespire.filters.SuperClassNameFilter;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

public class EventRegistry {

	private static class EventFilter extends SuperClassNameFilter {
		
		public EventFilter() {
			super("com.evaciptaed.cardcrawl.modthespire.event.Event");
		}
		
	}
	
	public static final Set<Class<? extends Event>> events = new HashSet<>();
	
	private EventRegistry() {}
	
	public static List<Iterable<String>> findEvents(ModInfo[] modInfos) throws IOException, URISyntaxException {
		URL[] urls = new URL[modInfos.length];
        for (int i = 0; i < modInfos.length; i++) {
            urls[i] = modInfos[i].jarURL;
        }
        return findEvents(urls, modInfos);
	}
	
	public static List<Iterable<String>> findEvents(URL[] urls, ModInfo[] modInfos) throws IOException, URISyntaxException {
		List<Iterable<String>> eventSetList = new ArrayList<>();
		for (int i = 0; i < urls.length; i++) {
			if (modInfos == null || modInfos[i].MTS_Version.compareTo(Loader.MTS_VERSION) <= 0) {
				URL[] singleURL = new URL[] {urls[i]};
				List<String> foundEvents = Finder.find(new EventFilter(), singleURL);
				eventSetList.add(foundEvents);
	        } else {
	            String str = "ERROR: " + modInfos[i].Name + " requires ModTheSpire v" + modInfos[i].MTS_Version.get() + " or greater!";
	            System.out.println(str);
	            JOptionPane.showMessageDialog(null, str);
	        }
		}
		return eventSetList;
	}
	
	public static void registerEvents(ClassLoader loader, ClassPool pool, List<Iterable<String>> classNames) throws NotFoundException, CannotCompileException {
		for (Iterable<String> iter : classNames) {
			registerEvents(loader, pool, iter);
		}
	}
	
	public static boolean registerEvents(ClassLoader loader, ClassPool pool, Iterable<String> classNames) throws NotFoundException, CannotCompileException {
		if (classNames == null) return false;
		
		for (String className : classNames) {
			CtClass ctEventClass = pool.get(className);
			if (Event.class.isAssignableFrom(ctEventClass.getClass())) {
				events.add(ctEventClass.getClass().asSubclass(Event.class));
			} else {
				throw new IllegalArgumentException("registerEvents must be passed ONLY classes that are subclasses of Event: " + className + " was not.");
			}
		}
		
		return true;
	}
	
	private static void debugPrint(Class<? extends Event> clazz) {
		
	}
	
	public static void finalizeEvents(ClassLoader loader) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		if (Loader.DEBUG) {
			System.out.println();
		}
		for (Class<? extends Event> clazz : events) {
			if (Loader.DEBUG) {
				debugPrint(clazz);
			}
			Field eventInfoField = clazz.getField("eventInfo");
			EventInfo eventInfo = (EventInfo) eventInfoField.get(null);
			eventInfo.setupEvent();
		}
	}
	
}
