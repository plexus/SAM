/*
 * Created on 28-sep-2005
 *
 */
package com.plexus.sam.gui.table;

import java.util.ResourceBundle;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import com.plexus.sam.SAM;
import com.plexus.sam.event.Autoplayer;
import com.plexus.sam.gui.models.AutoplayTableModel;

/**
 * Table that displays the rules registered in the {@link Autoplayer}, also
 * allows for editing of these rules.
 * 
 * @author plexus
 *
 */
public class AutoplayRuleTable extends JTable {
	private ResourceBundle i18n = SAM.getBundle("gui");
	
	/**
	 * 
	 * @param model
	 */
	public AutoplayRuleTable(AutoplayTableModel model) {
		super (model);
		setDefaultRenderer(Object.class, new AutoplayCellRenderer());
		
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		columnModel.getColumn(0).setHeaderValue(i18n.getString("DAY"));
		columnModel.getColumn(1).setHeaderValue(i18n.getString("START"));
		columnModel.getColumn(2).setHeaderValue(i18n.getString("STOP"));
		columnModel.getColumn(3).setHeaderValue(i18n.getString("PLAYLIST"));
		
		columnModel.getColumn(0).setCellEditor(new DayofweekCellEditor());
		columnModel.getColumn(1).setCellEditor(new TimeCellEditor());
		columnModel.getColumn(2).setCellEditor(new TimeCellEditor());
		columnModel.getColumn(3).setCellEditor(new PlaylistCellEditor());
		
		columnModel.getColumn(1).setCellRenderer(new TimeCellRenderer());
		columnModel.getColumn(2).setCellRenderer(new TimeCellRenderer());
	}
}
