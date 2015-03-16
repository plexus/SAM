/*
 * Created on 14-sep-2005
 *
 */
package com.plexus.sam.gui.actions;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;

import com.plexus.sam.SAM;
import com.plexus.sam.config.ConfigGroup;
import com.plexus.sam.config.Configuration;

/**
 * Toggle the autosync switch in the configuration. Automatically the name of the action is adjusted.
 * 
 * @author plexus
 *
 */
public class ToggleAutoSyncAction extends AbstractAction implements PropertyChangeListener {
	private ConfigGroup config = Configuration.getConfigGroup("sync");
	private ResourceBundle i18n = SAM.getBundle("gui");
	
	/**
	 * Default constructor, registers itself as listener of the sync configuration
	 */
	public ToggleAutoSyncAction() {
		setName();
		config.addChangeListener(this);
	}
	
	/**
	 * Action : toggle the autosync property (on or off, true or false)
	 */
	public void actionPerformed(ActionEvent e) {
		if (config.get("autosync").equals("true")) 
			config.set("autosync", "false");
		else
			config.set("autosync", "true");
		Configuration.save();
	}

	/**
	 * Adjust the caption of this action when the autosync property changes
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("autosync"))
			setName();
		
	}

	/**
	 * Read the autosync property and set the caption of this action accordingly
	 *
	 */
	private void setName() {
		if (config.get("autosync").equals("true")) {
			this.putValue(NAME, i18n.getString("disable_autosync"));
		} else {
			this.putValue(NAME, i18n.getString("enable_autosync"));
		}
	}
}
