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
 * Let the player play the next song. Listen to the player to enable/disable.
 *
 */
public class NextAction extends PlayerAction {
	/**
	 * Default constructor
	 */
	public NextAction(Player player) {
		super(player, "next");
	}
	
	/**
	 * Action : player.next()
	 */
	public void actionPerformed(ActionEvent e) {
		player.next();
	}

	/**
	 * @return true when a playlist is set which has a next song
	 */
	protected boolean enabled() {
		return (player.getPlaylist() != null) && (player.hasNext());
	}
}
