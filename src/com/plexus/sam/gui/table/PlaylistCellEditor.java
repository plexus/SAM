/*
 * Created on 29-sep-2005
 *
 */
package com.plexus.sam.gui.table;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import com.plexus.sam.SAM;
import com.plexus.sam.audio.Playlist;

public class PlaylistCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener, PropertyChangeListener {
	private JComboBox editBox;
	
	public PlaylistCellEditor() {
		editBox = new JComboBox();
		for (Playlist p : SAM.playlists.getPlaylists())
			editBox.addItem(p);
		editBox.addActionListener(this);
		SAM.playlists.addPropertyChangeListener(this);
	}
	
	/**
	 * @param table 
	 * @param value 
	 * @param isSelected 
	 * @param row 
	 * @param column 
	 * @return a combobox with all the playlists
	 * 
	 */
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		editBox.setSelectedItem(value);
		return editBox;
	}

	/**
	 * @return the selected playlist
	 */
	public Object getCellEditorValue() {
		return editBox.getSelectedItem();
	}

	/**
	 * When a playlist is selected we stop editing
	 * @param e
	 */
	public void actionPerformed(ActionEvent e) {
		super.fireEditingStopped();
	}

	public void propertyChange(PropertyChangeEvent evt) {
		editBox.removeAllItems();
		for (Playlist p : SAM.playlists.getPlaylists())
			editBox.addItem(p);
	}

	
}
