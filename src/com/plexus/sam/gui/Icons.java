/*
 * Created on 12-sep-2005
 *
 */
package com.plexus.sam.gui;

import java.util.Map;

import javax.swing.Icon;

import com.plexus.sam.config.StaticConfig;

/**
 * Registry for all icons used in the gui. In the current implementation
 * all information comes from the StaticConfig (hardcoded).
 * 
 * @author plexus
 *
 */
public class Icons {
	/**
	 * Map<String,ImageIcon>
	 */
	private static Map iconMap;
	
	
	static {
		iconMap = (Map)StaticConfig.getObject("icons");
	}
	
	/**
	 * @param key name of the icon
	 * @return the icon
	 */
	public static Icon get(String key) {
		return (Icon)iconMap.get(key);
	}

}
