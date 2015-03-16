/*
 * Created on 27-aug-2005
 */
package com.plexus.sam.config;

import java.util.Map;
import java.util.HashMap;

import javax.swing.ImageIcon;

import com.plexus.sam.audio.DynamicPlaylist;
import com.plexus.sam.audio.RulesPlaylist;
import com.plexus.sam.audio.SimplePlaylist;


/**
 * This class contains a number a static (hardcoded values). Mainly the
 * path to the main configuration XML file.
 * 
 * @author plexus
 */
public class StaticConfig {
	private static Map<String, Object> values;
	
	static {
		values = new HashMap<String, Object>();
		values.put("config-path", "config/configuration.xml");
		values.put("i18n_package", "com.plexus.sam.i18n");
		values.put("config_ui", "data/config_gui.xml");
		values.put("autoplayer_path", "data/autoplayrules.xml");
		values.put("error_log", "data/error_log.txt");
		values.put("trigger-path", "data/triggers.xml");
		
		Map<String, ImageIcon> icons = new HashMap<String, ImageIcon>();
		icons.put("songgroup", new ImageIcon("icons/Gramophone.gif"));
		icons.put("song", new ImageIcon("icons/Hamer.gif"));
		//icons.put("play", new ImageIcon("icons/play.gif"));
		//icons.put("stop", new ImageIcon("icons/stop.gif"));
		icons.put("play_small", new ImageIcon("icons/play_small.gif"));
		icons.put("stoptrigger", new ImageIcon("icons/stoptrigger.gif"));
		values.put("icons", icons);
	}
	
	
	/**
	 * Get the designated hard-coded value.
	 * 
	 * @param key Key that identifies the value
	 * @return the value
	 */
	public static String get(String key) {
		if (values.containsKey(key))
			return (String)values.get(key);
		else
			return null;
	}
	
	/**
	 * The different types of playlists, when implementing
	 * a new type it can be added here and the whole UI will
	 * be updated
	 * 
	 * @return array with every class that implements Playlist
	 */
	public static Class[] playlistClasses() {
			Class[] pl = {SimplePlaylist.class, DynamicPlaylist.class, RulesPlaylist.class};
			return pl;
	}

	/**
	 * @param string
	 * @return
	 */
	public static Object getObject(String key) {
		if (values.containsKey(key))
			return values.get(key);
		else
			return null;
	}
}