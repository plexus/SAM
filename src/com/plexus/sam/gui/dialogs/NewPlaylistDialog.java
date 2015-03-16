/*
 * Created on 11-sep-2005
 *
 */
package com.plexus.sam.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.plexus.sam.SAM;
import com.plexus.sam.audio.Playlist;
import com.plexus.sam.config.StaticConfig;
import com.plexus.sam.gui.NamedClass;
import com.plexus.sam.gui.actions.CancelDialogAction;

/**
 * Dialog that asks for the name and type for a new playlist
 * 
 * @author plexus
 *
 */
public class NewPlaylistDialog extends JDialog {

	private Frame parent;
	private static ResourceBundle i18n = SAM.getBundle("gui");
	
	/**
	 * Give the parent component to display a dialog.
	 * 
	 * @param parent
	 */
	public NewPlaylistDialog (Frame parent) {
		this.parent = parent;
		makeGui();
		this.setVisible(true);
	}

	/**
	 * The field where the playlist name is typed
	 */
	public JTextField nameField;
	
	/**
	 * The combobox where a playlisttype is selected
	 */
	public JComboBox playlistTypeBox;
	private JButton ok, cancel;
	
	
	/**
	 * Fill the dialog with the necessary widgets
	 *
	 */
	public void makeGui() {
		//Get a new Dialog
		this.setTitle(i18n.getString("new_playlist_dialog_title"));
		this.setSize(600,100);
		this.setResizable(false);
		this.setLocation((parent.getWidth()-600)/2 , (parent.getHeight()-100)/2);
		//put a label on it
		this.add(new JLabel(i18n.getString("new_playlist_label")),BorderLayout.NORTH);
		
		//The textfield for the name
		nameField = new JTextField();
		Box centerPanel = Box.createVerticalBox();
		centerPanel.add(new JLabel());
		centerPanel.add(nameField, BorderLayout.CENTER);
		centerPanel.add(Box.createGlue());
		this.add(centerPanel, BorderLayout.CENTER);
		
		Box subPanel = Box.createHorizontalBox();
		//Then the combobox with the types
		Class[] playlistTypes = StaticConfig.playlistClasses();
		NamedClass[] typeNames = new NamedClass[playlistTypes.length];
		
		for (int i = 0 ; i < playlistTypes.length ; i++) {
			typeNames[i] = new NamedClass( playlistTypes[i] );
		}
		
		DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel(typeNames);
		
		playlistTypeBox = new JComboBox( comboBoxModel );
		
		subPanel.add(playlistTypeBox);
		
		//The ok/cancel
		ok = new JButton(new CreatePlayListAction());
		cancel = new JButton(new CancelDialogAction(this));
		
		subPanel.add(ok);
		subPanel.add(cancel);
		
		this.add(subPanel, BorderLayout.SOUTH);
	}
	
	/**
	 * 
	 * @return the interantionalization bundle
	 */
	public ResourceBundle i18n() {
		return i18n;
	}
	/**
	 * 
	 * @author plexus
	 *
	 */
	private class CreatePlayListAction extends AbstractAction {
		/**
		 * Action that creates a new playlist
		 *
		 */
		public CreatePlayListAction() {
			this.putValue(NAME, i18n().getString("ok"));
		}
		
		/**
		 * Action : Create a new playlist
		 * 
		 * @param e
		 */
		public void actionPerformed(ActionEvent e) {
			if (! nameField.getText().equals("") ) {
				NamedClass plClass = (NamedClass)playlistTypeBox.getSelectedItem();
				try {
					String name = nameField.getText();
					Playlist pl = (Playlist)plClass.theClass().newInstance();
					int i = 0;
					if (SAM.playlists.getPlaylist(name) != null) {
						while (SAM.playlists.getPlaylist(name+(i)) != null) {
							i++;
						}
						pl.setName(name+i);
					} else {
						pl.setName(name);
					}
					SAM.playlists.addPlaylist(pl);
					SAM.playlists.save();
				} catch (InstantiationException e1) {
					SAM.error("failed_creating_playlist", e1);
				} catch (IllegalAccessException e1) {
					SAM.error("failed_creating_playlist", e1);
				}
			}
			setVisible(false);
		}
	}
}
