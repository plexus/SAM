/*
 * Created on 12-sep-2005
 *
 */
package com.plexus.sam.gui.actions;

import java.util.Iterator;
import java.util.List;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.plexus.sam.SAM;
import com.plexus.sam.audio.SimplePlaylist;
import com.plexus.sam.audio.Song;

/**
 * Remove the song currently selected in a table, from a playlist.
 * 
 * @author plexus
 */
public class RemoveFromSimplePlaylistAction extends AbstractAction implements ListSelectionListener {
	private JTable table;
	private SimplePlaylist playlist;
	
	/**
	 * @param table
	 * @param playlist
	 */
	public RemoveFromSimplePlaylistAction(JTable table, SimplePlaylist playlist) {
		this.table = table;
		this.playlist = playlist;
		this.putValue(NAME, SAM.getBundle("gui").getObject("remove_song"));
		table.getSelectionModel().addListSelectionListener(this);
		this.setEnabled(table.getSelectedRowCount() > 0);
	}

	/**
	 * Action : remove selected song from the playlist
	 */
	public void actionPerformed(ActionEvent e) {
		int[] indices = table.getSelectedRows();
		List toBeRemoved = new ArrayList();
		
		for(int i = 0; i < indices.length ; i++ ) 
			toBeRemoved.add(playlist.get(indices[i]));
		
		
		for (Iterator j = toBeRemoved.iterator() ; j.hasNext() ;)
			playlist.removeSong( (Song) j.next());
		
	}


	/**
	 * Disable the action when nothing is selected
	 */
	public void valueChanged(ListSelectionEvent e) {
		this.setEnabled(table.getSelectedRowCount() > 0);
	}

}
