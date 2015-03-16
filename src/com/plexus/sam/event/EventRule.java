/*
 * Created on 29-sep-2005
 *
 */
package com.plexus.sam.event;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Calendar;

/**
 * Base class for Rule classes that say when some event must occur, and what must happen then
 * 
 * All known subclasses:<br />
 * {@link AutoplayRule}
 * {@link SongRule}
 * @author plexus
 *
 */
public class EventRule {
	/**
	 * We declare these solely for convenience
	 */
	protected static final int MONDAY = Calendar.MONDAY; //2
	protected static final int TUESDAY = Calendar.TUESDAY; //3
	protected static final int WEDNESDAY = Calendar.WEDNESDAY; //4
	protected static final int THURSDAY = Calendar.THURSDAY; //5
	protected static final int FRIDAY = Calendar.FRIDAY; //6
	protected static final int SATURDAY = Calendar.SATURDAY; //7
	protected static final int SUNDAY = Calendar.SUNDAY; //1
	
	/**
	 * This rule is applicable every day
	 */
	public static final int EVERYDAY = 8;
	
	/**
	 * The day of the week this rule applies to
	 */
	protected int dayofweek;
	
	/**
	 * All our propertys are javabean bound propertys.
	 * 
	 */
	protected PropertyChangeSupport listenerSupport;

	/**
	 * 
	 *
	 */
	public EventRule() {
		listenerSupport = new PropertyChangeSupport(this);
	}
	/**
	 * @return Returns the dayofweek.
	 */
	public int getDayofweek() {
		return dayofweek;
	}

	/**
	 * @param dayofweek The dayofweek to set.
	 */
	public void setDayofweek(int dayofweek) {
		int oldDayofweek = this.dayofweek;
		this.dayofweek = dayofweek;
		listenerSupport.firePropertyChange("dayofweek", oldDayofweek, dayofweek);
	}
	
	/**
	 * Delegate method
	 * 
	 * @param listener 
	 * @see java.beans.PropertyChangeSupport#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listenerSupport.addPropertyChangeListener(listener);
	}

	/**
	 * Delegate method
	 * 
	 * @param propertyName 
	 * @param listener 
	 * @see java.beans.PropertyChangeSupport#addPropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		listenerSupport.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * Delegate method
	 * 
	 * @param listener 
	 * @see java.beans.PropertyChangeSupport#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listenerSupport.removePropertyChangeListener(listener);
	}

	/**
	 * Delegate method
	 * 
	 * @param propertyName 
	 * @param listener 
	 * @see java.beans.PropertyChangeSupport#removePropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
	 */
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		listenerSupport.removePropertyChangeListener(propertyName, listener);
	}
}
