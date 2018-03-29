package com.evacipated.cardcrawl.modthespire.lib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.evacipated.cardcrawl.modthespire.event.Event;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {
	int priority() default Priority.DEFAULT;
	boolean receiveIfCancelled() default Event.DEFAULT_CANCELLED;
	
	/**
	 * higher priority is indicated by lower numbers
	 */
	public static class Priority {
		private Priority() {}
		
		public static final int VERY_LOW = 100;
		public static final int LOW = 50;
		public static final int DEFAULT = 0;
		public static final int HIGH = -50;
		public static final int VERY_HIGH = -100;
		public static final int MINOR_STEP = 1;
		public static final int MAJOR_STEP = 5;
	}
}
