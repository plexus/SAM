/*
 * Created on 29-sep-2005
 *
 */
package com.plexus.sam.gui.table;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import com.plexus.util.Time;

/**
 * Editor class to edit a Time (hour+minute) value in (for instance) a JTable. We use two
 * comboboxes to select the two values.
 * 
 * @author plexus
 *
 */
public class TimeCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
	private JComboBox hours;
	private JComboBox minutes;
	private JPanel editPanel;
	
	/**
	 * Fill the comboboxes
	 *
	 */
	public TimeCellEditor() {
		hours = new JComboBox();
		for (int i = 0 ; i < 24 ; i++) {
			hours.addItem(new Integer(i));
		}
		hours.addActionListener(this);
		
		minutes = new JComboBox();
		for (int i = 0 ; i < 60 ; i++) {
			minutes.addItem(new Integer(i));
		}
		minutes.addActionListener(this);
		
		editPanel = new JPanel();
		editPanel.setLayout(new GridLayout(0,2));
		editPanel.add(hours);
		editPanel.add(minutes);
	}
	
	/**
	 * Returns two comboboxes, one for the hours, one for the minutes
	 * 
	 * @param table 
	 * @param value 
	 * @param isSelected 
	 * @param row 
	 * @param column 
	 * @return a panel with two comboboxes
	 * 
	 */
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		Time t = (Time) value;
		hours.setSelectedIndex(t.getHours());
		minutes.setSelectedIndex(t.getMinutes());
		return editPanel;
	}

	/**
	 * @return a Time object
	 */
	public Object getCellEditorValue() {
		return new Time(hours.getSelectedIndex(), minutes.getSelectedIndex());
	}

	/**
	 * When a selection is made, we stop editing
	 * @param e
	 */
	public void actionPerformed(ActionEvent e) {
		super.fireEditingStopped();
	}
}
