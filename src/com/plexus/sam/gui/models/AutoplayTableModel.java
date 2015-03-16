/*
 * Created on 28-sep-2005
 *
 */
package com.plexus.sam.gui.models;

import javax.swing.table.AbstractTableModel;

import com.plexus.sam.audio.Playlist;
import com.plexus.sam.event.EventRule;
import com.plexus.sam.event.RulemodelListener;
import com.plexus.sam.event.AutoplayRule;
import com.plexus.sam.event.Autoplayer;
import com.plexus.util.Time;

/**
 * Model for the table that displays and edits the autoplayrules.
 * @see Autoplayer
 * @see AutoplayRule
 * @author plexus
 *
 */
public class AutoplayTableModel extends AbstractTableModel implements RulemodelListener {
	private Autoplayer autoplayer;
	
	/**
	 * Create a tablemodel to display the rules registered in the autoplayer
	 * 
	 * @param autoplayer
	 */
	public AutoplayTableModel(Autoplayer autoplayer) {
		this.autoplayer = autoplayer;
		autoplayer.addAutoplayListener(this);
	}
	
	/**
	 * @return the number of rows, this is the number of rules
	 */
	public int getRowCount() {
		return autoplayer.size();
	}

	/**
	 * 0 : day of the week<br/>
	 * 1 : start<br/>
	 * 2 : stop<br/>
	 * 3 : playlist<br/>
	 * 
	 * @return the number of columns, being 4
	 */
	public int getColumnCount() {
		return 4;
	}

	/**
	 * Get the value at a specified row and column
	 * 
	 * @param rowIndex the row to be displayed, being the index of the rule
	 * @param columnIndex value from 0 to 3
	 * <ul>
	 * <li>0 : Integer day of the week</li>
	 * <li>1 : Time start</li>
	 * <li>2 : Time stop</li>
	 * <li>3 : Playlist</li>
	 * </ul>
	 * For day of the week constants, @see Calendar, @see AutoplayRule.
	 * @return the specified value
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case (0): return autoplayer.getRule(rowIndex).getDayofweek();
		case (1): return autoplayer.getRule(rowIndex).getStart();
		case (2): return autoplayer.getRule(rowIndex).getStop();
		case (3): return autoplayer.getRule(rowIndex).getPlaylist();
		}
		return null;
	}

    /**
     *  Returns false.  This is the default implementation for all cells.
     *
     *  @param  rowIndex  the row being queried
     *  @param  columnIndex the column being queried
     *  @return false
     */
	@Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
    }

    /**
     *  This empty implementation is provided so users don't have to implement
     *  this method if their data model is not editable.
     *
     *  @param  aValue   value to assign to cell
     *  @param  rowIndex   row of cell
     *  @param  columnIndex  column of cell
     */
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    	switch (columnIndex) {
    	case (0) :
    		autoplayer.getRule(rowIndex).setDayofweek(((Integer)aValue).intValue());break;
    	case (1) :
    		autoplayer.getRule(rowIndex).setStart((Time)aValue);break;
    	case (2) :
    		autoplayer.getRule(rowIndex).setStop((Time)aValue);break;
    	case (3) :
    		autoplayer.getRule(rowIndex).setPlaylist((Playlist)aValue);break;
    	}
    }
	
	/**
	 * When the autoplayer changes, we redraw from scratch
	 * @param rule
	 */
	public void ruleAdded(EventRule rule) {
		fireTableDataChanged();
		
	}

	/**
	 * When the autoplayer changes, we redraw from scratch
	 * @param rule
	 */
	public void ruleRemoved(EventRule rule) {
		fireTableDataChanged();
		
	}

	/**
	 * When the autoplayer changes, we redraw from scratch
	 * @param rule
	 */
	public void ruleChanged(EventRule rule) {
		fireTableDataChanged();
		
	}
}
