/*
 * Created on 8-sep-2005
 *
 */
package com.plexus.sam.audio;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;

import com.plexus.sam.SAM;
import com.plexus.sam.gui.DynamicPlaylistEditPanel;
import com.plexus.sam.gui.DynamicPlaylistPanel;
import com.plexus.sam.gui.models.DynamicPlaylistModel;

/**
 * The dynamic playlist uses a policy rather than a real list to determine the
 * next songs to be played. <br />
 * <br />
 * For every songgroup a relevance value can be set, the playlist will pick
 * songs randomly, but the odds are determined by the relevance factors. <br />
 * <br />
 * A relevance factor of 0 means no songs will be picked.
 * 
 * @author plexus
 *  
 */
public class DynamicPlaylist implements Playlist, PropertyChangeListener {
	/**
	 * Increasing this will slow down the algorithm but make the picking of
	 * songs more accuratly represent the relevance factors
	 */
	private static final int ARRAY_SIZE = 100;

	/**
	 * A map from SongGroup to Float
	 */
	private Map<SongGroup, Float> relevance;

	/**
	 * The sum of all relevance factors
	 */
	private float relevanceSum = 0f;

	/**
	 * Random generator to pick a songgroup and pick a song
	 */
	private Random rg;

	/**
	 * This array is filled with songgroups, so we can pick one randomly
	 * according to the relevance of each
	 */
	private SongGroup[] chanceArray;

	/**
	 * We keep a reference to the next song to easily return it, when the song
	 * starts playing we are notified, and calculate a new value
	 */
	protected Song nextSong;

	/**
	 * The name of this songgroup
	 */
	private String name;

	/**
	 * Debugging on/off
	 */
	private boolean DEBUG = false;

	/**
	 * The GUI model
	 */
	private DynamicPlaylistModel model = null;

	/**
	 * Notify of changes
	 */
	protected PropertyChangeSupport propertyListeners;
	
	/**
	 * ResourceBundle for i18n purposes
	 */
	private ResourceBundle i18n = SAM.getBundle("sam");

	/**
	 * Array containing the last songs returned
	 */
	private Song[] recentSongs = new Song[3];
	
	/**
	 * A counter that points to the least recent song in the recentSongs array
	 */
	private int recentSongCounter = 0;

	private boolean alwaysValid;
	/**
	 * Default constructor
	 *  
	 */
	public DynamicPlaylist() {
		debug("default constructor");
		relevance = new HashMap<SongGroup, Float>();
		chanceArray = new SongGroup[ARRAY_SIZE];
		rg = new Random();
		propertyListeners = new PropertyChangeSupport(this);
		SAM.repos.addPropertyChangeListener(this);
	}

	/**
	 * Every time the map with relevances changes this method should be called
	 *  
	 */
	private void relevancesChanged() {
		debug("relevances changed");
		calculateSum();
		if (relevanceSum > 0) {
			int counter = 0;
			for (Iterator i = relevance.keySet().iterator(); i.hasNext();) {
				SongGroup sg = (SongGroup) i.next();
				if (sg.size() > 0) {
					Float rel = relevance.get(sg);

					int chance = Math.round(ARRAY_SIZE
							* (rel.floatValue() / relevanceSum));
					debug("songgroup " + sg + " gets " + chance
							+ " places in the chancearray");
					for (int j = counter; (j < (counter + chance))
							&& (j < ARRAY_SIZE - 1); j++) {
						chanceArray[j] = sg;
					}
					counter += chance;
				} else {
					debug("empty songgroup " + sg.getName());
				}
			}
		}
		determineAlwaysValid();
		determineNext();
	}

	/**
	 * Calculate the sum of all relevance factors
	 */
	private void calculateSum() {
		relevanceSum = 0;
		for (Iterator i = relevance.keySet().iterator(); i.hasNext();) {
			SongGroup sg = (SongGroup) i.next();
			if (sg.size() > 0) {
				Float f = relevance.get(sg);
				relevanceSum += f.floatValue();
			}
		}
		debug("sum of relevances " + relevanceSum);
	}

	/**
	 * Set the next song according to the relevance factors
	 */
	private void determineNext() {
		debug("determineNext");
		if (relevanceSum != 0f) {
			SongGroup sg = chanceArray[rg.nextInt(ARRAY_SIZE)];
			while (sg == null)
				sg = chanceArray[rg.nextInt(ARRAY_SIZE)];
			Song next = sg.getSongByIndex(rg.nextInt(sg.size()));
			if (!validNext(next))
				determineNext();
			else {
				Song oldNext = nextSong;
				this.nextSong = next;
				putRecent(next);
				propertyListeners.firePropertyChange("next", oldNext, nextSong);
			}
		} else {
			nextSong = null;
		}
		debug("nextSong = " + nextSong);
	}

	/**
	 * Set the relevance of a songgroup
	 * 
	 * @param sg
	 * @param f
	 *            with f >= 0 && f < 100
	 */
	public void setRelevance(SongGroup sg, float f) {
		debug("setRelevance : " + f + ", " + sg.getName());
		if (f != 0f && f < 100) {
			relevance.put(sg, new Float(f));
			relevancesChanged();
		} else {
			debug("relevance value not in the interval ]0, 100[  removing the songgroup");
			if (relevance.containsKey(sg)) {
				relevance.remove(sg);
				relevancesChanged();
			}
		}
		SAM.playlists.playlistChanged(this);
		propertyListeners.firePropertyChange("playlist_changed", null, this);
	}

	/**
	 * Get the relevance of a given songgroup
	 * 
	 * @param sg
	 * @return 0f if no relevance is set
	 */
	public float getRelevance(SongGroup sg) {
		if (relevance.containsKey(sg))
			return relevance.get(sg).floatValue();
		return 0f;
	}

	/**
	 * @see com.plexus.sam.audio.Playlist#hasNext()
	 */
	public boolean hasNext() {
		return relevanceSum > 0;
	}

	/**
	 * @see com.plexus.sam.audio.Playlist#nextSong()
	 */
	public Song nextSong() {
		return nextSong;
	}

	/**
	 * @see com.plexus.sam.audio.Playlist#hasPrevious()
	 */
	public boolean hasPrevious() {
		return false;
	}

	/**
	 * @see com.plexus.sam.audio.Playlist#previousSong()
	 */
	public Song previousSong() {
		return null;
	}

	/**
	 * @see com.plexus.sam.audio.Playlist#getName()
	 */
	public String getName() {
		return name;
	}

	/**
	 * @see com.plexus.sam.audio.Playlist#setName(java.lang.String)
	 */
	public void setName(String s) {
		debug("setName " + s);
		this.name = s;
	}

	/**
	 * Output a debugging message
	 * 
	 * @param msg
	 */
	private void debug(String msg) {
		if (DEBUG)
			System.out.println("dynamicplaylist " + msg);
	}

	/**
	 * @return the relevance factors of this playlist
	 */
	public Relevance[] getValues() {
		Relevance[] r = new Relevance[relevance.size()];
		int counter = 0;
		for (Iterator i = relevance.keySet().iterator(); i.hasNext();) {
			SongGroup sg = (SongGroup) i.next();
			r[counter++] = new Relevance(sg.getName(),  relevance.get(sg).floatValue());
		}
		return r;
	}

	/**
	 * @param r
	 *            the values to add
	 */
	public void setValues(Relevance[] r) {
		for (int i = 0; i < r.length; i++) {
			SongGroup sg = SAM.repos.getSongGroup(r[i].getGroupname());
			if (sg != null)
				this.setRelevance(sg, r[i].getValue());
		}
	}

	/**
	 * Implementation of Object
	 * 
	 * @return the name of the playlist, or  
	 */
	@Override
	public String toString() {
		return name == null ? i18n.getString("unnamed_playlist") : name;
	}

	/**
	 * Inner class used for storing and retreiving the playlist with
	 * XMLen/decoder
	 *  
	 */
	public static class Relevance {
		private String groupname;

		private float value;

		/**
		 * 
		 *
		 */
		public Relevance() {
			//
		}
		/**
		 * Set the name of the group and the associated relevance factor
		 * 
		 * @param name groupname
		 * @param value relevance factor
		 */
		public Relevance(String name, float value) {
			this.groupname = name;
			this.value = value;
		}

		/**
		 * @return Returns the groupname.
		 */
		public String getGroupname() {
			return groupname;
		}

		/**
		 * @param groupname
		 *            The groupname to set.
		 */
		public void setGroupname(String groupname) {
			this.groupname = groupname;
		}

		/**
		 * @return Returns the value.
		 */
		public float getValue() {
			return value;
		}

		/**
		 * @param value
		 *            The value to set.
		 */
		public void setValue(float value) {
			this.value = value;
		}
	}

	/**
	 * The Class of the Panel to be shown in the Player window
	 * 
	 * @return DynamicPlaylistPanel.class
	 * @see com.plexus.sam.audio.Playlist#getPanelClass()
	 */
	public Class getPanelClass() {

		return DynamicPlaylistPanel.class;
	}

	/**
	 * The Class of the panel to be shown in the Playlist window
	 * 
	 * @return DynamicPlaylistEditPanel.class
	 * @see com.plexus.sam.audio.Playlist#getEditPanelClass()
	 */
	public Class getEditPanelClass() {
		return DynamicPlaylistEditPanel.class;
	}

	/**
	 * Get the gui model associated with this playlist, so every controller/view
	 * uses the same model. A new model is created when this method is first called.
	 * 
	 * @return the associated model
	 */
	public DynamicPlaylistModel getModel() {
		if (model == null)
			model = new DynamicPlaylistModel(this);
		return model;
	}

	/**
	 * @param listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyListeners.addPropertyChangeListener(listener);
	}

	/**
	 * @param propertyName
	 * @param listener
	 */
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyListeners.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * @param listener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyListeners.removePropertyChangeListener(listener);
	}

	/**
	 * @param propertyName
	 * @param listener
	 */
	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyListeners.removePropertyChangeListener(propertyName, listener);
	}

	/**
	 * Go to the previous song, we don't support this operation.
	 */
	public void gotoPrevious() {
		//
	}

	/**
	 * Go to the next song
	 */
	public void gotoNext() {
		determineNext();
	}
	
	/**
	 * Notify listeners we're being deleted
	 */
	public void delete() {
		propertyListeners.firePropertyChange("delete", this, null);
	}

	/**
	 * Listen to the repository, because when the repository is empty, we don't have
	 * a next song. When new songs are added, we have to determine a next song.
	 * 
	 * @param evt
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("synchronize") && this.hasNext() == false) {
			for (SongGroup sg : this.relevance.keySet()) {
				if (SAM.repos.getSongGroup(sg.getName()) == null)
					relevance.remove(sg);
			}
			this.relevancesChanged();
		}
	}

	/**
	 * Put a song in the recent song list, overwriting the least recent one. 
	 *
	 * @param s song to put
	 */
	private void putRecent(Song s) {
		if (recentSongCounter == recentSongs.length)
			recentSongCounter = 0;
		recentSongs[recentSongCounter++] = s;
	}
	
	/**
	 * Determine wether a song is a valid song to be returned next, this means no song
	 * in the recent song list is from the same artist.
	 * 
	 * @param s song to check
	 * @return true when the song is ok
	 */
	private boolean validNext(Song s) {
		
		if (alwaysValid)
			return true;
		
		for (Song r : recentSongs) {
			if (r != null && r.getAuthor().toLowerCase().equals(s.getAuthor().toLowerCase()))
				return false;
		}
		return true;
	}

	/**
	 * When a next song is determined, no songs are returned from an artist that has played recently (e.g. last three songs),
	 * but, when only a small number of different artists are available in our songgroups, this will result in endless recursion.
	 * Thus this method that sets the field 'alwaysValid' to true, when the validity of a song shouldn't be checked.
	 */
	private void determineAlwaysValid() {
		Set<String> artists = new HashSet<String>();
		for (SongGroup sg : relevance.keySet()) {
			for (Song song : sg.getSongList()) {
				if (!artists.contains(song.getAuthor().toLowerCase()))
					artists.add(song.getAuthor().toLowerCase());
				if (artists.size() > recentSongs.length)
					alwaysValid = false;
			}
		}
		alwaysValid = true;
	}
}