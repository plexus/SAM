/*
 * Created on 14-sep-2005
 *
 */
package com.plexus.sam.event;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.plexus.sam.audio.Repository;
import com.plexus.sam.config.ConfigGroup;
import com.plexus.sam.config.Configuration;

/**
 * This class takes care of keeping the repository up to date. It will check the configuration to see if this
 * should be done automatically, and start or stop the scheduler accordingly.
 * 
 * @author plexus
 *
 */
public class RepositorySynchronizer implements PropertyChangeListener {
	public boolean DEBUG = false;
	private ConfigGroup config = Configuration.getConfigGroup("sync");
	private EventScheduler syncScheduler;
	private Repository repos;
	
	/**
	 * @param repos repository to synchronize
	 */
	public RepositorySynchronizer( Repository repos ) {
		this.repos = repos;
		this.syncScheduler = new EventScheduler();
		config.addChangeListener( this );
		if (config.get("autosync").equals("false"))
			syncScheduler.stop();
		
		SyncEvent syncEvent = new SyncEvent( syncScheduler, repos );
		syncScheduler.addEvent( syncEvent );
	}
	
	/**
	 * Listen for changes in the 'autosync' property of the 'sync' configgroup,
	 * to start or stop the scheduler.
	 * 
	 * @param evt event from the configuration
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("autosync")) {
			if (config.get("autosync").equals("true"))
				syncScheduler.start();
			else
				syncScheduler.stop();
		}
		
	}
	
	/**
	 * Drop everything and synchronize now
	 */
	public void syncNow() {
		debug("syncNow");
		syncScheduler.stop();
		this.syncScheduler = new EventScheduler();
		if (config.get("autosync").equals("false"))
			syncScheduler.stop();
		
		SyncEvent syncEvent = new SyncEvent( syncScheduler, repos );
		syncScheduler.addEvent(syncEvent);
		syncEvent.execute();
		
		//this will automatically reschedule the event after execution, if autosync is on.
	}
	
	public void debug(String msg) {
		if (DEBUG)
			System.out.println("RepositorySynchronizer "+msg);
	}
}
