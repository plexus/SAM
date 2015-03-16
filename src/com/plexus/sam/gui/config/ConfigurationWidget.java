/*
 * Created on 23-sep-2005
 *
 */
package com.plexus.sam.gui.config;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.plexus.sam.SAM;
import com.plexus.sam.config.ConfigGroup;
import com.plexus.sam.config.Configuration;

/**
 * Common base class for all types of Configuration Widgets (text, choice,
 * integer,...). One widget controls one configuration item.<br />
 * All subclasses must be fully compliant javabeans see
 * {@link java.beans.XMLEncoder}. <br /><br />
 * To write a concrete ConfigurationWidget
 * <ul>
 * <li>Add the {@link #descriptionLabel} to the panel (with wathever Layout you choose)</li>
 * <li>Add widgets that control the value (a {@link javax.swing.JTextField}, a {@link javax.swing.JComboBox}, ...)</li>
 * <li>Implement the necessary Interfaces to listen to your widgets for changes</li>
 * <li>Call {@link #setValue(String)} when the value changes</li>
 * <li>Override {@link #setValue(String)}, start with <code>super.setValue(value);</code>
 * and add code to update your widgets when it is called from outside our scope</li>
 * <li>Add javabean accessors/mutators to enable the widget to become fully persistent (if necessary)</li>
 * </ul>
 * {@link #setValue(String)} will immediatly update the configuration.
 * 
 * @see ConfigurationPanel
 * @see ConfigGroupPanel
 * @see com.plexus.sam.config.Configuration
 * 
 * @author plexus
 */
public abstract class ConfigurationWidget extends JPanel {
	/**
	 * Name of the {@link ConfigGroup}
	 */
	protected String group;

	/**
	 * Key used to look up a value in the {@link ConfigGroup}
	 */
	protected String key;

	/**
	 * Current value corresponding to {@link #group} and {@link #key}
	 */
	protected String value;

	/**
	 * Key used to look up the description of this configuration item in the
	 * i18n ResourceBundle
	 */
	private String descriptionKey;

	/**
	 * Current description as shown in the gui
	 */
	private String description;

	/**
	 * The i18n ResourceBundle currently in use
	 */
	protected ResourceBundle i18n;

	/**
	 * The label that displays the {@link #description} in the gui.
	 */
	protected JLabel descriptionLabel;

	/**
	 * The ResourceBundle used for i18n when none is specified at construction
	 */
	protected static ResourceBundle defaultI18n;

	/**
	 * The default i18n resourcebundle is initialized to
	 * com.plexus.sam.i18n.plexus, if that file can be found (for the current
	 * locale).
	 */
	static {
		try {
			ConfigurationWidget.defaultI18n = SAM
					.getBundle("config");
		} catch (MissingResourceException e) {
			ConfigurationWidget.defaultI18n = null;
		}
	}

	/**
	 * Initialize the 'group' and 'key' properties referring to a value in a
	 * {@link com.plexus.sam.config.ConfigGroup}. This invokes the abstract
	 * method {@link #setValue(String)} which should initialize the widget to
	 * display the currently set value.<br />
	 * The description key is used to look up the description of the
	 * configuration property in the given {@link java.util.ResourceBundle}.
	 * 
	 * 
	 * @param group
	 *            name of the configuration group
	 * @param key
	 *            key of the value to read and write from and to the
	 *            configuration
	 * @param descriptionKey
	 *            key to look up the description in of the managed value in a
	 *            ResourceBundle
	 * @param i18n
	 *            the resourcebundle to look up the description
	 */
	public ConfigurationWidget(String group, String key, String descriptionKey,
			ResourceBundle i18n) {
		this.group = group;
		this.key = key;
		this.i18n = i18n;
		this.descriptionKey = descriptionKey;
		this.descriptionLabel = new JLabel();
		loadDescription();

		loadConfigValue();
	}

	/**
	 * Load the description from the i18n ResourceBundle
	 */
	private void loadDescription() {
		if (descriptionKey != null && !descriptionKey.equals("")) {
			this.description = this.i18n.getString(this.descriptionKey);
			this.descriptionLabel.setText(description);
		}
	}

	/**
	 * Initialize the 'group' and 'key' properties referring to a value in a
	 * {@link com.plexus.sam.config.ConfigGroup}. This invokes the
	 * method {@link #setValue(String)} which should initialize the widget to
	 * display the currently set value.<br />
	 * The description key is used to look up the description of the
	 * configuration property in the default {@link java.util.ResourceBundle}
	 * (Normally com.plexus.sam.i18n.gui, see also
	 * {@link ConfigurationWidget#setDefaultI18n(ResourceBundle)}.<br />
	 * 
	 * @param group
	 *            name of the configuration group
	 * @param key
	 *            key of the value to read and write from and to the
	 *            configuration
	 * @param descriptionKey
	 *            key to look up the description in of the managed value in a
	 *            ResourceBundle
	 */
	public ConfigurationWidget(String group, String key, String descriptionKey) {
		this(group, key, descriptionKey, ConfigurationWidget.defaultI18n);
	}

	/**
	 * Set the default resourcebundle for looking up i18n strings. This property
	 * is read when no ResourceBundle is given at construction time.<br />
	 * Initially the ResourceBundle com.plexus.sam.i18n.gui is used.
	 * 
	 * @param i18n
	 *            The default ResourceBundle to look up the description
	 */
	public static void setDefaultI18n(ResourceBundle i18n) {
		ConfigurationWidget.defaultI18n = i18n;
	}

	/**
	 * Update the configuration and set the value currently visible in the widget
	 * 
	 * @param value configuration value
	 */
	public void setValue(String value)  {
		if (!Configuration.getConfigGroup( group ).get( key ).equals( value )) {
			Configuration.getConfigGroup( group ).set( key, value );
			Configuration.save();
		}
		this.value = value;
	}


	/**
	 * @return the name of the {@link com.plexus.sam.config.ConfigGroup}.
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * @param group
	 *            The group to set.
	 */
	public void setGroup(String group) {
		this.group = group;
		loadConfigValue();
	}

	/**
	 *
	 */
	private void loadConfigValue() {
		if (group != null && Configuration.getConfigGroup(this.group) != null && key != null && !key.equals(""))
			this.setValue(Configuration.getConfigGroup(this.group).get(this.key));
	}

	/**
	 * @return Returns the configuration key.
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            The key that identifies a configuration value in the
	 *            {@link com.plexus.sam.config.ConfigGroup}.
	 */
	public void setKey(String key) {
		this.key = key;
		loadConfigValue();
	}

	/**
	 * @return Returns the descriptionKey used to look up the description in the
	 *         i18n resourcebundle.
	 */
	public String getDescriptionKey() {
		return descriptionKey;
	}

	/**
	 * @param descriptionKey
	 *            The descriptionKey used to look up the description in the i18n
	 *            resourcebundle.
	 */
	public void setDescriptionKey(String descriptionKey) {
		if (this.descriptionKey == null || !this.descriptionKey.equals(descriptionKey)) {
			this.descriptionKey = descriptionKey;
			loadDescription();
		}
	}

	/**
	 * @param i18n
	 *            The i18n resourcebundle to use.
	 */
	public void setI18n(ResourceBundle i18n) {
		if (!this.i18n.equals(i18n)) {
			this.i18n = i18n;
			loadDescription();
		}
	}

	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}
}
