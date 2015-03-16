/*
 * Created on 23-sep-2005
 *
 */
package com.plexus.sam.gui.config;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BoxLayout;
import javax.swing.JTextField;

/**
 * Configuration widget to display or alter a string of a single line using a JTextField
 * 
 * @author plexus
 *
 */
public class TextWidget extends ConfigurationWidget implements ActionListener, FocusListener {
	protected JTextField textField;
	
	/**
	 * Set the groupname and key to look up a configuration value. Set the key to look up
	 * the description in the config ResourceBundle
	 * 
	 * @param group
	 * @param key
	 * @param descriptionKey
	 */
	public TextWidget(String group, String key, String descriptionKey) {
		super(group, key, descriptionKey);
		setLayout(new GridLayout(2,0));
		this.add (descriptionLabel);
		this.textField = new JTextField(value);
		this.add(textField);
		textField.addActionListener(this);
		textField.addFocusListener(this);
	}

	/**
	 * Empty constructor, initialize group, key and descriptionkey with an empty string
	 *
	 */
	public TextWidget() {
		this("","",null);
	}
	
	/**
	 * @param value new value to save in the configuration and display in the widgets
	 */
	@Override
	public void setValue(String value) {
		super.setValue(value);
		if (textField != null && !textField.getText().equals(value))
			textField.setText(value);
	}
	
	/**
	 * Save the value when enter is pressed
	 * 
	 * @param e
	 */
	public void actionPerformed(ActionEvent e) {
		setValue(textField.getText());
	}

	/**
	 * Save the value when focus is lost
	 * 
	 * @param e
	 */
	public void focusLost(FocusEvent e) {
		setValue(textField.getText());
	}

	
	/**
	 * Not implemented
	 * @param e
	 */
	public void focusGained(FocusEvent e) {
		//
	}
}
