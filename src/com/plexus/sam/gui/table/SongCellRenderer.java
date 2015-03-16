/*
 * Created on 10-okt-2005
 *
 */

package com.plexus.sam.gui.table;


import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.TableCellRenderer;

import com.plexus.sam.audio.Song;
import com.plexus.sam.audio.SongGroup;
import com.plexus.sam.gui.Icons;


/**
 * CellRenderer for the browser, which displays Songs and SongGroups correctly, with
 * an icon.
 * 
 * @author plexus
 */
public class SongCellRenderer extends JLabel implements ListCellRenderer, TableCellRenderer {
	/**
	 * Show Songgroups with icon and name, songs with icon and title-author.
	 * @param list 
	 * @param value 
	 * @param index 
	 * @param isSelected 
	 * @param cellHasFocus 
	 * 
	 * @return a JLabel to display in the group/song list.
	 */
	public Component getListCellRendererComponent (
			JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		String text;
		if (value != null) {
			if (value instanceof SongGroup) {
				setText( value.toString() );
				setIcon(Icons.get("songgroup"));
			} else if (value instanceof Song) {
				Song song = (Song)value;
				text = song.getAuthor().replaceAll(" *$","");
				text += "-"+song.getTitle();
				setText( text );
				this.setToolTipText(text);
				setIcon(Icons.get("song"));
				
			}
		}
		
		if (list != null) {
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			}
			else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			setEnabled(list.isEnabled());
			setFont(list.getFont());
		} else {
			if (isSelected) {
				setBackground((new JTable()).getSelectionBackground());
				setForeground((new JTable()).getSelectionForeground());
			}
			else {
				setBackground((new JTable()).getBackground());
				setForeground((new JTable()).getForeground());
			}
		}
		setOpaque(true);
		
		return this;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		return this.getListCellRendererComponent(null, value, row, isSelected, hasFocus);
	}
}