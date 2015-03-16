/*
 * Created on 3-sep-2005
 *
 */
package com.plexus.sam.audio;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.plexus.sam.SAM;
import com.plexus.sam.gui.SimplePlaylistEditPanel;
import com.plexus.sam.gui.SimplePlaylistPanel;

/**
 * A simple implementation of a playlist which is simply a list of songs
 * that can be traversed in both directions
 * 
 * @author plexus
 *
 */
public class SimplePlaylist implements Playlist {
	/**
	 * The songs in the playlist
	 */
	private List songs;
	
	/**
	 * We need listener support for interacting with the gui
	 */
	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	
	/**
	 * Cursor pointing to the currently playing song.
	 * The cursor ranges from -1 to songs.size()+1
	 * It points to the currently playing song,
	 * but can also be before the first or after the last song.
	 * <br /> <br />
	 * Previous returns the song before the cursor
	 * next returns the song after the cursor
	 */
	private int cursor = -1;

	/**
	 * The name of the playlist
	 */
	private String name;

	/**
	 * En/disable debugging
	 */
	private boolean DEBUG=false;
	
	/**
	 * Default constructor
	 */
	public SimplePlaylist() {
		songs = new ArrayList();
	}
	
	/**
	 * Add a song
	 * @param s
	 */
	public void addSong(Song s) {
		songs.add(s);
		listeners.firePropertyChange("addSong", null, s);
	}
	
	/**
	 * remove a song
	 * 
	 * @param s
	 */
	public void removeSong(Song s) {
		songs.remove(s);
		listeners.firePropertyChange("removeSong", s, null);
	}
	
	/**
	 * is there still a next song?
	 * 
	 * @return true when a next song is available 
	 */
	public boolean hasNext() {
		return cursor < songs.size()-1 && ! songs.isEmpty();
	}

	/**
	 * is there still a previous song?
	 * 
	 * @return true when a previous song is available 
	 */
	public boolean hasPrevious() {
		return cursor > 0 && ! songs.isEmpty();
	}

	/**
	 * Get the next song in the playlist
	 * 
	 * @return the song after the cursor
	 */
	public Song nextSong() {
		debug ("nextSong : "+songs.get(cursor+1));
		return (Song)songs.get(cursor+1);
	}
	
	/**
	 * Get the previous song
	 * 
	 * @return the song before the cursor
	 */
	public Song previousSong() {
		debug ("previousSong : "+songs.get(cursor-1));
		return (Song)songs.get(cursor-1);
	}

	/**
	 * Get the name of this playlist
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of this playlist
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Get the identifiers of the songs in this playlist
	 * 
	 * @return a new array
	 */
	public long[] getSongIds() {
		long[] ids = new long[songs.size()];
		int count = 0;
		for (Iterator i = songs.iterator() ; i.hasNext(); ) {
			ids[count++] = ((Song)i.next()).getId();
		}
		return ids;
	}
	
	/**
	 * Set the songs in this playlist by their ids
	 * 
	 * @param ids
	 */
	public void setSongIds(long[] ids) {
		for (int i = 0; i<ids.length ; i++) {
			Song s = SAM.repos.getSong(ids[i]);
			if (s != null)
				songs.add(SAM.repos.getSong(ids[i]));
		}
		listeners.firePropertyChange("setSongIds", null, null);
	}
	
	/**
	 * output a debug message
	 * 
	 * @param string
	 */
	private void debug(String string) {
		if (DEBUG=false)
			System.out.println("simpleplaylist "+string);
	}
	
	/**
	 * Add a song at a specific position
	 * 
	 * @param position
	 * @param song
	 */
	public void add(int position, Song song) {
		songs.add(position, song);
		listeners.firePropertyChange("addSong", null, song);
	}
	
	/**
	 * Does the playlist contain the specified song?
	 * 
	 * @param s
	 * @return
	 */
	public boolean contains(Song s) {
		return songs.contains(s);
	}
	/**
	 * Get a song at a specific index
	 * 
	 * @param index between 0 and size-1
	 * @return 
	 */
	public Song get(int index) {
		return (Song)songs.get(index);
	}
	
	/**
	 * is the playlist empty?
	 * 
	 * @return true if the playlist is empty
	 */
	public boolean isEmpty() {
		return songs.isEmpty();
	}
	
	/**
	 * Get the size of the playlist
	 * 
	 * @return the number of songs in the playlist
	 */
	public int size() {
		return songs.size();
	}
	
	/**
	 * Implementation of Object
	 */
	public String toString() {
		return name == null ? "Unnamed playlist" : name;
	}

	
	public Class getPanelClass() {
		return SimplePlaylistPanel.class;
	}

	public Class getEditPanelClass() {
		return SimplePlaylistEditPanel.class;
	}
	
	/**
	 * @param listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
	}
	/**
	 * @param propertyName
	 * @param listener
	 */
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(propertyName, listener);
	}
	/**
	 * @param listener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);
	}
	/**
	 * @param propertyName
	 * @param listener
	 */
	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(propertyName, listener);
	}
	
	/**
	 * @param o
	 * @return
	 */
	public int lastIndexOf(Object o) {
		return songs.lastIndexOf(o);
	}

	/**
	 * Traverse backwards to the previous song
	 */
	public void gotoPrevious() {
		if (hasPrevious()) {
			cursor--;
			listeners.firePropertyChange("cursor", cursor+1, cursor);
		}
	}

	/**
	 * Traverse forward to the next song 
	 */
	public void gotoNext() {
		if (hasNext()) {
			cursor++;
			listeners.firePropertyChange("cursor", cursor-1, cursor);
		}		
	}
	
	/**
	 * Remove a song at a specific position
	 * 
	 * @param index
	 * @return
	 */
	public Object remove(int index) {
		return songs.remove(index);
	}

	/**
	 * Notify listeners we're being deleted
	 */
	public void delete() {
		listeners.firePropertyChange("delete", this, null);
	}

	/**
	 * Set the cursor pointing to the current song
	 * @param cursor the new cursor
	 */
	public void setCursor(int cursor) {
		int oldCursor = cursor;
		this.cursor = cursor;
		listeners.firePropertyChange("cursor", oldCursor, cursor);
	}

	public void propertyChange(PropertyChangeEvent evt) {
	}
}
