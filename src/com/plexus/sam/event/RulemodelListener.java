/*
 * Created on 27-sep-2005
 *
 */
package com.plexus.sam.event;

import com.plexus.sam.audio.DynamicPlaylist;

/**
 * An rulemodellistener can register itself to a rulemodel which is a container,
 * that maintains a set of EventRules. The listener is notified when
 * rules are added, removed or changed.
 * 
 * @see Autoplayer
 * @see DynamicPlaylist
 * @author plexus
 *
 */
public interface RulemodelListener {
	/**
	 * 
	 * @param rule rule that was added
	 */
	public void ruleAdded(EventRule rule);
	
	/**
	 * 
	 * @param rule rule that was removed
	 */
	public void ruleRemoved(EventRule rule);
	
	/**
	 * 
	 * @param rule rule that was changed
	 */
	public void ruleChanged(EventRule rule);
}
