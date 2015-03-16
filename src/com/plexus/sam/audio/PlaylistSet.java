/*
 * Created on 2-sep-2005
 *
 */
package com.plexus.sam.audio;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import com.plexus.sam.SAM;
import com.plexus.sam.config.ConfigGroup;
import com.plexus.sam.config.Configuration;

/**
 * This is a container class that bundles the playlists, and has methods for loading and
 * saving, analogeous to repository saving the songs and songgroups.  
 * 
 * @author plexus
 */
public class PlaylistSet {
	/**
	 * List with playlists
	 */
	private List<Playlist> playlists;
	
	/**
	 * Bundle of error messages
	 */
	private ResourceBundle error = ResourceBundle.getBundle("com.plexus.sam.i18n.error");
	
	/**
	 * General configuration
	 */
	private ConfigGroup general = Configuration.getConfigGroup("general");
	
	/**
	 * Change listener support
	 */
	private PropertyChangeSupport listeners;
	
	//************************             ****************************
	/**
	 * Default constructor
	 */
	public PlaylistSet() {
		playlists = new ArrayList<Playlist>();
		listeners = new PropertyChangeSupport(this);
	}
	
	/**
	 * Load the playlists from disk
	 */
	public void load() {
		XMLDecoder d;
		try {
			d = new XMLDecoder(
			        new BufferedInputStream(
			            new FileInputStream(
			            		general.get("playlists_xml_path"))));
			try {				
				Playlist pl;
				while (true) {
					pl = (Playlist) d.readObject();
					addPlaylist (pl);
				}
				
			} catch(ArrayIndexOutOfBoundsException e) {
				//End of XML file
				d.close();
				listeners.firePropertyChange("init", false, true);
			}
		} catch (FileNotFoundException e) {
			listeners.firePropertyChange("init", false, true);
			SAM.error("bad_playlists_xml_path", e);
		}
	}
	
	/**
	 * Save the playlists to disk
	 * @throws  
	 *
	 */
	public void save() {
		   XMLEncoder e;
		   try {
				e = new XMLEncoder(
				        new BufferedOutputStream(
				            new FileOutputStream(general.get("playlists_xml_path"))));
				
							
				for (Iterator i = playlists.iterator() ; i.hasNext() ; ) {
			    	Playlist pl = (Playlist)i.next();
			    			    	e.writeObject(pl);
			    }
			    e.close();
			} catch (FileNotFoundException ex) {
				SAM.error(error.getString("bad_playlists_xml_path_save"), ex);
			}
		}
	
	
	/**
	 * @return Returns the playlists.
	 */
	public List<Playlist> getPlaylists() {
		return playlists;
	}
	
	/**
	 * @param playlists The playlists to set.
	 */
	public void setPlaylists(List<Playlist> playlists) {
		List oldList = this.playlists;
		this.playlists = playlists;
		listeners.firePropertyChange("playlists", oldList, playlists);
	}
	
	/**
	 * Add a playlist at a specific position
	 * 
	 * @param position
	 * @param playlist
	 */
	public void addPlaylist(int position, Playlist playlist) {
		playlists.add(position, playlist);
		playlist.addPropertyChangeListener(new PlaylistChangeListener(playlist));
		listeners.firePropertyChange("addPlaylist", null, playlist);
	}
	
	/**
	 * Add a playlist to the end of the list
	 * @param playlist
	 * @return Returns: true (as per the general contract of the Collection.add method)
	 */
	public boolean addPlaylist(Playlist playlist) {
		boolean b = playlists.add(playlist);
		playlist.addPropertyChangeListener(new PlaylistChangeListener(playlist));
		listeners.firePropertyChange("addPlaylist", null, playlist);
		return b;
	}
	
	/**
	 * Get a playlist at a specific position
	 *  
	 * @param index
	 * @return the playlist with the given index
	 */
	public Playlist getPlaylist(int index) {
		return playlists.get(index);
	}
	
	/**
	 * Get a playlist with a certain name
	 * 
	 * @param name
	 * @return null when no such playlist is found
	 */
	public Playlist getPlaylist(String name) {
		for (Playlist p : playlists) {
			if (p.getName().equals(name))
				return p;
		}
		return null;
	}
	
	
	
	/**
	 * Are there any playlists?
	 * 
	 * @return true when there are no playlists in the set
	 */
	public boolean isEmpty() {
		return playlists.isEmpty();
	}
	
	/**
	 * Remove a playlist a specific position
	 * 
	 * @param index
	 * @return the element that was removed
	 */
	public Playlist remove(int index) {
		Playlist pl = playlists.remove(index);
		listeners.firePropertyChange("removePlaylist", pl, null);
		pl.delete();
		return playlists.remove(index);
	}
	
	/**
	 * Remove a playlist
	 *  
	 * @param p
	 * @return true if the playlist was present in the set
	 */
	public boolean remove(Playlist p) {
		boolean b = playlists.remove(p);
		listeners.firePropertyChange("removePlaylist", p, null);
		p.delete();
		return b;
	}
	
	/**
	 * Number of available playlists
	 * @return size
	 */
	public int size() {
		return playlists.size();
	}
	
	/**
	 * This is called by a Playlist to notify us (and our listeners) the playlist
	 * has changed.
	 * 
	 * @param p
	 */
	public void playlistChanged(Playlist p) {
		listeners.firePropertyChange("playlist_change", null, p);
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
	 * Cascade propertyChangeEvent from the playlists to our own listeners
	 */
	private class PlaylistChangeListener implements PropertyChangeListener {
		private Playlist pl;
		
		/**
		 * Set the playlist where this object will be set to listen to
		 * 
		 * @param pl
		 */
		public PlaylistChangeListener(Playlist pl) {
			this.pl = pl;
		}
		
		/**
		 * @param evt
		 */
		public void propertyChange(PropertyChangeEvent evt) {
			playlistChanged(pl);
		}
	}
}
