/*
 * Created on 12-sep-2005
 *
 */
package com.plexus.sam.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.plexus.sam.SAM;
import com.plexus.sam.audio.SimplePlaylist;


/**
 * Action to move a song one place to the front of a SimplePlaylist
 * 
 * @author plexus
 */
public class MoveSongUpAction extends AbstractAction implements ListSelectionListener {
	private JTable table;
	private SimplePlaylist playlist;
	
	/**
	 * @param table
	 * @param playlist
	 */
	public MoveSongUpAction(JTable table, SimplePlaylist playlist) {
		this.table = table;
		this.playlist = playlist;
		this.putValue(NAME, SAM.getBundle("gui").getObject("up_song"));
		table.getSelectionModel().addListSelectionListener(this);
		this.setEnabled(table.getSelectedRowCount() == 1 && table.getSelectedRow() > 0);
	}

	/**
	 * Action : move selected song to the front of the playlist
	 */
	public void actionPerformed(ActionEvent e) {
		int index = table.getSelectedRow();
		playlist.add(index - 1, playlist.get(index));
		playlist.remove(index + 1);
		table.getSelectionModel().setSelectionInterval(index-1, index-1);
	}


	/**
	 * Enable the action when there is a single song selected which is not the first song.
	 */
	public void valueChanged(ListSelectionEvent e) {
		this.setEnabled(table.getSelectedRowCount() == 1 && table.getSelectedRow() > 0);
	}

}
