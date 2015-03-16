/*
 * Created on 24-sep-2005
 *
 */
package com.plexus.sam.gui.config;

import java.awt.GridLayout;

import com.plexus.util.IntegerDocument;

/**
 * @author plexus
 *
 */
public class NumberWidget extends TextWidget {

	/**
	 * @param group
	 * @param key
	 * @param descriptionKey
	 */
	public NumberWidget(String group, String key, String descriptionKey) {
		super(group, key, descriptionKey);
		
		this.textField.setDocument(new IntegerDocument());
	}

	/**
	 * 
	 */
	public NumberWidget() {
		this("","","");
	}
}
