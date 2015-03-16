/*
 * Created on 12-okt-2005
 *
 */
package com.plexus.sam.gui.table;

import javax.swing.table.AbstractTableModel;

import com.plexus.sam.comm.TriggerListener;
import com.plexus.sam.comm.TriggerModel;
import com.plexus.sam.comm.TriggerModel.Trigger;

public class TriggerTableModel extends AbstractTableModel implements TriggerListener {
	private TriggerModel model;
	
	/**
	 * 
	 * @param m
	 */
	public TriggerTableModel(TriggerModel m) {
		model = m;
		m.addTriggerListener(this);
	}
	
	/**
	 * 
	 */
	public int getColumnCount() {
		return 3;
	}

	/**
	 * 
	 */
	public int getRowCount() {
		return model.triggerSize();
	}

	/**
	 * 
	 */
	public Object getValueAt(int row, int column) {
		Trigger t = model.getTrigger(row);
		
		if (column == 1)
			return t.song;
		
		return t.getTriggerHex();
	}

	public void triggerAdded(Trigger t) {
		super.fireTableDataChanged();
	}

	public void triggerRemoved(Trigger t) {
		super.fireTableDataChanged();
	}

	public void triggerFired(Trigger t) {}

}
