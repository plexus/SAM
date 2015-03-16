/*
 * Created on 10-okt-2005
 *
 */
package com.plexus.sam.gui.table;

import java.util.ResourceBundle;

import javax.swing.JTable;

import com.plexus.sam.SAM;
import com.plexus.sam.audio.RulesPlaylist;
import com.plexus.sam.config.Configuration;
import com.plexus.sam.event.SchedulerListener;
import com.plexus.sam.gui.models.RuleplaylistTableModel;
import com.plexus.sam.gui.models.SongEventTableModel;



public class SongEventTable extends JTable implements SchedulerListener {
	
	public SongEventTable(RulesPlaylist playlist) {
		ResourceBundle i18n = SAM.getBundle("gui");

		
		this.setModel(new SongEventTableModel(playlist));
		this.getColumnModel().getColumn(0).setCellRenderer(new DayofWeekCellRenderer());
		this.getColumnModel().getColumn(1).setCellRenderer(new TimeCellRenderer());
		this.getColumnModel().getColumn(2).setCellRenderer(new SongCellRenderer());
		
		this.getColumnModel().getColumn(0).setHeaderValue(i18n.getString("DAY"));
		this.getColumnModel().getColumn(1).setHeaderValue(i18n.getString("TIME"));
		this.getColumnModel().getColumn(2).setHeaderValue(i18n.getString("SONG"));
		
		playlist.getQueue().addListener(this);
	}

	public void eventListChanged() {
		this.invalidate();
	}
}
