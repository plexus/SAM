/*
 * Created on 12-okt-2005
 *
 */
package com.plexus.sam.gui.table;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;

import com.plexus.sam.SAM;
import com.plexus.sam.gui.Icons;

public class TriggerSongCellRenderer extends SongCellRenderer {

	/**
	 * 
	 */
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		
		JLabel result = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
				cellHasFocus);
		
		if (value == null) {
			result.setText(SAM.getBundle("gui").getString("stop_trigger"));
			result.setIcon(Icons.get("stoptrigger"));
		}
		
		return result;
		
	}

}
