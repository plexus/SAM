/*
 * Created on 28-sep-2005
 *
 */
package com.plexus.sam.gui.table;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.plexus.sam.SAM;
import com.plexus.sam.event.AutoplayRule;
import com.plexus.sam.event.EventRule;

import java.util.Calendar;
import java.util.ResourceBundle;

/**
 * CellRenderer for the AutoplayRuleTable
 * @author plexus
 *
 */
public class AutoplayCellRenderer extends DefaultTableCellRenderer {
	private ResourceBundle i18n = SAM.getBundle("sam");
	
	/**
	 * Main method of the TableCellRenderer interface, we delegate the work to
	 * our superclass.
	 * 
	 * @param table
	 * @param value 
	 * @param isSelected 
	 * @param hasFocus 
	 * @param row 
	 * @param column 
	 * @return a JLabel that can be displayed in the table
	 * @see DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, 
			boolean isSelected, boolean hasFocus, int row, int column) {
		if (column == 0) {
			String day = "";
			switch(((Integer)value).intValue()) {
			case(Calendar.MONDAY): day = i18n.getString("MONDAY"); break;
			case(Calendar.TUESDAY): day = i18n.getString("TUESDAY"); break;
			case(Calendar.WEDNESDAY): day = i18n.getString("WEDNESDAY"); break;
			case(Calendar.THURSDAY): day = i18n.getString("THURSDAY"); break;
			case(Calendar.FRIDAY): day = i18n.getString("FRIDAY"); break;
			case(Calendar.SATURDAY): day = i18n.getString("SATURDAY"); break;
			case(Calendar.SUNDAY): day = i18n.getString("SUNDAY"); break;
			case(EventRule.EVERYDAY): day = i18n.getString("EVERYDAY"); break;
			default: assert false;
			}
			return super.getTableCellRendererComponent(table, day, isSelected, hasFocus, row, column);
		}
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	} 
}
