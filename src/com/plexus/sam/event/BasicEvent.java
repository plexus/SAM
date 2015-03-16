package com.plexus.sam.event;


import java.util.Date;
import java.util.TimerTask;

/**
 * Base class to create events that can be scheduled with the EventSchedular class. 
 * To create events, implement the {@link #nextExecution()} method, so it always returns the
 * time when this event has to be executed. By setting the {@link #recurrent} property to false,
 * the event is executed only once. Otherwise, it will schedule a new exection after every
 * execution.<br /><br />
 * Make sure you add the line <code>executionTime = nextExecution()</code> to your constructor.
 *  
 * @author plexus
 *
 */
public abstract class BasicEvent extends TimerTask implements Comparable {
	protected EventScheduler myQueue;
	protected Date executionTime;
	protected boolean recurrent = true;
	
	/**
	 * We need a reference to our scheduler to reschedule ourselves
	 * 
	 * @param myQueue
	 */
	protected BasicEvent(EventScheduler myQueue) {
		this.myQueue = myQueue;
	}
	
	/**
	 * Set the next execution time
	 * 
	 * @param d
	 */
	public void setTime(Date d) {
		this.executionTime = d;
	}

	/**
	 * Get the execution time
	 * 
	 * @return the execution time
	 */
	public Date getTime() {
		return executionTime;
	}
	
	/**
	 * Implementation of comparable, to order events
	 * @param arg0
	 * @return 0 if equal, negative when smaller, positive when greater
	 */
	public int compareTo(Object arg0) {
		return executionTime.compareTo( ((BasicEvent)arg0).executionTime );
	}

	/**
	 * implementation of Runnable, do the execution and reschedule if necessary.
	 * 
	 */
	@Override
	public void run() {
		execute();
		if (recurrent) {
			executionTime = nextExecution();
			myQueue.removeEvent(this);
			myQueue.addEvent( newInstace() );
		} else {
			myQueue.removeEvent( this );
		}
	}
	
	/**
	 * @return a new instance of the concrete class, initialized with the same queue
	 */
	protected abstract BasicEvent newInstace() ;

	/**
	 * Action to be performed
	 *
	 */
	protected abstract void execute();
	
	/**
	 * @return time of next execution
	 */
	protected abstract Date nextExecution();
}
