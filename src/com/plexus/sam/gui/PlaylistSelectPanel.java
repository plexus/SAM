/*
 * Created on 11-sep-2005
 *
 */
package com.plexus.sam.gui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SpringLayout;

import com.plexus.sam.SAM;
import com.plexus.sam.audio.Playlist;
import com.plexus.sam.audio.PlaylistSet;
import com.plexus.sam.gui.actions.DeletePlaylistAction;
import com.plexus.sam.gui.actions.NewPlaylistDialogAction;
import com.plexus.sam.gui.actions.PlaylistSelectedAction;


/**
 * A panel with a combobox containing the different playlists,
 * a new playlist and a delete playlist button.
 * 
 * This is the top part of the PlaylistEditPanel.
 *  
 * @author plexus
 *
 */
public class PlaylistSelectPanel extends JPanel implements ItemListener {
	private ResourceBundle i18n = SAM.getBundle("gui");
	private PlaylistBox comboBox;
	private JButton newPlaylist;
	private JButton removeSelected;
	private JLabel selectPlaylist;
	private JSplitPane parent;
	
	/**
	 * Create a playlistselectpanel with a certain parent splitpane
	 * 
	 * @param parent
	 */
	public PlaylistSelectPanel(JSplitPane parent) {
		this.parent = parent;
		Object[] names = SAM.playlists.getPlaylists().toArray();
		comboBox = new PlaylistBox( new PlaylistSelectedAction(parent, true, false) );
		
		newPlaylist = new JButton( new NewPlaylistDialogAction() );
		removeSelected = new JButton( new DeletePlaylistAction(comboBox) );
		
		selectPlaylist = new JLabel(i18n.getString("select_playlist"));
		
		SpringLayout layout = new SpringLayout();
		
		this.setLayout(layout);
		this.add(selectPlaylist);
		this.add(comboBox);
		this.add(removeSelected);
		this.add(newPlaylist);
		
		layout.putConstraint(SpringLayout.NORTH, selectPlaylist, 5, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.NORTH, comboBox, 5, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.NORTH, removeSelected, 5, SpringLayout.SOUTH, comboBox);
		layout.putConstraint(SpringLayout.NORTH, newPlaylist, 5, SpringLayout.SOUTH, comboBox);
		
		layout.putConstraint(SpringLayout.WEST, selectPlaylist, 5, SpringLayout.WEST, this);
		
		layout.putConstraint(SpringLayout.EAST, comboBox, -5, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.EAST, removeSelected, -5, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.EAST, newPlaylist, -5, SpringLayout.WEST, removeSelected);
		
		if (SAM.playlists.size() > 0) {
			if (SAM.player.getPlaylist() != null)
				comboBox.setSelectedItem(SAM.player.getPlaylist());
			else
				comboBox.setSelectedIndex(0);
		}
		
		SAM.playlists.addPropertyChangeListener(new SelectNewPlaylistListener());
		comboBox.addItemListener(this);
	}
	
	public Playlist getSelectedPlaylist() {
		return (Playlist)comboBox.getSelectedItem();
	}

	public class SelectNewPlaylistListener implements PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() instanceof PlaylistSet && evt.getPropertyName().equals("addPlaylist"))
				comboBox.setSelectedItem(evt.getNewValue());
			
		}
	}

	public void itemStateChanged(ItemEvent e) {
		if (comboBox.getSelectedIndex() == -1) {
			parent.setBottomComponent(((PlaylistPanel)parent).noPlaylistMessage);
		}
		
	}
}