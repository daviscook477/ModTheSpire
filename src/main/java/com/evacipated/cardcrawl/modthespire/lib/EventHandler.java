package com.evacipated.cardcrawl.modthespire.lib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {
	public static final boolean DEFAULT_AUTOMATIC = true;
	
	int priority() default Priority.DEFAULT;
	boolean receiveIfCanceled() default Event.DEFAULT_CANCELED;
	boolean automatic() default DEFAULT_AUTOMATIC;
	
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
