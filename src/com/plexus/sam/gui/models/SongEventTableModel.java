/*
 * Created on 10-okt-2005
 *
 */
package com.plexus.sam.gui.models;

import java.util.Calendar;

import javax.swing.table.AbstractTableModel;

import com.plexus.sam.audio.RulesPlaylist;
import com.plexus.sam.config.Configuration;
import com.plexus.sam.event.EventScheduler;
import com.plexus.sam.event.SongEvent;
import com.plexus.util.Time;

public class SongEventTableModel extends AbstractTableModel {
	private EventScheduler queue;
	private static int numberOfSongEvents;
	
	public SongEventTableModel(RulesPlaylist playlist) {
		this.queue = playlist.getQueue();
		try {
			numberOfSongEvents = 
				Integer.parseInt( Configuration.getConfigGroup("gui").get("numberOfSongEvents") ); 
		} catch (Throwable t) {
			numberOfSongEvents = 5;
		}
	}

	public int getRowCount() {
		return queue.size() < numberOfSongEvents ? queue.size() : numberOfSongEvents;
	}

	public int getColumnCount() {
		return 3;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		SongEvent e = (SongEvent)queue.getEvent(rowIndex);
		Calendar c = Calendar.getInstance();
		c.setTime(e.getTime());
		if (columnIndex == 0) {
			return c.get(Calendar.DAY_OF_WEEK);
		}
		
		if (columnIndex == 1)
			return new Time(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
		
		return e.getSong();
	}

}
