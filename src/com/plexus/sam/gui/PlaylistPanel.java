/*
 * Created on 8-sep-2005
 *
 */
package com.plexus.sam.gui;


import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import com.plexus.sam.SAM;
import com.plexus.sam.config.ConfigGroup;
import com.plexus.sam.config.Configuration;


/**
 * @author plexus
 *
 */
public class PlaylistPanel extends JSplitPane {
	private PlaylistSelectPanel topPanel;
	public JTextArea noPlaylistMessage;
	
	/**
	 * Construct a new panel that can show and edit playlists 
	 *
	 */
	public PlaylistPanel() {
		this.noPlaylistMessage = new JTextArea(SAM.getBundle("gui").getString("no_playlist_message"));
		this.noPlaylistMessage.setEditable(false);
		this.noPlaylistMessage.setLineWrap(true);
		
		topPanel = new PlaylistSelectPanel(this);
		this.setOrientation(JSplitPane.VERTICAL_SPLIT);
		this.setTopComponent(topPanel);
		//this.setEnabled(false);
		this.setDividerLocation(110);
		this.setResizeWeight(.5);
		this.setContinuousLayout(true);
		
		if (topPanel.getSelectedPlaylist() == null)
			this.setBottomComponent(this.noPlaylistMessage);
		
		ConfigGroup configGroup = Configuration.getConfigGroup("gui");
		try {
			setDividerLocation(Integer.parseInt(configGroup.get("playlistpanel_dividerlocation")));
		} catch (NumberFormatException e) {
			//Default is good enough for starters
		}
	}

}
