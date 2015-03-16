/*
 * Created on 11-sep-2005
 *
 */
package com.plexus.sam.gui.actions;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;

import com.plexus.sam.SAM;
import com.plexus.sam.audio.Playlist;
import com.plexus.sam.gui.PlaylistBox;

/**
 * @author plexus
 *
 */
public class DeletePlaylistAction extends AbstractAction {
	private PlaylistBox box;
	private ResourceBundle i18n = SAM.getBundle("gui");
	
	public DeletePlaylistAction(PlaylistBox box) {
		this.box = box;
		this.putValue(NAME,i18n.getString("remove_selected_playlist"));
		this.putValue(SHORT_DESCRIPTION, i18n.getString("remove_selected_playlist_tooltip"));
	}

	public void actionPerformed(ActionEvent e) {
		if (box.getSelectedItem() != null)
			SAM.playlists.remove((Playlist)box.getSelectedItem());
	}

}
