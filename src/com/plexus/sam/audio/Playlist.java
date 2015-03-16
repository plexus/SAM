/*
 * Created on 2-sep-2005
 *
 */
package com.plexus.sam.audio;

import java.beans.PropertyChangeListener;

/**
 * Common interface for the different playlist implementations
 * Playlists should keep a pointer of where in the playlist
 * the player currently is. <br /> <br /> 
 * the nextSong/previousSong return a Song but don't change the 
 * position. This is done when the player calls ( gotoNext()/gotoPrevious() ).
 * 
 * @author plexus
 */
public interface Playlist extends PropertyChangeListener {
	/**
	 * Will the next call to nextSong() return a new Song
	 * 
	 * @return false if the playlist doesn't have a next song
	 */
	public boolean hasNext();
	
	/**
	 * Get the next song from the playlist
	 *
	 * @return next song
	 */
	public Song nextSong();
	
	/**
	 * Does the playlist have a previous song
	 * 
	 * @return true if previousSong returns a usefull value
	 */
	public boolean hasPrevious();
	
	/**
	 * Get the previous song from the playlist
	 * 
	 * @return previous song
	 */
	public Song previousSong();
	
	/**
	 * Get the name of this playlist
	 * @return playlist name
	 */
	public String getName();
	
	/**
	 * Set the name of this playlist
	 * 
	 * @param name name of the playist
	 */
	public void setName(String name);
	
	/**
	 * The class that is used as view/controller
	 * for this playlist. This should be an
	 * extension of JPanel
	 * 
	 * @return the subclass of {@link javax.swing.JPanel} that can displays information about this playist 
	 */
	public Class getPanelClass();
	
	/**
	 * The class that is used as an editor for
	 * this type of playlist
	 * 
	 * @return the subclass of {@link javax.swing.JPanel} that can edit this playist
	 */
	public Class getEditPanelClass();
	
	/**
	 * Traverse backwards (usually means the user pressed 'previous')
	 */
	public void gotoPrevious();
	
	/**
	 * Traverse forward (usually means the user pressed 'next')
	 *
	 */
	public void gotoNext();
	
	/**
	 * Add a propertyChangeListener to be notified when this playlist changes
	 * 
	 * @param listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener);
	
	/**
	 * Add a propertyChangeListener to be notified when this playlist changes
	 * @param propertyName
	 * @param listener
	 */
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener);
	
	/**
	 * Remove a specific listener
	 * 
	 * @param listener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener);
	
	/**
	 * Remove a specific listener
	 * 
	 * @param propertyName
	 * @param listener
	 */
	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener);
	
	/**
	 * Clean up resources and notify listeners this playlist is being deleted.
	 *
	 */
	public void delete();
}
