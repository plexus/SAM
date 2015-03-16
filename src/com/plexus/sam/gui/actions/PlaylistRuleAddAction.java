/*
 * Created on 6-okt-2005
 *
 */
package com.plexus.sam.gui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.plexus.sam.SAM;
import com.plexus.sam.audio.RulesPlaylist;
import com.plexus.sam.event.SongRule;



public class PlaylistRuleAddAction extends AbstractAction {
	private RulesPlaylist playlist;
	
	public PlaylistRuleAddAction(RulesPlaylist playlist) {
		this.playlist = playlist;
		this.putValue( AbstractAction.NAME, SAM.getBundle("gui").getString("add_songrule") );
	}
	
	public void actionPerformed(ActionEvent e) {
		playlist.addRule(new SongRule());
		
	}

}
