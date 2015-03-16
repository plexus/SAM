/*
 * Created on 12-sep-2005
 *
 */
package com.plexus.sam.gui;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.plexus.sam.SAM;
import com.plexus.sam.audio.SimplePlaylist;
import com.plexus.sam.gui.table.SimplePlaylistTable;

/**
 * Panel that displays a simple playlist in the form of a table, a user can't change the playlist
 * from here directly, it is possible to dubbleclick to start a certain song.
 * We also listen to the cursor of the playlist to highlight the currently playing song.
 * 
 * @author plexus
 *
 */
public class SimplePlaylistPanel extends JPanel implements PropertyChangeListener, MouseListener {
	private SimplePlaylistTable table;
	private SimplePlaylist playlist;
	
	/**
	 * Create a panel for a certain playlist
	 * @param p
	 */
	public SimplePlaylistPanel(SimplePlaylist p) {
		this.playlist = p;
		this.table = new SimplePlaylistTable( playlist );
		this.setLayout(new BorderLayout());
		this.add (new JScrollPane(table), BorderLayout.CENTER);
		playlist.addPropertyChangeListener(this);
		table.addMouseListener(this);
	}

	/**
	 * Listen to the cursor and set the current song as selected
	 * 
	 * @param evt
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("cursor"))
			table.getSelectionModel().setSelectionInterval( ((Integer)evt.getNewValue()).intValue(), ((Integer)evt.getNewValue()).intValue());	
	}

	/**
	 * Dubbleclick causes the player to start the selected song
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			SAM.player.playNow( playlist.get( table.getSelectedRow() ) );
			playlist.setCursor( table.getSelectedRow() );
		}
	}

	/**
	 * Not implemented
	 * @param e
	 */
	public void mousePressed(MouseEvent e) {
		//We only listen for mouseclicked events
	}
	
	/**
	 * Not implemented
	 * @param e
	 */
	public void mouseReleased(MouseEvent e) {
		//We only listen for mouseclicked events
	}
	
	/**
	 * Not implemented
	 * @param e
	 */
	public void mouseEntered(MouseEvent e) {
		//We only listen for mouseclicked events
	}
	
	/**
	 * Not implemented
	 * @param e
	 */
	public void mouseExited(MouseEvent e) {
		//We only listen for mouseclicked events
	}

}
