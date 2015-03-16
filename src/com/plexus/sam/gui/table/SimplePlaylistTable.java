/*
 * Created on 15-sep-2005
 *
 */
package com.plexus.sam.gui.table;

import java.util.ResourceBundle;

import javax.swing.JTable;

import com.plexus.sam.SAM;
import com.plexus.sam.audio.SimplePlaylist;
import com.plexus.sam.gui.models.SimplePlaylistTableModel;

/**
 * @author plexus
 *
 */
public class SimplePlaylistTable extends JTable {
	private ResourceBundle i18n = SAM.getBundle("gui");
	
	/**
	 * Create a new table to display and possibly alter a simple playlist
	 * 
	 * @param playlist playlist to display
	 */
	public SimplePlaylistTable(SimplePlaylist playlist) {
		super( new SimplePlaylistTableModel( playlist ) );
		
		columnModel.getColumn(0).setHeaderValue(i18n.getString("song_title"));
		columnModel.getColumn(1).setHeaderValue(i18n.getString("song_author"));
		columnModel.getColumn(2).setHeaderValue(i18n.getString("song_album"));
		columnModel.getColumn(3).setHeaderValue(i18n.getString("song_track"));
		columnModel.getColumn(4).setHeaderValue(i18n.getString("song_length"));
		columnModel.getColumn(5).setHeaderValue(i18n.getString("song_genre"));	
	}

}
