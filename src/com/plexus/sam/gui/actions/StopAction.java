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
 * Stop playback. Disable when not playing.
 *
 */
public class StopAction extends PlayerAction {
	/**
	 * Player to act upon
	 * @param player
	 */
	public StopAction(Player player) {
		super(player, "stop");
	}
	
	/**
	 * Action : stop
	 */
	public void actionPerformed(ActionEvent e) {
		player.stop();
	}

	/**
	 * Listen to player for en/disable
	 */
	protected boolean enabled() {
		return player.isPlaying();
	}

}
