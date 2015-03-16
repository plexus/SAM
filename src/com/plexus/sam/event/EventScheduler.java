package com.plexus.sam.event;

import java.awt.Container;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Timer;
import java.util.TimerTask;

import com.plexus.sam.gui.table.SongEventTable;

/**
 * Generic EventScheduler class. To use extend BasicEvent, adding the scheduling policy and
 * execution task. This class will keep the differen events ordered in a queue, and will
 * schedule the next to be executed task in a Timer.
 * 
 * @author plexus
 *
 */
public class EventScheduler {
	private static final boolean DEBUG = false;
	private static int count = 0;
	private int id;
	/**
	 * The queue that stores the events
	 */
	private PriorityQueue<BasicEvent> eventQueue;
	
	/**
	 * The timer that will execute every next event
	 */
	private Timer eventTimer;
	
	/**
	 * The next to be executed event
	 */
	private BasicEvent nextEvent;
	
	/**
	 * Switch that activates or deactivates this scheduler, see {@link #start()}, {@link #stop()}
	 */
	private boolean active = true;
	private List<SchedulerListener> listenerList;
	
	/**
	 * Default constructor
	 *
	 */
	public EventScheduler() {
		debug("new EventScheduler");
		eventQueue = new PriorityQueue<BasicEvent>();
		eventTimer = new Timer();
		id = count++;
		listenerList = new LinkedList<SchedulerListener>();
	}
	
	/**
	 * Add an event to the queue. If this changes the next to be scheduled event,
	 * then the timer is rescheduled to execute this new event.
	 * 
	 * @param e
	 */
	public void addEvent(BasicEvent e) {
		debug("addEvent "+e);
		if (nextEvent == null) {
			nextEvent = e;
			eventQueue.add( e );
			scheduleNext();
		} else {
			if (! eventQueue.contains( e ) ) {
				eventQueue.add( e );
				if (! nextEvent.equals( eventQueue.peek() )) {
					nextEvent = eventQueue.peek();
					scheduleNext();
				} 
			} else { 
				debug ("eventQueue allready contains "+e);
			}
		}
	}
	
	/**
	 * Remove an event from the queue. If this is the frontmost event, then the next
	 * event is scheduled.
	 * 
	 * @param e
	 * @return true if the event was in the queue
	 */
	public boolean removeEvent(BasicEvent e) {
		debug("removeEvent "+e);
		boolean b = eventQueue.remove( e );
		if (e.equals( nextEvent )) {
			nextEvent = eventQueue.peek();
			scheduleNext();
		}
		return b;
	}
	
	/**
	 * This method should always be called when the Time property of an event changes,
	 * so the event can be rescheduled appropriatly.
	 * 
	 * @param event
	 */
	public void rescheduleEvent(BasicEvent event) {
		debug ("rescheduleEvent "+event);
		debug("> execution time : "+event.getTime());
		if ( removeEvent(event) ) 
			addEvent(event.newInstace());
	}

	/**
	 * Program the Timer to execute the next event.
	 *
	 */
	private void scheduleNext() {
		debug("scheduleNext");
		if (active) {
			eventTimer.cancel();
			if (nextEvent != null) {
				eventTimer = new Timer();
				eventTimer.schedule( new TimerTask() {
					@Override
					public void run() {
						debug("Running event");
						getNextEvent().run();
					}
				}, nextEvent.getTime() );
				debug(">>scheduled "+nextEvent+" @ "+nextEvent.getTime());
			}
		}
		fireEventListChanged();
	}
	
	/**
	 * Activate the scheduler
	 */
	public void start() {
		debug("start");
		active = true;
		if (!eventQueue.isEmpty()) {
			nextEvent = eventQueue.peek();
			scheduleNext();
		}
	}
	
	/**
	 * Deactivate the scheduler
	 *
	 */
	public void stop() {
		debug("stop");
		active = false;
		eventTimer.cancel();
	}
	
	/**
	 * Output a debugging message preceded by "EventScheduler "
	 * 
	 * @param s debugging message
	 */
	public void debug(String s) {
		if (DEBUG) {
			System.out.println("EventScheduler ["+id+"]"+s);
			//System.out.println(" >>nextEvent "+nextEvent);
			//if (nextEvent != null)
				//System.out.println(" >>eventTimer "+nextEvent.executionTime);
		}
		
	}

	/**
	 * @return Returns the nextEvent.
	 */
	public BasicEvent getNextEvent() {
		return nextEvent;
	}

	/**
	 * Return an event by index, the returned elements are sorted by execution time.
	 * 
	 * @param index
	 * @return event
	 */
	@SuppressWarnings("unchecked")
	public BasicEvent getEvent(int index) {
		List<BasicEvent> newList = new ArrayList<BasicEvent>(eventQueue);
		Collections.sort(newList);
		return newList.get(index);
	}
	
	/**
	 * Get the number of events stored
	 * 
	 * @return the size of this eventQueue
	 */
	public int size() {
		return eventQueue.size();
	}
	
	/**
	 * Remove all scheduled events
	 *
	 */
	public void clear() {
		if (active)
			eventTimer.cancel();
		eventQueue.clear();
		nextEvent = null;
	}

	public void addListener(SchedulerListener listener) {
		listenerList.add(listener);
		
	}
	
	private void fireEventListChanged() {
		for (SchedulerListener s : listenerList ) {
			s.eventListChanged();
		}
	}
}
