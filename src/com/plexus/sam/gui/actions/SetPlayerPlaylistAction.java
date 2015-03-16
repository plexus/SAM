/*
 * Created on 11-sep-2005
 *
 */
package com.plexus.sam.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.plexus.sam.SAM;
import com.plexus.sam.audio.Playlist;
import com.plexus.sam.gui.PlaylistBox;

/**
 * Set the playlist in the player.
 * 
 * @author plexus
 *
 */
public class SetPlayerPlaylistAction extends AbstractAction {
	private Playlist pl;
	private PlaylistBox plBox;
	
	/**
	 * Always set the same playlist
	 * @param pl
	 */
	public SetPlayerPlaylistAction(Playlist pl) {
		this.pl=pl;
	}
	
	/**
	 * Get the playlist from a PlaylistBox
	 * @param plBox
	 */
	public SetPlayerPlaylistAction(PlaylistBox plBox) {
		this.plBox = plBox;
	}
	
	/**
	 * Action: set the playlist
	 */
	public void actionPerformed(ActionEvent e) {
		if (pl != null) 
			SAM.player.setPlaylist(pl);
		else if (plBox != null && plBox.getSelectedItem() != null)
			SAM.player.setPlaylist((Playlist)plBox.getSelectedItem());

	}

}
