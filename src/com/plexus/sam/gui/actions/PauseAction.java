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
 * Pause playback. A call to play() will resume the player at the same position in the song.
 * Disable when not playing.
 */
public class PauseAction extends PlayerAction {
	/**
	 * Player to act upon
	 * @param player
	 */
	public PauseAction(Player player) {
		super(player, "pause");
	}
	
	/**
	 * Action : Pause
	 */
	public void actionPerformed(ActionEvent e) {
		player.pause();
	}

	/**
	 * 
	 */
	protected boolean enabled() {
		return player.isPlaying();
	}

	
}
