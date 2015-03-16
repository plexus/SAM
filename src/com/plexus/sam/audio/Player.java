/*
 * Created on 2-sep-2005
 *
 */
package com.plexus.sam.audio;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Timer;

import javax.sound.sampled.FloatControl;


import com.plexus.sam.config.ConfigGroup;
import com.plexus.sam.config.Configuration;


/**
 * Intelligent music player component. It plays Song objects either directly (playNow())
 * or from a playlist (setPlaylist()). Its controls are play/stop , pause/resume and next.
 * 
 * @author plexus
 * 
 */
public class Player implements PropertyChangeListener {
	private boolean DEBUG = false;
	private Playlist playlist;
	private Song current;
	private Song previous;
	
	private SongStream previousStream;
	private SongStream currentStream;

	private Thread lastStarted;
	private boolean playing = false;
	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);

	private double volume;
	
	/**
	 * Defualt constructor.
	 *
	 */
	public Player() {
		debug ("new player");
		ConfigGroup guiconfig = Configuration.getConfigGroup("gui");
		try {
			setVolume(Float.parseFloat(guiconfig.get("volume")));
		} catch (Exception e) {
			setVolume(700f);
		}
	}
	
	/**
	 * Immediatly start playing a song
	 * 
	 * @param song
	 */
	public void playNow(Song song){
		debug("playnow "+song);
		if (playing) {//TODO: Fade out
			currentStream.stop();
			if (previousStream != null && previousStream.isRunning())
				previousStream.stop();	
		}
		previous = current;
		current = song;
		playing = true;
		
		previousStream = currentStream;
		currentStream = new SongStream(this);
		currentStream.setMedia(song);
		currentStream.initLine();
		lastStarted = currentStream.start();
		listeners.firePropertyChange("current", previous, current);
	}

	
	//************************ private methods ***************************
	/**
	 * Get the next song from the playlist
	 *
	 */
	private synchronized void nextFromPlaylist() {
		debug("nextFromPlaylist");
		if (playlist != null && playlist.hasNext()) {
			previous = current;
			if (previousStream != null && previousStream.isRunning())
				previousStream.stop();
			previousStream = currentStream;
			currentStream = null;
			current = playlist.nextSong();
			playlist.gotoNext();
			if (previous != null && previous.equals(current)) 
				listeners.firePropertyChange("next_equals_current", null, null);
			else
				listeners.firePropertyChange("current", previous, current);
			debug("oldsong "+previous);
			debug("newsong "+current);
		} else if (playlist != null) {
			Song oldCurrent = current;
			current = null;
			currentStream = null;
			listeners.firePropertyChange("current", oldCurrent, current);
		}
		
	}
	
	/**
	 * Get the previous song from the playlist
	 *
	 */
	private synchronized void previousFromPlaylist() {
		debug("previousFromPlaylist");
		
		if (playlist != null && playlist.hasPrevious()) {
			previous = current;
			current = playlist.previousSong();
			if (previousStream != null && previousStream.isRunning())
				previousStream.stop();
			previousStream = currentStream;
			currentStream = null;
			playlist.gotoPrevious();
			if (previous != null && previous.equals(current)) 
				listeners.firePropertyChange("previous_equals_current", null, null);
			else
				listeners.firePropertyChange("current", previous, current);
			debug("oldsong "+previous);
			debug("newsong "+current);
		}
		
	}
	
	//************************* controls **********************************
	/**
	 * Play the song that is currently set as current song.
	 *
	 */
	public void play() {
		debug("play");
		if (currentStream != null && currentStream.isPaused()) {
			synchronized (this) {
				playing = true;
				lastStarted = currentStream.start();
				if (previousStream != null && previousStream.isPaused())
					previousStream.start();
				
				listeners.firePropertyChange("playing", false, true);
			}
		} else if (!playing) {
			debug("starting "+current);
			if ( current != null && (currentStream == null || !currentStream.isRunning())) {
				synchronized (this) {
					currentStream = new SongStream(this);
					currentStream.setMedia( current );
					currentStream.initLine();
					if (currentStream.isOutputInitialized()) {
						debug ("ouput initialized") ;
						playing=true;
						lastStarted = currentStream.start();
						listeners.firePropertyChange("playing", false, true);
					} else {
						debug("failed to initialize output");
					}
				}
			} else if (playlist != null && playlist.hasNext()) {
				synchronized (this) {
					nextFromPlaylist();
				}
				play();
			}
		}
	}
	
	/**
	 * Pause the playing
	 *
	 */
	public void pause() {
		debug("pause   playing?"+playing+" currentStream=="+currentStream);
		if (playing && currentStream != null) {
			synchronized (this) {
				currentStream.pause();
				playing=false;
				if (previousStream != null)
					previousStream.pause();
			}
			listeners.firePropertyChange("playing", true, false);
			
		}
		else
			debug("no pause");
		
	}

	/**
	 * Stop playback and return to the beginning of the song
	 *
	 */
	public synchronized void stop() {
		debug("stop");
		if (playing) {
			playing = false;
			if (previousStream != null)
				previousStream.stop();
			if (currentStream != null) {
				currentStream.stop();
				current.reinitialiseStream();
			}
			listeners.firePropertyChange("playing", true, false);
		} else {
			debug ("currentStream == null");
		}
		
	}

	/**
	 * Resume playback, or start playing if no song is paused.
	 *
	 */
	public void resume() {
		debug("resume");
		if (!playing && currentStream != null && !currentStream.isRunning()) {
			synchronized (this) {
				playing=true;
				lastStarted = currentStream.start();
				listeners.firePropertyChange("playing", false, true);
			}
		}
		else if (!playing && current != null )
			play();
	}

	/**
	 * Go to the next song on the playlist
	 *
	 */
	public synchronized void next() {
		debug("next");
		debug("current "+current);
		debug("previous "+(this.previousStream == null ? null : previousStream.getSong()));
		boolean isPlaying = playing;
		if (isPlaying)
			stop();
		
		
			if (playlist != null && playlist.hasNext() ) {
				nextFromPlaylist();
			} else {
				debug("playlist is null or doesnt have a next song");
				isPlaying = false;
			}
			if (isPlaying)
				play();
		
		
		debug(" >current "+current);
		debug(" >previous "+(this.previousStream == null ? null : previousStream.getSong()));
		
	
	}

	/**
	 * Play the previous song from the playlist.
	 *
	 */
	public synchronized void previous() {
		debug("previous");
		boolean isPlaying = playing;
		if (isPlaying)
			stop();
		
			if (playlist != null && playlist.hasPrevious() ) {
				previousFromPlaylist();
			} else {
				isPlaying = false;
			}
		
		if (isPlaying)
			play();
	}

	//*************************** properties ************************
	/**
	 * Does the playlist have a next song?
	 * @return true when there is a next song to play
	 */
	public boolean hasNext() {
		return playlist != null && playlist.hasNext();
	}
	
	/**
	 * Does the playlist have a previous song?
	 * @return true when there is a previous song
	 */
	public boolean hasPrevious() {
		return playlist != null && playlist.hasPrevious();
	}

	
	/**
	 * Set the playlist, this adds the new playlist to the list of
	 * propertychangelisteners, and removes (if any) the old playlist
	 * from the list of propertychangelisteners. 
	 * 
	 * @param pl
	 */
	public synchronized void setPlaylist ( Playlist pl ) {
		debug("setPlayList "+pl);
		Playlist oldPlaylist = playlist;
		this.playlist = pl;
		pl.addPropertyChangeListener(this);
		listeners.addPropertyChangeListener(pl);
		listeners.firePropertyChange("playlist", oldPlaylist, playlist);
		listeners.removePropertyChangeListener(oldPlaylist);
		if (oldPlaylist != null)
			oldPlaylist.removePropertyChangeListener(this);
		
		if (!playing) {
			nextFromPlaylist();
		}
	}

	/**
	 * Get the current playlist
	 * 
	 * @return null if no playlist is set
	 */
	public Playlist getPlaylist() {
		return playlist;
	}
	
	/**
	 * Get the length of the current song
	 * 
	 * @return length in ms
	 */
	public long getLength() {
		if (currentStream != null)
			return currentStream.getLength();
		return 0;
	}
	
	/**
	 * Get the position of the current song in ms
	 * @return position in ms, <= getLength()
	 */
	public long getPosition() {
		if (currentStream != null)
			return currentStream.getPosition();
		return 0;
	}
	
	/**
	 * Is the player currently playing?
	 * @return true whent the player is running
	 */
	public boolean isPlaying() {
		return playing;
	}
	
	/**
	 * Get the song that is currently playing (or will be up next when stopped).
	 * @return the current song
	 */
	public Song getCurrent() {
		return current;
	}
	
	/**
	 * Get the next song that will play
	 * @return the next song
	 */
	public Song getNext() {
		return playlist.nextSong();
	}
	
	/**
	 * Add a propertychangelistener to be notified of changes in the properties:
	 * current, playing or playlist
	 * @param cl
	 */
	public void addListener(PropertyChangeListener cl) {
		listeners.addPropertyChangeListener(cl);
	}
	
	/**
	 * Callback from songstream to notify us that the current song has stopped playing
	 * @param song
	 */
	public synchronized void done(Thread t) {
		debug("done "+t);
		if (t.equals(lastStarted)) {
			if (previousStream != null && previousStream.isRunning())
				previousStream.stop();
			
			if (playlist != null && playlist.hasNext() && playing) {
				nextFromPlaylist();
				playing = false;
				play();
			}
		}
	}
	
	/**
	 * Output a debugging message
	 * 
	 * @param s
	 */
	public void debug(String s) {
		assert currentStream == null || current.equals(currentStream.getSong()) : "current != currentStream";
		assert previousStream == null || previous.equals(previousStream.getSong()) : "previous != previousStream";
		if (DEBUG)
			System.out.println("player "+s);
	}

	/**
	 * When a playlist is deleted, we have to stop playing it
	 * @param evt event transmitted by the playlist
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource().equals(playlist) && evt.getPropertyName().equals("delete")) {
			Playlist oldPlaylist = playlist;
			playlist = null;
			stop();
			listeners.firePropertyChange("playlist", oldPlaylist, playlist);
			
			Song oldCurrent = current;
			current = null;
			listeners.firePropertyChange("current", oldCurrent, current);
		} else if (evt.getSource().equals(playlist) && evt.getPropertyName().equals("playlist_changed")) {
			listeners.firePropertyChange("playlist_changed", null, playlist);
		}
		
	}
	
	/**
	 * Get the current volume as a value between 0 and 1000
	 * 
	 * @return the volume
	 */
	public double getVolume() {
		return volume;
	}
	
	/**
	 * set the current volume 
	 * 
	 * @param volume the volume
	 */
	public void setVolume(double volume) {
		double oldVolume = this.volume;
		this.volume = (volume < 0) ? 0 : (volume > 1000 ? 1 : volume) ;
		if (previousStream != null)
			previousStream.setVolume();
		if (currentStream != null)
			currentStream.setVolume();
		listeners.firePropertyChange("volume", oldVolume, this.volume);
	}
	
	public void fadeTo(double targetVolume, long delayms) {
		if (delayms == 0) {
			setVolume(targetVolume);
			return;
		}
		debug("fading from "+volume+" to "+targetVolume+" in "+delayms+" ms");
		long time = System.currentTimeMillis();
		double startVolume = this.volume;
		for (long delay = System.currentTimeMillis() - time; 
				targetVolume != this.volume;
				delay = System.currentTimeMillis() - time) {
			synchronized(this) {
				if (delay > delayms)
					delay=delayms;
				volume = startVolume + (targetVolume - startVolume) * ((float)delay / (float)delayms);
				if (currentStream != null && 
						(currentStream.getOutputLine().isControlSupported(FloatControl.Type.MASTER_GAIN)
						||currentStream.getOutputLine().isControlSupported(FloatControl.Type.VOLUME)))
					currentStream.setVolume();
				if (previousStream != null&& 
						(previousStream.getOutputLine().isControlSupported(FloatControl.Type.MASTER_GAIN)
						||previousStream.getOutputLine().isControlSupported(FloatControl.Type.VOLUME)))
					previousStream.setVolume();
			}
				
		}
		listeners.firePropertyChange("volume", startVolume, this.volume);
	}
	
}

