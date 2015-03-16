/*
 * Created on 6-okt-2005
 *
 */
package com.plexus.sam.gui.models;

import javax.swing.table.AbstractTableModel;

import com.plexus.sam.audio.RulesPlaylist;
import com.plexus.sam.audio.Song;
import com.plexus.sam.event.EventRule;
import com.plexus.sam.event.RulemodelListener;
import com.plexus.sam.event.SongRule;
import com.plexus.util.Time;

/**
 * 
 * @author plexus
 *
 */
public class RuleplaylistTableModel extends AbstractTableModel implements RulemodelListener {
	private RulesPlaylist playlist;
	
	/**
	 * 
	 * @param playlist
	 */
	public RuleplaylistTableModel(RulesPlaylist playlist) {
		this.playlist = playlist;
		playlist.addListener(this);
	}
	
	/**
	 * Get the number of rows, this is the number of rules.
	 * @return the number of rows
	 */
	public int getRowCount() {
		return playlist.ruleSize();
	}

	/**
	 * Return the number of colums.<br /><br />
	 * 
	 * 0 : day of the week<br />
	 * 1 : time of execution<br />
	 * 2 : song to be played<br />
	 * 
	 * @return 3
	 */
	public int getColumnCount() {
		return 3;
	}

	/**
	 * @param rowIndex 
	 * @param columnIndex 
	 * @return correct value
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case (0) : return playlist.getRule(rowIndex).getDayofweek();
		case (1) : return playlist.getRule(rowIndex).getTime();
		case (2) : return playlist.getRule(rowIndex).getSong();
		}
		return null;
	}
	
	/**
	 * Notify the table has changed when we receive an event.
	 * 
	 * @param rule affected rule
	 */
	public void ruleAdded(EventRule rule) {
		fireTableDataChanged();
	}
	
	/**
	 * Notify the table has changed when we receive an event.
	 * 
	 * @param rule affected rule
	 */
	public void ruleRemoved(EventRule rule) {
		fireTableDataChanged();
	}
	
	/**
	 * Notify the table has changed when we receive an event.
	 * 
	 * @param rule affected rule
	 */
	public void ruleChanged(EventRule rule) {
		fireTableDataChanged();
	}

	/**
	 * 
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	/**
	 * 
	 */
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		SongRule s = playlist.getRule(rowIndex);
		switch (columnIndex) {
		case (0) : s.setDayofweek(((Integer)aValue).intValue());break;
		case (1) : s.setTime((Time)aValue);break;
		case (2) : s.setSong((Song)aValue);break;
		}
	}

}
