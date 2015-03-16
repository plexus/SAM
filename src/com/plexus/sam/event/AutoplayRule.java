/*
 * Created on 25-sep-2005
 */
package com.plexus.sam.event;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Calendar;

import com.plexus.sam.audio.Playlist;
import com.plexus.util.Time;

/**
 * A rule containing a day of the week when this rule must be active,
 * a playlist that must be loaded into the player and started/stopped.
 * A time during the day (hours and minutes) when the player should
 * be started and stopped.
 * <br /><br />
 * This class obeys the javabean conventions so it can be made persistent.
 * <br />
 * Following propertys generate a {@link PropertyChangeEvent}</br>
 * <ul>
 * <li>dayofweek</li>
 * <li>start</li>
 * <li>stop</li>
 * <li>playlist</li>
 *
 * @see java.beans.XMLEncoder
 * @author plexus
 */
public class AutoplayRule extends EventRule implements PropertyChangeListener, Comparable {
	private static final boolean DEBUG = false;
	
	/**
	 * Time to start playing
	 */
	private Time start;
	
	/**
	 * Time to stop playing
	 */
	private Time stop;
	
	/**
	 * The playlist to start/stop
	 */
	private Playlist playlist;
	
	/**
	 * Set all fields
	 * 
	 * @param dayofweek a day constant from {@link Calendar}, e.g. {@link Calendar#MONDAY}
	 * @param start time to start the player with the given playlist
	 * @param stop time to stop the player with the given playlist
	 * @param playlist playlist to play
	 */
	public AutoplayRule(int dayofweek, Time start, Time stop, Playlist playlist) {
		assert 	dayofweek == MONDAY || 
				dayofweek == TUESDAY || 
				dayofweek == WEDNESDAY ||
				dayofweek == THURSDAY ||
				dayofweek == FRIDAY ||
				dayofweek == SATURDAY ||
				dayofweek == SUNDAY ||
				dayofweek == EVERYDAY;
		assert start != null;
		assert stop != null;
		
		this.dayofweek = dayofweek;
		this.start = start;
		this.stop = stop;
		this.playlist = playlist;
		
		debug("new Autoplayrule "+this);
		
		start.setListener(this);
		stop.setListener(this);
		this.listenerSupport = new PropertyChangeSupport(this);
		debug("new Autoplayrule "+this);
	}
	
	/**
	 * Construct a rule with Monday as dayofweek, that starts at 0:00 and stops at 0:01
	 * with playlist <code>null</code>
	 *
	 */
	public AutoplayRule() {
		this(MONDAY, new Time(0,0), new Time(0,0), null);
	}

	/**
	 * @return Returns the playlist.
	 */
	public Playlist getPlaylist() {
		return playlist;
	}

	/**
	 * @param playlist The playlist to set.
	 */
	public void setPlaylist(Playlist playlist) {
		debug("set playlist "+playlist);
		Playlist oldPlaylist = this.playlist;
		this.playlist = playlist;
		listenerSupport.firePropertyChange("playlist", oldPlaylist, playlist);
	}

	/**
	 * @return Returns the start.
	 */
	public Time getStart() {
		return start;
	}

	/**
	 * @param start The start to set.
	 */
	public void setStart(Time start) {
		debug("setStart" + start);
		Time oldStart = this.start;
		this.start = start;
		listenerSupport.firePropertyChange("start", oldStart, start);
		oldStart.setListener(null);
		start.setListener( this );
	}

	/**
	 * @return Returns the stop.
	 */
	public Time getStop() {
		return stop;
	}

	/**
	 * @param stop The stop to set.
	 */
	public void setStop(Time stop) {
		debug("setStop "+stop);
		Time oldStop = this.stop;
		this.stop = stop;
		listenerSupport.firePropertyChange("stop", oldStop, stop);
		oldStop.setListener(null);
		stop.setListener(this);
	}
	
	
	/**
	 * @return e.g. "Monday Start:8.30 Stop:17.45 Playlist:DynamicPlaylist@E4dg5g'
	 */
	@Override
	public String toString() {
		String day = null;
		switch (dayofweek) {
		case(MONDAY) : day="Monday";break;
		case(TUESDAY) : day="Tuesday";break;
		case(WEDNESDAY) : day="Wednesday";break;
		case(THURSDAY) : day="Thursday";break;
		case(FRIDAY) : day="Friday";break;
		case(SATURDAY) : day="Saturday";break;
		case(SUNDAY) : day="Sunday";break;
		case(EVERYDAY) : day="Every day";break;
		default :
			assert false; //unreachable code
		}
		return day+" Start:"+start+" Stop:"+stop+" Playlist:"+playlist;
	}
	
	/**
	 * @param o object to compare with
	 * @return true when o is of type AutoplayRule and all fields are equal 
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof AutoplayRule) {
			AutoplayRule rule = (AutoplayRule) o;
			return 	dayofweek == rule.getDayofweek() && 
					start.equals(rule.getStart()) && 
					stop.equals(rule.getStop()) && 
					playlist == rule.getPlaylist(); 
		}
		return false;
	}

	/**
	 * Implemtation of PropertyChangeListener, when the start or stop time is
	 * directly modified, we also need to send out a {@link PropertyChangeEvent}.
	 * @param evt the event we receive from the Time class
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		assert evt.getSource().getClass() == Time.class;
		Time oldValue;
		if (evt.getSource().equals(start)) {
			oldValue = new Time(start);
			
			if (evt.getPropertyName().equals("hours"))
				oldValue.setHours( ((Integer)evt.getNewValue()).intValue() );
			else if (evt.getPropertyName().equals("minutes"))
				oldValue.setMinutes( ((Integer)evt.getNewValue()).intValue() );
			
			this.listenerSupport.firePropertyChange("start", oldValue, start);
		} else if (evt.getSource().equals(stop)) {
			oldValue = new Time(stop);
			
			if (evt.getPropertyName().equals("hours"))
				oldValue.setHours( ((Integer)evt.getNewValue()).intValue() );
			else if (evt.getPropertyName().equals("minutes"))
				oldValue.setMinutes( ((Integer)evt.getNewValue()).intValue() );
			
			this.listenerSupport.firePropertyChange("stop", oldValue, stop);
		}
	}
	

	
	/**
	 * Output a debugging message preceded by "AutoplayRule "
	 * 
	 * @param s debugging message
	 */
	public void debug(String s) {
		if (DEBUG)
			System.out.println("AutoplayRule "+s);
		
	}

	/**
	 * Sort on dayofweek, starttime, stoptime in that order.
	 * 
	 * @param o
	 * @return 0 when equal, negative when this comes before o, positive otherwise
	 */
	public int compareTo(Object o) {
		AutoplayRule other = (AutoplayRule)o;
		if (this.dayofweek == other.dayofweek) {
			if (this.start.equals(other.start))
				return this.stop.compareTo(other.stop);
			return this.start.compareTo(other.start);
		}
		return this.dayofweek - other.dayofweek;
	}
}
