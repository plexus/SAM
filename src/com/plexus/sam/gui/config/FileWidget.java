/*
 * Created on 24-sep-2005
 *
 */
package com.plexus.sam.gui.config;

import java.awt.event.ActionEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;

/**
 * 
 * @author plexus
 *
 */
public class FileWidget extends TextWidget {
	protected JButton browse;
	
	/**
	 * 
	 * @param group
	 * @param key
	 * @param descriptionKey
	 */
	public FileWidget(String group, String key, String descriptionKey) {
		super(group, key, descriptionKey);
		this.remove(textField);
		this.browse = new JButton(i18n.getString("browse"));
		Box b = Box.createHorizontalBox();
		b.add(textField);
		b.add(browse);
		this.add(b);
		browse.addActionListener(this);
	}

	/**
	 * 
	 *
	 */
	public FileWidget() {
		this("","","");
	}
	
	/**
	 * @param e
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(browse)) {
		    JFileChooser chooser = new JFileChooser();
		    int returnVal = chooser.showOpenDialog(this);
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		    	this.setValue(chooser.getSelectedFile().getAbsolutePath());
		    }
		} else {
			super.actionPerformed(e);
		}
			
		
	}
}
