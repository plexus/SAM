/*
 * Created on 29-sep-2005
 *
 */
package com.plexus.sam.audio;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.plexus.sam.SAM;
import com.plexus.sam.event.EventRule;
import com.plexus.sam.event.EventScheduler;
import com.plexus.sam.event.RulemodelListener;
import com.plexus.sam.event.SongEvent;
import com.plexus.sam.event.SongRule;
import com.plexus.sam.gui.RulesPlaylistEditPanel;
import com.plexus.sam.gui.RulesPlaylistPanel;

/**
 * Extension of the dynamic playlist with the ability to schedule songs (spots) to be
 * played at a certain time.
 * 
 * @see SongRule
 * @author plexus
 *
 */
public class RulesPlaylist extends DynamicPlaylist implements Iterable, Playlist {
	private boolean DEBUG = false;
	private EventScheduler myQueue;
	private List<SongRule> rules;
	private List<RulemodelListener> listenerList;
	
	/**
	 * 
	 *
	 */
	public RulesPlaylist() {
		debug("new RulesPlaylist");
		myQueue = new EventScheduler();
		rules = new ArrayList<SongRule>();
		listenerList = new ArrayList<RulemodelListener>();
	}

	/**
	 * Set the next song to be played
	 *
	 * @param next the next song to be played
	 */
	public void setNext(Song next) {
		Song oldNext = nextSong;
		this.nextSong = next;
		propertyListeners.firePropertyChange("next", oldNext, next);
	}

	/**
	 * Add a new rule
	 * @param rule
	 */
	public void addRule(SongRule rule) {
		debug("addRule "+rule);
		rules.add(rule);
		myQueue.addEvent(new SongEvent(myQueue, rule, this));
		fireRuleAdded(rule);
		rule.addPropertyChangeListener(this);
	}
	
	/**
	 * Remove a rule at a specified position
	 * @param index
	 */
	public void removeRule(int index) {
		debug("removeRule "+index);
		if (rules.size()>index) {
			SongRule rule = rules.remove(index);
			rule.remove();
			fireRuleRemoved(rule);
		}
		
	}
	
	/**
	 * Remove a certain rule
	 * @param rule
	 */
	public void removeRule(SongRule rule) {
		debug("removeRule "+rule);
		rules.remove(rule);
		rule.remove();
		fireRuleRemoved(rule);
	}
	
	/**
	 * the number of rules in the list
	 * 
	 * @return the number of rules in the list
	 */
	public int ruleSize() {
		return rules.size();
	}
	
	/**
	 * Get a rule specified by index
	 * @param i the index
	 * @return the rule
	 */
	public SongRule getRule(int i) {
		return rules.get(i);
	}
	
	/**
	 * We implement Iterable so one can traverse the rules
	 * @return iterator of the rules
	 */
	public Iterator<SongRule> iterator() {
		return rules.iterator();
	}
	
	/**
	 * @return Returns the rules.
	 */
	public SongRule[] getRules() {
		SongRule[] array = new SongRule[rules.size()];
		int i=0;
		for(SongRule r : rules) {
			array[i++] = r;
		}
		return array;
	}

	/**
	 * @param rules The rules to set.
	 */
	public void setRules(SongRule[] rules) {
		debug("setRules");
		for (SongRule r: rules) {
			if (SAM.repos.getSong(r.getSong().getId()) != null) { 
				this.rules.add(r);
				myQueue.addEvent(new SongEvent(myQueue, r, this));
				fireRuleAdded(r);
				r.addPropertyChangeListener(this);
				debug(" -> setRules:"+r);
			}
		}
	}
	
	/**
	 * Add a listener
	 * @param l listener to add
	 */
	public void addListener(RulemodelListener l) {
		listenerList.add(l);
	}
	
	/**
	 * remove a listener
	 * @param l listener to be removed
	 */
	public void removeListener(RulemodelListener l) {
		listenerList.remove(l);
	}
	
	/**
	 * Notify listeners a rule was added
	 * @param rule rule that was added
	 */
	private void fireRuleAdded(EventRule rule) {
		debug(" firing rule added : "+rule);
		for (RulemodelListener l : listenerList)
			l.ruleAdded(rule);
	}
	
	/**
	 * Notify listener a rule was removed
	 * 
	 * @param rule that was removed
	 */
	private void fireRuleRemoved(EventRule rule) {
		debug(" firing rule removed : "+rule);
		for (RulemodelListener l : listenerList)
			l.ruleRemoved(rule);
	}
	
	/**
	 * Notify listener a rule was changed
	 * 
	 * @param rule rule that was changed
	 */
	private void fireRuleChanged(EventRule rule) {
		debug(" firing rule changed : "+rule);
		for (RulemodelListener l : listenerList)
			l.ruleChanged(rule);
	}

	/**
	 * @return the class of the panel that can edit this playlist
	 */
	@Override
	public Class getEditPanelClass() {
		return RulesPlaylistEditPanel.class;
	}

	/**
	 * @return the class of the panel that can view this playlist
	 */
	@Override
	public Class getPanelClass() {
		return RulesPlaylistPanel.class;
	}
	
	/**
	 * When a rule changes we notify our listeners
	 * When the player starts/stops this playlist, we start stop our eventqueue
	 * @param evt
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		if (evt.getSource() instanceof SongRule) {
			fireRuleChanged((SongRule)evt.getSource());
		}
		if(evt.getPropertyName().equals("playlist")) {
			if (evt.getOldValue() != null && evt.getOldValue().equals(this)) {
				myQueue.stop();
				debug("stopped queue");
			}
			if (evt.getNewValue() != null && evt.getNewValue().equals(this)) {
				myQueue.start();
				debug("started queue");
				debug("next event : "+myQueue.getNextEvent());
			}
		}
	}

	/**
	 * @return Returns the myQueue.
	 */
	public EventScheduler getQueue() {
		return myQueue;
	}
	
	public void debug(String msg) {
		if(DEBUG)
			System.out.println("RulesPlaylist "+msg);
	}
}
