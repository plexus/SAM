/*
 * Created on 23-sep-2005
 *
 */
package com.plexus.sam.gui.config;

import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.plexus.sam.SAM;
import com.plexus.sam.config.ConfigGroup;

/**
 * Panel containing widgets to alter the configuration of a single {@link com.plexus.sam.config.ConfigGroup}.
 * 
 * @see com.plexus.sam.config.Configuration
 * @see com.plexus.sam.config.ConfigGroup
 * @see com.plexus.sam.gui.config.ConfigurationPanel
 * @author plexus
 *
 */
public class ConfigGroupPanel extends JPanel {
	/**
	 * Since we extend JPanel we should convey to the Serializable conventions
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The widgets shown
	 */
	private ArrayList<ConfigurationWidget> widgets;
	
	/**
	 * The panel where widgets are added to
	 */
	private JPanel contentPane;
	
	/**
	 * The name of the {@link ConfigGroup} we're editing
	 */
	private String groupname;
	
	/**
	 * The caption as shown in the gui, this is the configgroupname, but looked up in a ResourceBundle
	 */
	private String caption;
	
	/**
	 * The 'config' ResourceBundle that contains translations for group and value keys.
	 */
	private ResourceBundle i18n = SAM.getBundle("config");
	
	/**
	 * Set the list of widgets, and the groupname
	 * 
	 * @param widgets
	 * @param groupname key for {@link com.plexus.sam.config.Configuration#getConfigGroup(String)}
	 */
	public ConfigGroupPanel(ArrayList<ConfigurationWidget> widgets, String groupname) {
		this.widgets = widgets;
		this.contentPane = new JPanel();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		JScrollPane scroll = new JScrollPane();
		scroll.setViewportView(contentPane);
		this.add(scroll);
		
		this.groupname = groupname;
		resolveCaption();
		update();
	}

	/**
	 * Look up the caption in the resourceBundle
	 */
	private void resolveCaption() {
		if (groupname == null)
			caption = "";
		else
			caption = i18n.getString(groupname);
	}
	
	/**
	 * Initialize with an empty widget-list, and empty groupname
	 * 
	 */
	public ConfigGroupPanel() {
		this (new ArrayList<ConfigurationWidget>(), null);
	}
	
	/**
	 * Lay out the different widgets from top to bottom.
	 */
	private void update() {
		contentPane.removeAll();
		if (widgets != null)
			for (ConfigurationWidget cw : widgets)
				contentPane.add(cw);
		contentPane.validate();
	}
	
	/**
	 * @return the list with widgets
	 */
	public ArrayList getWidgets() {
		return widgets;
	}

	/**
	 * @param widgets The list of widgets to set.
	 */
	public void setWidgets(ArrayList<ConfigurationWidget> widgets) {
		this.widgets = widgets;
		update();
	}
	
	/**
	 * Add a widget to the bottom of the list
	 * 
	 * @param widget component to add
	 */
	/*private void addWidget(ConfigurationWidget widget) {
		if (widgets == null)
			widgets = new ArrayList<ConfigurationWidget>();
		widgets.add( widget );
		update();
	}*/
	
	/**
	 * Add a widget at a specified position
	 * 
	 * @param index position in the list
	 * @param widget component to add
	 */
	/*private void addWidget(int index, ConfigurationWidget widget) {
		if (widgets == null)
			widgets = new ArrayList<ConfigurationWidget>();
		
		widgets.add( index, widget );
		update();
	}*/
	
	/**
	 * Remove a widget from the panel
	 * 
	 * @param widget
	 */
	public void removeWidget(ConfigurationWidget widget) {
		if (widgets != null)
			widgets.remove( widget );
		update();
	}
	
	/**
	 * Remove a widget at a specific index from the panel
	 * 
	 * @param index
	 */
	public void removeWidget(int index) {
		if (widgets != null) {
			widgets.remove( index );
			update();
		}
	}

	/**
	 * @return Returns the groupname.
	 */
	public String getGroupname() {
		return groupname;
	}

	/**
	 * @param groupname The groupname to set.
	 */
	public void setGroupname(String groupname) {
		this.groupname = groupname;
		resolveCaption();
	}

	/**
	 * @return Returns the caption.
	 */
	public String getCaption() {
		return caption;
	}
}
