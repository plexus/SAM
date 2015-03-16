/*
 * Created on 2-okt-2005
 *
 */
package com.plexus.sam.event;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.Date;

import com.plexus.sam.audio.RulesPlaylist;
import com.plexus.sam.audio.Song;
import com.plexus.util.Time;

/**
 * Event that can be scheduled for execution. This event is associated with a {@link RulesPlaylist},
 * it will set the next song that will be returned by the playlist.<br />
 * Used for programming jingles and the likes.
 * 
 * @author plexus
 *
 */
public class SongEvent extends BasicEvent implements PropertyChangeListener {
	private SongRule myRule;
	private RulesPlaylist playlist;
	
	/**
	 * Set the scheduler, and the rule that must be executed
	 * 
	 * @param sched
	 * @param rule
	 * @param playlist
	 */
	public SongEvent(EventScheduler sched, SongRule rule, RulesPlaylist playlist) {
		super(sched);
		myRule = rule;
		this.playlist = playlist;
		
		myRule.addPropertyChangeListener(this);
		executionTime = nextExecution();
	}
	
	/**
	 * Create an exact copy
	 * @return a clone of this object
	 */
	@Override
	protected BasicEvent newInstace() {
		return new SongEvent(myQueue, myRule, playlist);
	}

	/**
	 * Execute this event : set the next song to be played
	 */
	@Override
	protected void execute() {
		if (myRule.getSong() != null)
			playlist.setNext( myRule.getSong() );
	}

	/**
	 * @return the time when this rule has to be executed  
	 */
	@Override
	protected Date nextExecution() {
		Time execTime = myRule.getTime();
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.HOUR_OF_DAY, execTime.getHours());
		cal.set(Calendar.MINUTE, execTime.getMinutes());
		
		if (myRule.getDayofweek() == EventRule.EVERYDAY) {
			cal.set(Calendar.DATE, cal.get(Calendar.DATE));
			
			if (cal.before(Calendar.getInstance()))
				cal.set(Calendar.DATE, cal.get(Calendar.DATE)+1);
			
			return cal.getTime();
		}
		
		cal.set(Calendar.DAY_OF_WEEK, myRule.getDayofweek());
		if (cal.before(Calendar.getInstance()))
			cal.set(Calendar.DATE, cal.get(Calendar.DATE)+7);
		
		return cal.getTime();
	}

	/**
	 * Listen to the SongRule for changes
	 * 
	 * @param evt changeevent from the SongRule
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if ( evt.getPropertyName().equals("time") || 
				 evt.getPropertyName().equals("dayofweek") ) {
				this.executionTime = nextExecution();
				myQueue.rescheduleEvent(this);
		} else if (evt.getPropertyName().equals("remove")) {
			myQueue.removeEvent(this);
		}
		
	}

	/**
	 * 
	 * @return
	 */
	public Song getSong() {
		return this.myRule.getSong();
	}
}
