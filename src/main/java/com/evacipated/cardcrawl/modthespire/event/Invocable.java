package com.evacipated.cardcrawl.modthespire.event;

import com.evacipated.cardcrawl.modthespire.lib.Event;

public abstract class Invocable {

	private Object parent;
	
	public Invocable(Object parent) {
		this.parent = parent;
	}
	
	public Object getParent() {
		return parent;
	}
	
	public abstract void invoke(Event event) throws EventProcessingException ;
	public abstract boolean receiveIfCanceled();
	
}
