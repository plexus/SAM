/*
 * Created on 2-sep-2005
 *
 */
package com.plexus.sam.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.plexus.sam.SAM;
import com.plexus.sam.audio.Playlist;
import com.plexus.sam.config.ConfigGroup;
import com.plexus.sam.config.Configuration;
import com.plexus.sam.gui.actions.NextAction;
import com.plexus.sam.gui.actions.PauseAction;
import com.plexus.sam.gui.actions.PlayAction;
import com.plexus.sam.gui.actions.PlaylistSelectedAction;
import com.plexus.sam.gui.actions.PreviousAction;
import com.plexus.sam.gui.actions.SetPlayerPlaylistAction;
import com.plexus.sam.gui.actions.StopAction;


/**
 * Panel that is displayed under the 'Player' tab. It has buttons to acces the player,
 * allows for setting of the playlist, and shows the currently playing song.
 * 
 * @author plexus
 *
 */
public class PlayerPanel extends JSplitPane implements PropertyChangeListener, ChangeListener {
	private JButton previousButton = new JButton(new PreviousAction(SAM.player));
	private JButton playButton = new JButton(new PlayAction(SAM.player));
	private JButton pauseButton = new JButton(new PauseAction(SAM.player));
	private JButton stopButton = new JButton(new StopAction(SAM.player));
	private JButton nextButton = new JButton(new NextAction(SAM.player));
	private SongInfoPanel songInfo;
	private PlaylistBox playlistBox;
	private JPanel topPanel = new JPanel();
	private JTextArea noPlaylistMessage;
	private JSlider volume;
	
	/**
	 * Default constructor
	 */
	public PlayerPanel() {
		this.noPlaylistMessage = new JTextArea(SAM.getBundle("gui").getString("no_playlist_message"));
		this.noPlaylistMessage.setEditable(false);
		this.noPlaylistMessage.setLineWrap(true);
		
		this.setOrientation(JSplitPane.VERTICAL_SPLIT);
		
		SAM.player.addListener(this);
		SAM.playlists.addPropertyChangeListener(this);
		
		this.songInfo = new SongInfoPanel("now_playing", true);
		topPanel.setLayout(new BorderLayout());
		
		this.playlistBox = new PlaylistBox(new PlaylistSelectedAction(this, false, false));
		this.playlistBox.addActionListener(new SetPlayerPlaylistAction( playlistBox ));
		
		topPanel.add(playlistBox, BorderLayout.NORTH);
		
		volume = new JSlider(SwingConstants.VERTICAL, 0, 1000, (int)Math.round(SAM.player.getVolume()));
		volume.addChangeListener(this);
		topPanel.add(volume, BorderLayout.EAST);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1,5));
		buttonPanel.add(previousButton);
		buttonPanel.add(playButton);
		buttonPanel.add(pauseButton);
		buttonPanel.add(stopButton);
		buttonPanel.add(nextButton);
		
		topPanel.add(buttonPanel, BorderLayout.CENTER);
		topPanel.add(songInfo, BorderLayout.SOUTH);
		
		this.setTopComponent( topPanel );

		if(SAM.playlists.size() > 0)
			playlistBox.setSelectedIndex(0);
		
		Playlist pl = SAM.player.getPlaylist();
		if (pl == null)
			this.setBottomComponent(noPlaylistMessage);
		
		ConfigGroup configGroup = Configuration.getConfigGroup("gui");
		try {
			setDividerLocation(Integer.parseInt(configGroup.get("playerpanel_dividerlocation")));
		} catch (NumberFormatException e) {
			//Default is good enough for starters
		}
		this.setResizeWeight(.5);
		
		Playlist lastPl = SAM.playlists.getPlaylist(configGroup.get("last_playlist"));
		if (lastPl != null)
			this.playlistBox.setSelectedItem(lastPl);
	}

	/**
	 *	Listen to the player to update the info of the upcoming song
	 *	@param arg0
	 */
	public void propertyChange(PropertyChangeEvent arg0) {
		if (arg0.getSource().equals( SAM.player) ) {
			if (arg0.getPropertyName().equals( "current" ) ) 
				songInfo.update(SAM.player.getCurrent());
			else if (arg0.getPropertyName().equals( "playlist" ) ) {
				Playlist pl = SAM.player.getPlaylist();
				if (pl == null)
					this.setBottomComponent(noPlaylistMessage);
				else
					playlistChanged();
			} else if (arg0.getPropertyName().equals( "volume" ) ) {
				volume.setValue((int)SAM.player.getVolume());
				volume.invalidate();
			}
				
		}
	}

	private void playlistChanged() {
		this.playlistBox.setSelectedItem(SAM.player.getPlaylist());
		
	}

	/**
	 * When the volume slider changes we also change the value of the player
	 * @param e
	 */
	public void stateChanged(ChangeEvent e) {
		SAM.player.setVolume(volume.getValue());
	}
	
}
