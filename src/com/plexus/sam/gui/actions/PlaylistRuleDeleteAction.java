/*
 * Created on 6-okt-2005
 *
 */
package com.plexus.sam.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.plexus.sam.SAM;
import com.plexus.sam.audio.Playlist;
import com.plexus.sam.audio.RulesPlaylist;

public class PlaylistRuleDeleteAction extends AbstractAction implements ListSelectionListener {
	private JTable table;
	private RulesPlaylist playlist;
	
	public PlaylistRuleDeleteAction(JTable table, RulesPlaylist playlist) {
		this.table = table;
		this.playlist = playlist;
		table.getSelectionModel().addListSelectionListener(this);
		this.putValue(NAME, SAM.getBundle("gui").getString("remove_songrule"));
	}
	
	public void actionPerformed(ActionEvent e) {
		playlist.removeRule(table.getSelectedRow());
	}
	
	public void valueChanged(ListSelectionEvent e) {
		this.setEnabled (table.getSelectedRowCount()==1);
	}

}
