/*
 * Created on 8-sep-2005
 *
 */
package com.plexus.sam.gui.models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import com.plexus.sam.audio.DynamicPlaylist;
import com.plexus.sam.audio.SongGroup;

/**
 * This model is used in the GUI to edit and display a dynamic playlist.
 * It is possible to set and get the name and relevance properties, 
 * and listeners are being notified of changes in these properties.
 * 
 * @author plexus
 *
 */
public class DynamicPlaylistModel {
	/**
	 * Our changelisteners
	 */
	private PropertyChangeSupport listeners;
	
	/**
	 * The playlist this models shows/changes
	 */
	private DynamicPlaylist playlist;
	

	/**
	 * Create a model for the playlist
	 * 
	 * @param playlist
	 */
	public DynamicPlaylistModel(DynamicPlaylist playlist) {
		this.listeners = new PropertyChangeSupport(this);
		this.playlist = playlist;
	}
	
	//************************* playlist properties *********************
	/**
	 * @return the name of the playlist
	 */
	public String getName() {
		return playlist.getName();
	}
	/**
	 * @param sg
	 * @return relevance of a given songgroup
	 */
	public float getRelevance(SongGroup sg) {
		return playlist.getRelevance(sg);
	}
	/**
	 * @param s name to set for the playlist
	 */
	public void setName(String s) {
		String oldName = playlist.getName();
		playlist.setName(s);
		listeners.firePropertyChange("name", oldName, s);
	}
	/**
	 * Set the relevance for a songgroup
	 * 
	 * @param sg 
	 * @param f
	 */
	public void setRelevance(SongGroup sg, float f) {
		float oldRelevance = playlist.getRelevance(sg);
		playlist.setRelevance(sg, f);
		listeners.firePropertyChange("relevance["+sg.getName()+"]", new Float(oldRelevance), new Float(f));
	}
	
	//******************* propertychangelistenersupport ****************
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

}
