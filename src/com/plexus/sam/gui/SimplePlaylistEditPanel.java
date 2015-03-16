/*
 * Created on 12-sep-2005
 *
 */
package com.plexus.sam.gui;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;

import com.plexus.sam.SAM;
import com.plexus.sam.audio.SimplePlaylist;
import com.plexus.sam.gui.actions.AddToSimplePlaylistAction;
import com.plexus.sam.gui.actions.MoveSongDownAction;
import com.plexus.sam.gui.actions.MoveSongUpAction;
import com.plexus.sam.gui.actions.RemoveFromSimplePlaylistAction;
import com.plexus.sam.gui.table.SimplePlaylistTable;

/**
 * Panel that allows the editing of a SimplePlaylist. The top part of the splitpane is
 * a SongBrowser, when you doubleclick the song is added to the end of the list.
 * The lower part of the splitpane consists of a table with the songs, and
 * a panel with add/remove/up/down buttons.
 *  
 * @author plexus
 *
 */
public class SimplePlaylistEditPanel extends JSplitPane implements MouseListener {
	private SimplePlaylist playlist;
	private JTable table;
	private SongBrowser browser;
	private JButton add, remove, up, down;
	
	/**
	 * Set the playlist to edit
	 * @param p
	 */
	public SimplePlaylistEditPanel(SimplePlaylist p) {
		this.playlist =  p;
		
		this.table = new SimplePlaylistTable (playlist);
		
		browser = new SongBrowser( SAM.repos );
		browser.addMouseListener(this);
		this.setTopComponent(browser);
		
		JPanel buttonPanel = new JPanel();
		add = new JButton(new AddToSimplePlaylistAction(browser, playlist));
		remove = new JButton(new RemoveFromSimplePlaylistAction(table, playlist));
		up = new JButton(new MoveSongUpAction(table, playlist));
		down = new JButton(new MoveSongDownAction(table, playlist));
		
		buttonPanel.add( add );
		buttonPanel.add( remove );
		buttonPanel.add( up );
		buttonPanel.add( down );
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout());
		bottomPanel.add(new JScrollPane(table), BorderLayout.CENTER);
		bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
		this.setBottomComponent(bottomPanel);
		
		this.setOrientation(JSplitPane.VERTICAL_SPLIT);
		this.setResizeWeight(0.5d);
	}

	/**
	 * Listen to the songlist for doubleclicks
	 * @param e 
	 */
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2)
			add.doClick();
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
