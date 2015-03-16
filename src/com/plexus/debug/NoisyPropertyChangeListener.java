/*
 * Created on 8-sep-2005
 *
 */
package com.plexus.debug;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * A real noisy changelistener for debugging purposes.
 * @author plexus
 *
 */
public class NoisyPropertyChangeListener implements PropertyChangeListener {
	
	private static int anonCount = 0; 
	private String name;
	
	/**
	 * Give a name that will be shown in all output messages 
	 * @param name
	 */
	public NoisyPropertyChangeListener (String name) {
		this.name = name;
	}
	
	/**
	 * Give a unique numeric id as name.
	 *
	 */
	public NoisyPropertyChangeListener () {
		this.name = ""+anonCount++;
	}
	
	/**
	 * Implementatino of {@link PropertyChangeListener}, this will output the source class, source,
	 * propertyname, old value and new value
	 * @param evt
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		out("new ChangeEvent");
		out("from source "+evt.getSource());
		out("from class "+evt.getClass());
		out("property ["+evt.getPropertyName()+"]");
		out("old value "+evt.getOldValue());
		out("new value "+evt.getNewValue());
	}
	
	/**
	 * Output a message, preceded by  'NoisyPropertyChangeListener[\<name\>]'
	 * 
	 * @param s message
	 */
	private void out(String s) {
		System.out.println ("NoisyPropertyChangeListener["+name+"] "+s);
	}
}
