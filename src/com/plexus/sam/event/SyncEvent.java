/*
 * Created on 14-sep-2005
 *
 */
package com.plexus.sam.event;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.plexus.debug.Assert;
import com.plexus.sam.SAM;
import com.plexus.sam.audio.Repository;
import com.plexus.sam.config.ConfigGroup;
import com.plexus.sam.config.Configuration;

/**
 * Does the automatic synchronization of the repository. The time of last execution is written to
 * the configuration.
 * 
 * @author plexus
 *
 */
public class SyncEvent extends BasicEvent {
	private DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, Locale.ENGLISH);
	private ConfigGroup config = Configuration.getConfigGroup("sync");
	private Repository repos;
	
	/**
	 * Set the scheduler to execute this task, and the repository to synchronize
	 * @param e
	 * @param repos
	 */
	public SyncEvent(EventScheduler e, Repository repos) {
		super(e);
		this.repos = repos;
		executionTime = nextExecution();
	}

	/**
	 * Synchronize the {@link com.plexus.sam.audio.Repository}, and save the time
	 * of last execution to the configuration.
	 */
	@Override
	protected void execute() {
		try {
			repos.synchronize();
			repos.save();
			config.set( "last", df.format( Calendar.getInstance(Locale.ENGLISH).getTime() ) );
			Configuration.save();
		}
		catch (Exception e) {
			SAM.error("synchronize_failed", e.getMessage(), e);
		}

	}

	/**
	 * Returns the time of last execution plus the interval as read from the configuration.
	 * @return next execution time 
	 */
	@Override
	protected Date nextExecution() {
		try {	 
			Assert.notNull("SynEvent : DateFormat df", df);
			Assert.notNull("SynEvent : ConfigGroup config ('sync')", config);
			Date last = df.parse(config.get("last"));
			Calendar c = Calendar.getInstance(Locale.ENGLISH);
	
			c.setTime( last );
			int interval = Integer.parseInt(config.get("interval")); //in seconds
			c.add(Calendar.SECOND, interval);
			return c.getTime();
			
		} catch (ParseException e) {
			Calendar c = Calendar.getInstance(Locale.ENGLISH);
			Date last = c.getTime();
			config.set("last", df.format(last));
			c.setTime( last );
			int interval = Integer.parseInt(config.get("interval")); //in seconds
			c.add(Calendar.SECOND, interval);
			return c.getTime();
		}
	}

	/**
	 * Return a new SyncEvent for the next execution
	 * @return new event with same queue and repository
	 */
	@Override
	protected BasicEvent newInstace() {
		return new SyncEvent( this.myQueue, this.repos );
	}

}
