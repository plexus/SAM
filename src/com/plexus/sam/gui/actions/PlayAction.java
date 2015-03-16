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
 * Action to start the player
 *
 */
public class PlayAction extends PlayerAction {
	/**
	 * @param player to act upon
	 *
	 */
	public PlayAction(Player player) {
		super(player, "play");
	}
	
	/**
	 * Action : start playback
	 */
	public void actionPerformed(ActionEvent e) {
		player.play();
	}

	protected boolean enabled() {
		return player.getPlaylist() !=null && !player.isPlaying() && (player.getCurrent() != null || player.hasNext());
	}
}
