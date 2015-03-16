/*
 * Created on 2-okt-2005
 *
 */
package com.plexus.sam.event;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.plexus.sam.audio.Song;
import com.plexus.util.Time;

/**
 * Rule that contains the necessary information for a rule-based playlist,
 * to start a given song at a given time.
 * 
 * @author plexus
 *
 */
public class SongRule extends EventRule implements PropertyChangeListener, Comparable {
	private Song song;
	private Time time;
	
	/**
	 * Set the day of the week, the time and the song that should be started at that time
	 * 
	 * @param dayofweek
	 * @param time
	 * @param song
	 */
	public SongRule(int dayofweek, Time time, Song song) {
		this.dayofweek = dayofweek;
		this.time = time;
		this.song = song;
		if (time != null)
			time.setListener( this );
	}
	
	/**
	 * Create a songrule with default values
	 *
	 */
	public SongRule() {
		this(MONDAY, new Time(0,0), null);
	}

	
	/**
	 * @return Returns the song.
	 */
	public Song getSong() {
		return song;
	}
	/**
	 * @param song The song to set.
	 */
	public void setSong(Song song) {
		Song oldSong = this.song;
		this.song = song;
		listenerSupport.firePropertyChange("song", oldSong, song);
	}
	/**
	 * @return Returns the time.
	 */
	public Time getTime() {
		return time;
	}
	/**
	 * @param time The time to set.
	 */
	public void setTime(Time time) {
		Time oldTime = this.time;
		this.time = time;
		listenerSupport.firePropertyChange("time", oldTime, time);
		oldTime.setListener(null);
		time.setListener( this );
	}

	/**
	 * Notify our listeners (the event(s) corresponding to this rule) that we are
	 * being removed
	 *
	 */
	public void remove() {
		listenerSupport.firePropertyChange("remove", this, null);
	}
	
	/**
	 * Listen to the Time object for changes in so we can propagate
	 * this event.
	 * 
	 * @param evt
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == time) {
			Time oldValue = new Time( time );
			
			if (evt.getPropertyName().equals("hours"))
				oldValue.setHours( ((Integer)evt.getNewValue()).intValue() );
			else if (evt.getPropertyName().equals("minutes"))
				oldValue.setMinutes( ((Integer)evt.getNewValue()).intValue() );
			
			this.listenerSupport.firePropertyChange("time", oldValue, time);
		}
			
		
	}

	/**
	 * implementation of comparable, first on compare on day of week,
	 * then on time
	 * @param o
	 * @return 0 when equal, negative when this before o, positive otherwise
	 */
	public int compareTo(Object o) {
		SongRule other = (SongRule)o;
		if (this.dayofweek == other.dayofweek) {
			return this.time.compareTo(other.time);
		}
		return this.dayofweek - other.dayofweek;
	}
	
}
