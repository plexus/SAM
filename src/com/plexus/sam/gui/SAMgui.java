/*
 * Created on 28-aug-2005
 *
 */
package com.plexus.sam.gui;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import com.plexus.sam.SAM;
import com.plexus.sam.audio.Player;
import com.plexus.sam.audio.SimplePlaylist;
import com.plexus.sam.comm.TriggerModel;
import com.plexus.sam.config.ConfigGroup;
import com.plexus.sam.config.Configuration;
import com.plexus.sam.config.StaticConfig;
import com.plexus.sam.event.Autoplayer;
import com.plexus.sam.gui.config.ConfigurationPanel;

/**
 * The main frame of the gui. Initialises the menu and the 
 * different tabs.
 * 
 * @author plexus
 *
 */
public class SAMgui extends JFrame {
	private ResourceBundle i18n=ResourceBundle.getBundle("com.plexus.sam.i18n.gui");
	private static SAMgui self = null;
	private PlayerPanel playerPanel;
	private PlaylistPanel playlistPanel;
	private TriggerPanel triggerPanel;
	
	/**
	 * The GUI is a singleton
	 * @return the gui
	 */
	public static SAMgui singleInstance() {
		if (self == null) {
			self = new SAMgui();
		}
		return self;
	}
	
	/**
	 * Private constructor for the singleton pattern
	 *
	 */
	private SAMgui() {
		super();
		this.setTitle(i18n.getString("title")+Configuration.getConfigGroup("general").get("version"));
		JTabbedPane tabbedPane = new JTabbedPane();
		this.add( tabbedPane );
		
		playerPanel = new PlayerPanel();
		tabbedPane.addTab(i18n.getString("tab_player"), null/*icon*/, playerPanel, i18n.getString("tab_player_full"));
		
		playlistPanel = new PlaylistPanel();
		tabbedPane.addTab(i18n.getString("tab_playlist"), null, playlistPanel, i18n.getString("tab_playlist_full"));
		
		RepositoryPanel reposPanel = new RepositoryPanel();
		tabbedPane.addTab(i18n.getString("tab_repos"), null, reposPanel, i18n.getString("tab_repos_full"));
		
		AutoplayerPanel autoplayerPanel = new AutoplayerPanel(new Autoplayer());
		tabbedPane.addTab(i18n.getString("tab_autoplayer"), null, autoplayerPanel, i18n.getString("tab_autoplayer_full"));
		
	    triggerPanel = new TriggerPanel(new TriggerModel(SAM.player));
		tabbedPane.addTab(i18n.getString("tab_trigger"), null, triggerPanel, i18n.getString("tab_trigger_full"));
			
		
		
		try {
			XMLDecoder d = new XMLDecoder(new BufferedInputStream(new FileInputStream(StaticConfig.get("config_ui"))));
			ConfigurationPanel configPanel = (ConfigurationPanel)d.readObject();
			tabbedPane.addTab(i18n.getString("tab_config"), null, configPanel, i18n.getString("tab_config_full"));
		} catch (FileNotFoundException e) {
			SAM.error("config_gui_filenotfound", e);
		}
		
	}
	
	/**
	 * This method is so the gui settings can be saved upon closing
	 * @return the position of the splitter of the playerpanel JSplitpane
	 */
	public int getPlayerSplitterPosition() {
		return playerPanel.getDividerLocation();
	}
	
	/**
	 * This method is so the gui settings can be saved upon closing
	 * @return the position of the splitter of the playlistpanel JSplitpane
	 */
	public int getPlaylistSplitterPosition() {
		return playlistPanel.getDividerLocation();
	}
	
	public int getTriggerSplitterPosition() {
		return triggerPanel.getSplitterPosition();
	}
}
