/*
 * Created on 13-sep-2005
 *
 */
package com.plexus.sam.gui.actions;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;

import com.plexus.sam.SAM;
import com.plexus.sam.audio.Player;
import com.plexus.sam.gui.Icons;

/**
 * Abstract superclass for all actions that control the player
 * (Start, stop, pause, previous, next). Actions override the {@link actionPerfomed(ActionEvent e)}
 * and {@link enabled()} method to define the action, and when it
 * should be enabled or disabled.
 * 
 * @author plexus
 *
 */
public abstract class PlayerAction extends AbstractAction implements PropertyChangeListener {
	protected Player player;
	
	/**
	 * Common constructor, the key is used to get the caption and toolbar icon.
	 * 
	 * @param player
	 * @param key
	 */
	protected PlayerAction(Player player, String key) {
		this.player = player;
		this.putValue(NAME, SAM.getBundle("gui").getString(key));
		this.putValue(SMALL_ICON, Icons.get(key));
		player.addListener(this);
		this.setEnabled(enabled());
	}
	
	/**
	 * Override this method
	 */
	public abstract void actionPerformed(ActionEvent e);

	/**
	 * Override this method to set the enable/disable policy
	 * @return true if this action should be enabled
	 */
	protected abstract boolean enabled();
	
	/**
	 * Listen to the player to en/disable
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		this.setEnabled(enabled());
	}
	
}
