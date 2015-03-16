/*
 * Created on 14-sep-2005
 *
 */
package com.plexus.sam.gui;

import java.util.ResourceBundle;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.plexus.sam.SAM;
import com.plexus.util.IntegerDocument;

/**
 * @author plexus
 *
 */
public class IntervalChooser extends JPanel {
	private JTextField nr;
	private JComboBox box;
	private ResourceBundle i18n = SAM.getBundle("sam");
	
	/**
	 * Stel het aantal seconden in dat zichtbaar is wanneer de intervalchooser start.
	 * 
	 * @param initialValue 
	 */
	public IntervalChooser(int initialValue) {
		ComboContent[] units = {
				new ComboContent("s", i18n.getString("s")),
				new ComboContent("m", i18n.getString("m")),
				new ComboContent("h", i18n.getString("h")),
				new ComboContent("d", i18n.getString("d"))
			};
		box = new JComboBox( units );
		box.setSelectedIndex( 0 );
		if (initialValue % (60*60*24) == 0) {
			box.setSelectedIndex( 3 );
			initialValue =  initialValue / (60*60*24);
		} else if ( initialValue % (60*60) ==0) {
			box.setSelectedIndex( 2 );
			initialValue =  initialValue / (60*60);
		} else if (initialValue % 60 ==0) {
			box.setSelectedIndex( 1 );
			initialValue =  initialValue / (60);
		}
		
		nr = new JTextField(new IntegerDocument(), ""+initialValue, 10);

		this.add(nr);
		this.add(box);
	}
	
	/**
	 * 
	 * @return
	 */
	public int getSeconds() {
		ComboContent unit = (ComboContent) box.getSelectedItem();
		if (unit.key.equals("s"))
			return Integer.parseInt(nr.getText());
		if (unit.key.equals("m"))
			return Integer.parseInt(nr.getText()) * 60;
		if (unit.key.equals("h"))
			return Integer.parseInt(nr.getText()) * 60 * 60;
		if (unit.key.equals("d"))
			return Integer.parseInt(nr.getText()) * 60 * 60 *24;
		return 0;
	}
	
	/**
	 * 
	 * @author plexus
	 *
	 */
	static private class ComboContent {
		public String key, value;
		/**
		 * 
		 * @param key
		 * @param value
		 */
		public ComboContent(String key, String value) {
			this.key = key;
			this.value = value;
		}
		
		/**
		 * 
		 */
		public String toString() {
			return value;
		}
	}
}
