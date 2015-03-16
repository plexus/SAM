/*
 * Created on 27-sep-2005
 *
 */
package com.plexus.sam.event;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.Date;

import com.plexus.sam.SAM;
import com.plexus.sam.audio.Player;
import com.plexus.util.Time;

/**
 * Event that starts or stops the player with a certain playlist.
 * 
 * @author plexus
 */
public class AutoplayEvent extends BasicEvent implements PropertyChangeListener {
	private static final boolean DEBUG = false;
	private AutoplayRule myRule;
	private boolean start;
	private Player player = SAM.player;
	
	/**
	 * Set the EventQueue, the rule that contains the start/stop time and the playlist to start.
	 * 
	 * @param myQueue the Queue this event is scheduled in (for rescheduling)
	 * @param myRule 
	 * @param start true when the player must be started, false when it must be stopped
	 */
	public AutoplayEvent(EventScheduler myQueue, AutoplayRule myRule, boolean start) {
		super(myQueue);
		debug ("new AutoplayEvent "+ (start?"start ":"stop ") +myRule);
		this.myRule = myRule;
		this.start = start;
		myRule.addPropertyChangeListener(this);
		executionTime = nextExecution();
	}

	/**
	 * Creat a clone of this event
	 * 
	 * @return an exact copy
	 */
	@Override
	protected BasicEvent newInstace() {
		debug("newInstance()");
		return new AutoplayEvent(myQueue, myRule, start);
	}

	/**
	 * Execute this event : start or stop the player.
	 * When the player is allready playing (in case start is true), nothing is done.
	 * (The playlist doesn't change).
	 */
	@Override
	protected void execute() {
		debug ("execute "+ (start?"start ":"stop ")+myRule);
		if (! myRule.getStart().equals( myRule.getStop() ) )
				if (start) {
					if (!player.isPlaying()) {
						player.setPlaylist(myRule.getPlaylist());
						player.play();
					}
				} else {
					if (player.isPlaying()) 
						player.stop();	
				}
	}

	/**
	 * Calculate the next time this event must be scheduled.
	 * @return the exect execution timestamp
	 */
	@Override
	protected Date nextExecution() {
		Time execTime;
		if (start)
			execTime = myRule.getStart();
		else
			execTime = myRule.getStop();
		
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
	 * Listen for changes in the autoplayrule so we can reschedule ourselves
	 * @param evt changeevent from the {@link AutoplayRule}
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		debug ("rule has changed "+evt.getSource());
		if ( (evt.getPropertyName().equals("start") && start) ||
			 (evt.getPropertyName().equals("stop") && !start) || 
			 (evt.getPropertyName().equals("dayofweek")) ) {
			this.executionTime = nextExecution();
			myQueue.rescheduleEvent(this);
		}
	}

	/**
	 * @param o object to compare with
	 * @return negative when executiontime of this object is earlier than the executiontime of o,
	 * 			0 when they are equal
	 * 			negative otherwise
	 */
	@Override
	public int compareTo(Object o) {
		debug("compare : "+executionTime+" with "+((AutoplayEvent)o).executionTime);
		debug(">returning "+this.executionTime.compareTo( ((AutoplayEvent)o).executionTime ));
		return this.executionTime.compareTo( ((AutoplayEvent)o).executionTime );
	}
	
	/**
	 * @param o object to compare with
	 * @return true when they are equal
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof AutoplayEvent) {
			AutoplayEvent e = (AutoplayEvent)o;
			return e.start ==start && e.myRule.equals(myRule); 
		}
		return false;
		
	}
	
	/**
	 * @return a string representation of the form "Event:\<Start/Stop\> \<Rule\>" 
	 */
	@Override
	public String toString() {
		return "Event:"+(start?"Start":"Stop")+" "+myRule;
	}
	
	/**
	 * Output a debugging message preceded by "AutoplayEvent "
	 * 
	 * @param s debugging message
	 */
	public void debug(String s) {
		if (DEBUG)
			System.out.println("AutoplayEvent "+s);
		
	}
}
