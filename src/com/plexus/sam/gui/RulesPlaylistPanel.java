/*
 * Created on 29-sep-2005
 *
 */
package com.plexus.sam.gui;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import com.plexus.sam.audio.RulesPlaylist;
import com.plexus.sam.gui.table.SongEventTable;

/**
 * 
 * @author plexus
 *
 */
public class RulesPlaylistPanel extends JSplitPane {
	private RulesPlaylist playlist;
	
	/**
	 * 
	 * @param playlist
	 */
	public RulesPlaylistPanel (RulesPlaylist playlist) {
		this.playlist = playlist;
		this.setLeftComponent(new DynamicPlaylistPanel(playlist));
		this.setRightComponent(new JScrollPane(new SongEventTable(playlist)));
		this.setResizeWeight(0.5d);
	}
}
