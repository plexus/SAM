/*
 * Created on 29-sep-2005
 *
 */
package com.plexus.sam.gui.table;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.ResourceBundle;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import com.plexus.sam.SAM;
import com.plexus.sam.event.EventRule;

/**
 * CellEditor that allows to edit a 'Day of week' as a combobox
 * 
 * @author plexus
 *
 */
public class DayofweekCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
	private JComboBox editBox;
	private ResourceBundle i18n = SAM.getBundle("sam");
	
	/**
	 * Fill the combobox
	 *
	 */
	public DayofweekCellEditor() {
		editBox = new JComboBox();
		editBox.addItem(new ComboItem(i18n.getString("MONDAY"), Calendar.MONDAY));
		editBox.addItem(new ComboItem(i18n.getString("TUESDAY"), Calendar.TUESDAY));
		editBox.addItem(new ComboItem(i18n.getString("WEDNESDAY"), Calendar.WEDNESDAY));
		editBox.addItem(new ComboItem(i18n.getString("THURSDAY"), Calendar.THURSDAY));
		editBox.addItem(new ComboItem(i18n.getString("FRIDAY"), Calendar.FRIDAY));
		editBox.addItem(new ComboItem(i18n.getString("SATURDAY"), Calendar.SATURDAY));
		editBox.addItem(new ComboItem(i18n.getString("SUNDAY"), Calendar.SUNDAY));
		editBox.addItem(new ComboItem(i18n.getString("EVERYDAY"), EventRule.EVERYDAY));
		editBox.addActionListener(this);
	}
	
	/**
	 * @param table 
	 * @param value 
	 * @param isSelected 
	 * @param row 
	 * @param column 
	 * @return a combobox where one can choose a day of the week
	 * 
	 */
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		for (int i = 0; i < editBox.getItemCount() ; i++) {
			ComboItem item = (ComboItem)editBox.getItemAt(i);
			if (item.day == ((Integer)value).intValue() )
				editBox.setSelectedIndex(i);
			
		}
		return editBox;
	}

	/**
	 * @return a Integer object containing a value, either one of the Calendar weekday constants,
	 * 			or Autoplayer.EVERYDAY 
	 * 
	 */
	public Object getCellEditorValue() {
		return new Integer(((ComboItem)editBox.getSelectedItem()).day);
	}

	/**
	 * Auxiliary class to store our day constants with a displayable string in the combobox
	 * 
	 * @author plexus
	 *
	 */
	private class ComboItem {
		/**
		 * The string to be displayed
		 */
		public String display;
		
		/**
		 * The day that corresponds with its display name
		 */
		public int day;
		
		/**
		 * Constructor for convenience
		 * @param display 
		 * @param day 
		 */
		public ComboItem(String display, int day) {
			this.display = display;
			this.day = day;
		}
		
		/**
		 * @return the display value set
		 * 
		 */
		@Override
		public String toString() {
			return display;
		}
	}

	/**
	 * When a selection is made, we stop editing
	 * @param e
	 */
	public void actionPerformed(ActionEvent e) {
		super.fireEditingStopped();
	}
}
