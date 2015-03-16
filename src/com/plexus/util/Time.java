/*
 * Created on 27-sep-2005
 *
 */
package com.plexus.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.plexus.sam.event.AutoplayRule;

/**
 * Time in hours and minutes. Only valid numbers can be stored, <br />
 * Domain(hours)=[0,24[<br />
 * Domain(minutes)=[0,60[<br /> 
 * <br />
 * One {@link PropertyChangeListener} can be set to receive changeEvent.
 * This way an {@link AutoplayRule} can transmit it's own events when the
 * start or stop time changes.<br /><br />
 * This also implies no two rules can use the same Time object.
 * @author plexus
 */
public class Time implements Comparable{
	private int hours;
	private int minutes;
	private PropertyChangeListener listener = null;
	
	/**
	 * Set everything except the changelistener
	 * 
	 * @param hours
	 * @param minutes
	 */
	public Time(int hours, int minutes) {
		setHours(hours);
		setMinutes(minutes);
	}
	
	/**
	 * Default constructor, set hours:minutes to 00:00
	 *
	 */
	public Time() {
		this(0,0);
	}
	
	/**
	 * Copy a time to a new object. This doesn't copy the changelistener.
	 * @param t
	 */
	public Time(Time t) {
		this(t.getHours(), t.getMinutes());
	}

	/**
	 * @return Returns the hours.
	 */
	public int getHours() {
		return hours;
	}

	/**
	 * When <code>hours < 0 || hours > 23</code>, hours is set to <code>0</code> or <code>23</code> respectivly
	 * @param hours The hours to set.
	 */
	public void setHours(int hours) {
		int oldValue = this.hours;
		this.hours = hours >= 0 ? hours < 24 ? hours : 23 : 0;
		firePropertyChange("hours", oldValue, hours);
	}

	/**
	 * @return Returns the minutes.
	 */
	public int getMinutes() {
		return minutes;
	}

	/**
	 * When <code>minutes < 0 || minutes > 59</code>, minutes is set to <code>0</code> or <code>59</code> respectivly
	 * @param minutes The minutes to set.
	 */
	public void setMinutes(int minutes) {
		int oldMinutes = this.minutes;
		this.minutes = minutes >=0 ? minutes < 60 ? minutes : 59 : 0;
		firePropertyChange("minutes", oldMinutes, minutes);
	}
	
	/**
	 * @return '\<hours\>.\<minutes\>'
	 */
	@Override
	public String toString() {
		return hours+":"+minutes;
	}
	
	/**
	 * @param o object to compare with
	 * @return true when <code>o instanceof Time</code>
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof Time) {
			Time t = (Time)o;
			return hours==t.getHours() && minutes==t.getMinutes();
		}
		return false;
	}

	
	/**
	 * This method is based on the number of minutes difference between the two times.
	 * e.g. <code>(new Time(15, 30)).compareTo(new Time(16, 45)</code> will return -75 .
	 * (negative since 15:30 is earlier than 16:45)
	 * 
	 * @param o object to compare with
	 * @return the difference in minutes between the two times
	 */
	public int compareTo(Object o) {
		Time t = (Time)o;
		int totalminutes1 = minutes + hours * 60;
		int totalminutes2 = t.getHours() * 60 + t.getMinutes();
		return totalminutes1 - totalminutes2;
	}

	/**
	 * Transmit a bound property change event
	 * @param property property name
	 * @param oldValue old value
	 * @param newValue new value
	 */
	private void firePropertyChange(String property, int oldValue, int newValue) {
		if (oldValue != newValue && listener != null) {
			PropertyChangeEvent evt = new PropertyChangeEvent(this, property, new Integer(oldValue), new Integer(newValue));
			listener.propertyChange(evt);
		}
	}

	/**
	 * @param listener The listener to set.
	 */
	public void setListener(PropertyChangeListener listener) {
		this.listener = listener;
	}
}