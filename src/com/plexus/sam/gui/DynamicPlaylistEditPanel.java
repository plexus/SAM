/*
 * Created on 8-sep-2005
 *
 */
package com.plexus.sam.gui;

import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import javax.swing.JPanel;

import com.plexus.sam.SAM;
import com.plexus.sam.audio.DynamicPlaylist;
import com.plexus.sam.audio.SongGroup;
import com.plexus.sam.gui.models.DynamicPlaylistModel;

/**
 * Panel used to edit a playlist of type DynamicPlaylist. It consists
 * of sliders for each songgroup which set the relevance.
 * 
 * @author plexus
 *
 */
public class DynamicPlaylistEditPanel extends JPanel implements PropertyChangeListener {
	/**
	 * The model that shows and edits the playlist
	 */
	private DynamicPlaylistModel model;
	
	/**
	 * Set the playlist to edit with this panel
	 * 
	 * @param pl
	 */
	public DynamicPlaylistEditPanel(DynamicPlaylist pl) {
		this.model = pl.getModel();
		if (SAM.repos != null) {
			repaintNow();
			SAM.repos.addPropertyChangeListener(this);
		}
		
	}

	/**
	 * When groups are added or removed, we have to repaint ourselves
	 * @param evt
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("synchronize"))
			repaintNow();
	}
	
	/**
	 * Repaint our panel
	 */
	private void repaintNow() {
		this.removeAll();
		this.setLayout(new GridLayout(SAM.repos.getSongGroupNames().size(), 1));
		for (Iterator i = SAM.repos.getSongGroupNames().iterator() ; i.hasNext() ; ) {
			String sgName = (String)i.next();
			SongGroup sg = SAM.repos.getSongGroup( sgName );
			this.add(new GroupRelevanceSlider(sg, model));
		}
	}
}
