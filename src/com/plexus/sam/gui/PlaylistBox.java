/*
 * Created on 11-sep-2005
 *
 */
package com.plexus.sam.gui;

import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import com.plexus.sam.SAM;

/**
 * A combobox that shows the current playlist, and executes an action when
 * a playlist is selected.
 * 
 * @author plexus
 *
 */
public class PlaylistBox extends JComboBox {
	
	public PlaylistBox(ActionListener listener) {
		super(SAM.playlists.getPlaylists().toArray());
		SAM.playlists.addPropertyChangeListener(new PlaylistSetListener());
		this.addActionListener(listener);
	}
	
	private class PlaylistSetListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals("addPlaylist")) {
				( (DefaultComboBoxModel)getModel() ).addElement( evt.getNewValue() );
			}
			else if (evt.getPropertyName().equals("removePlaylist"))
				( (DefaultComboBoxModel)getModel() ).removeElement( evt.getOldValue() );
			else if (evt.getPropertyName().equals("playlists")) {
				Object[] names = SAM.playlists.getPlaylists().toArray();
				setModel(new DefaultComboBoxModel(names));
			}
		}
	}
}
