/*
 * Created on 11-okt-2005
 *
 */
package com.plexus.sam.gui.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import com.plexus.util.Time;

/**
 * 
 * @author plexus
 *
 */
public class TimeCellRenderer extends DefaultTableCellRenderer {

	/**
	 * @param table 
	 * @param value 
	 * @param isSelected 
	 * @param hasFocus 
	 * @param row 
	 * @param column 
	 * @return component
	 * 
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JPanel p = new JPanel(new GridLayout(0,3));
		assert (value instanceof Time) : table;
		Time t = (Time)value;
		String hour = ""+t.getHours();
		if (t.getHours() < 10)
			hour = "0"+hour;
		
		String minute = ""+t.getMinutes();
		if (t.getMinutes() < 10)
			minute = "0"+minute;
			
		p.add(new JLabel());
		p.add(new JLabel(hour+":"+minute));
		p.add(new JLabel());
		
		if (isSelected) {
			p.setForeground(table.getSelectionForeground());
			p.setBackground(table.getSelectionBackground());
		}
		
		
		p.setFont(table.getFont());
		
		if (hasFocus) {
			p.setBorder( UIManager.getBorder("Table.focusCellHighlightBorder") );
			if (!isSelected && table.isCellEditable(row, column)) {
				Color col;
				col = UIManager.getColor("Table.focusCellForeground");
				if (col != null) {
					p.setForeground(col);
				}
				col = UIManager.getColor("Table.focusCellBackground");
				if (col != null) {
					p.setBackground(col);
				}
			}
		} else {
			p.setBorder(noFocusBorder);
		}
		return p;
	}
	

}
