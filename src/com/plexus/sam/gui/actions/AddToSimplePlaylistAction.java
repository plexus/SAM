/*
 * Created on 12-sep-2005
 *
 */
package com.plexus.sam.gui.actions;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;

import com.plexus.debug.NoisyPropertyChangeListener;
import com.plexus.sam.SAM;
import com.plexus.sam.audio.SimplePlaylist;
import com.plexus.sam.gui.SongBrowser;

/**
 * Add the selected song from a SongBrowser to a SimplePlaylist.
 * 
 * @author plexus
 */
public class AddToSimplePlaylistAction extends AbstractAction implements PropertyChangeListener {
	private SongBrowser browser;
	private SimplePlaylist playlist;
	
	/**
	 * Set the browser and playlist to read from/write to
	 * @param browser
	 * @param playlist
	 */
	public AddToSimplePlaylistAction(SongBrowser browser, SimplePlaylist playlist) {
		this.browser=browser;
		this.playlist=playlist;
		browser.addPropertyChangeListener(this);
		this.putValue(NAME, SAM.getBundle("gui").getObject("add_song"));
		this.setEnabled(browser.getSelected() != null);
	}

	/**
	 * Action : add a song
	 */
	public void actionPerformed(ActionEvent e) {
		playlist.addSong(browser.getSelected());
	}

	/**
	 * En/disable when a/no song is selected in the browser
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		this.setEnabled(browser.getSelected() != null);
	}

}
