/*
 * Created on 24-sep-2005
 *
 */
package com.plexus.sam.gui.config;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JComboBox;

import com.plexus.sam.config.Configuration;

/**
 * Widget expressing a configuration field that can take a number of discreet values, shown in a combobox.
 * A value can be associated with a key, which is looked up in the i18n files to display a description
 * in the combobox.
 * 
 * @author plexus
 *
 */
public class ChoiceWidget extends ConfigurationWidget implements ActionListener {
	protected JComboBox choiceBox;
	protected Map<String, String> keyValueMap;
	
	/**
	 * 
	 * @param group
	 * @param key
	 * @param descriptionKey
	 * @param i18n
	 * @see ConfigurationWidget#ConfigurationWidget(String, String, String, ResourceBundle)
	 */
	public ChoiceWidget(String group, String key, String descriptionKey,
			ResourceBundle i18n) {
		super(group, key, descriptionKey, i18n);
		setLayout(new GridLayout(0,2));
		this.add(this.descriptionLabel);
		this.choiceBox = new JComboBox();
		this.add(choiceBox);
		choiceBox.addActionListener(this);
	}

	/**
	 * @param group
	 * @param key
	 * @param descriptionKey
	 * @see ConfigurationWidget#ConfigurationWidget(String, String, String)
	 */
	public ChoiceWidget(String group, String key, String descriptionKey) {
		this(group, key, descriptionKey, ConfigurationWidget.defaultI18n);
		
	}

	/**
	 * We need a default constructor for XMLEncoder.
	 *
	 */
	public ChoiceWidget() {
		this("","","");
	}

	/**
	 * The keyValueMap is the map that associates a configuration value with an i18n-key
	 *  
	 * @return Returns the keyValueMap.
	 */
	public Map<String, String> getKeyValueMap() {
		return keyValueMap;
	}

	/**
	 * The keyValueMap is the map that associates a configuration value with an i18n-key
	 * 
	 * @param keyValueMap The keyValueMap to set.
	 */
	public void setKeyValueMap(Map<String, String> keyValueMap) {
		this.keyValueMap = keyValueMap;
		fillComboBox();
	}

	/**
	 * Fill the combobox with everything in the keyValueMap
	 *
	 */
	protected void fillComboBox() {
		if (group != null && keyValueMap != null) {
			String savedValue = Configuration.getConfigGroup(group).get(key);
			choiceBox.removeAllItems();
			for (String k : keyValueMap.keySet()) {
				choiceBox.addItem(new ChoiceValue(k, keyValueMap.get(k), group+"_"+key+"_"+k, i18n.getString(group+"_"+key+"_"+k)));
			}
			this.setValue(savedValue);
		}
	}

	/**
	 * Listen to the combobox for selection
	 * @param e 
	 */
	public void actionPerformed(ActionEvent e) {
		ChoiceValue cv = (ChoiceValue)choiceBox.getSelectedItem();
		setValue(cv.getValue());
	}
	
	/**
	 * @param value value to set 
	 */
	@Override
	public void setValue(String value) {
		super.setValue(value);
		if (keyValueMap != null) {
			if (choiceBox != null)
				for (int i=0; i<choiceBox.getModel().getSize(); i++) {
					ChoiceValue cv = (ChoiceValue) choiceBox.getModel().getElementAt(i);
					if (cv.getValue().equals(value))
						choiceBox.setSelectedIndex(i);
				}
		}
	}
	
	
	/**
	 * Inner class that bundles a configuration key, a configuration value, and a description. these objects
	 * are stored in the combobox.
	 * 
	 * @author plexus
	 *
	 */
	protected static class ChoiceValue {
		private String key, value, descriptionKey, description;

		/**
		 * Set everything at once
		 * 
		 * @param key
		 * @param value
		 * @param descriptionKey
		 * @param description
		 */
		public ChoiceValue(String key, String value, String descriptionKey, String description) {
			this.key = key;
			this.value = value;
			this.description = description;
			this.descriptionKey = descriptionKey;
		}
		
		/**
		 * @return Returns the description.
		 */
		public String getDescription() {
			return description;
		}

		/**
		 * @param description The description to set.
		 */
		public void setDescription(String description) {
			this.description = description;
		}

		/**
		 * @return Returns the key.
		 */
		public String getKey() {
			return key;
		}

		/**
		 * @param key The key to set.
		 */
		public void setKey(String key) {
			this.key = key;
		}

		/**
		 * @return Returns the value.
		 */
		public String getValue() {
			return value;
		}

		/**
		 * @param value The value to set.
		 */
		public void setValue(String value) {
			this.value = value;
		}

		/**
		 * @return Returns the descriptionKey.
		 */
		public String getDescriptionKey() {
			return descriptionKey;
		}

		/**
		 * @param descriptionKey The descriptionKey to set.
		 */
		public void setDescriptionKey(String descriptionKey) {
			this.descriptionKey = descriptionKey;
		}
		
		/**
		 * String representation : description to be visible in the combobox
		 * @return @see #getDescription()
		 */
		@Override
		public String toString() {
			return getDescription();
		}
		
	}
}
