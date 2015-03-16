/*
 * Created on 11-okt-2005
 *
 */
package com.plexus.sam.gui.config;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.RXTXCommDriver;
import gnu.io.RXTXPort;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;



import com.plexus.sam.gui.config.ChoiceWidget.ChoiceValue;


public class SerialPortChoiceWidget extends ChoiceWidget {

	public SerialPortChoiceWidget() {
		
	}
	
	/**
	 * 
	 */
	@Override
	public void fillComboBox() {
		Enumeration e = CommPortIdentifier.getPortIdentifiers();
		while (e.hasMoreElements()) {
			CommPortIdentifier cpi = (CommPortIdentifier)e.nextElement();
			choiceBox.addItem(new ChoiceValue(cpi.getName(), cpi.getName(), cpi.getName(), cpi.getName()));
		}
	}
	
	/**
	 * @param value value to set 
	 */
	@Override
	public void setValue(String value) {
		super.setValue(value);
		if (choiceBox != null) {
			for (int i=0; i<choiceBox.getModel().getSize(); i++) {
				ChoiceValue cv = (ChoiceValue) choiceBox.getModel().getElementAt(i);
				if (cv.getValue().equals(value))
					choiceBox.setSelectedIndex(i);
			}
		}
	}
}
