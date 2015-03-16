/*
 * Created on 6-okt-2005
 *
 */
package com.plexus.sam.gui.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.Calendar;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

import com.plexus.sam.SAM;
import com.plexus.sam.audio.Song;
import com.plexus.sam.event.EventRule;
import com.plexus.util.Time;

/**
 * Cell renderer for the table that shows and edits the rules of a Rulesplaylist
 * 
 * @author plexus
 *
 */
public class DayofWeekCellRenderer extends DefaultTableCellRenderer {
	private ResourceBundle i18n = SAM.getBundle("sam");
	private SongCellRenderer songRenderer = new SongCellRenderer();
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
}