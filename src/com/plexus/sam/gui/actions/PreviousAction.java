/*
 * Created on 11-sep-2005
 *
 */
package com.plexus.sam.gui.actions;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;

import com.plexus.sam.SAM;
import com.plexus.sam.audio.Player;

/**
 * Let the player play the previous song. Listen to the player to enable/disable.
 *
 */
public class PreviousAction extends PlayerAction {

	
	/**
	 * Default constructor
	 */
	public PreviousAction(Player player) {
		super(player, "previous");
	}
	
	/**
	 * Action : player.previous()
	 */
	public void actionPerformed(ActionEvent e) {
		player.previous();
	}

	/**
	 * Listen to the player
	 */
	protected boolean enabled() {
		return player.hasPrevious();
	}
}