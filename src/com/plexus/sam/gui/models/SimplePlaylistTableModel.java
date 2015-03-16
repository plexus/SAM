/*
 * Created on 12-sep-2005
 *
 */
package com.plexus.sam.gui.models;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.table.AbstractTableModel;

import com.plexus.sam.audio.SimplePlaylist;
import com.plexus.sam.audio.Song;

/**
 * TableModel to display a SimplePlaylist.
 * 
 * @author plexus
 *
 */
public class SimplePlaylistTableModel extends AbstractTableModel implements PropertyChangeListener {
	private SimplePlaylist playlist;
	
	/**
	 * Create the model for a playlist
	 * 
	 * @param playlist
	 */
	public SimplePlaylistTableModel(SimplePlaylist playlist) {
		this.playlist = playlist;
		playlist.addPropertyChangeListener(this);
	}
	
	/**
	 * @return the number of rows in the model, this is the number of songs in the playlist
	 */
	public int getRowCount() {
		return playlist.size();
	}

	
	/**
	 * @return the number of columns, this is 6
	 */
	public int getColumnCount() {
		return 6;
	}

	
	/**
	 * Return the object to be displayed in a certain position of the table
	 * @param rowIndex row index = song index
	 * @param columnIndex 
	 * <ul>
	 * <li>O : Title</li>
	 * <li>1 : Author</li>
	 * <li>2 : Album</li>
	 * <li>3 : Track</li>
	 * <li>4 : Length (min:sec)</li>
	 * <li>5 : Genre</li>
	 * </ul>
	 * @return value at the specified position
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		Song s = playlist.get(rowIndex);
		switch(columnIndex) {
			case 0 :
				return s.getTitle();
			case 1 :
				return s.getAuthor();
			case 2 :
				return s.getAlbum();
			case 3 :
				return new Integer(s.getTrack());
			case 4 :
				long ms = s.getLength();
				int min = (int) Math.floor(ms / 60000);
				int sec = Math.round((ms % 60000)/1000);
				return min+":"+(sec < 10 ? "0" :"")+sec;
			case 5 :
				return s.getGenre();
				//TODO: check for numeric genre id and convert to string
			default :
				return "";
		}
	}

	/**
	 * We listen to the playlist for changes
	 * @param evt event transmitted when the playlist changes
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource().equals(this.playlist))
			this.fireTableDataChanged();
	}

}
