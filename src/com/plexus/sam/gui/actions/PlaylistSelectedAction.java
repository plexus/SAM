/*
 * Created on 11-sep-2005
 *
 */
package com.plexus.sam.gui.actions;

import java.awt.event.ActionEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JSplitPane;

import com.plexus.sam.audio.Playlist;
import com.plexus.sam.gui.PlaylistBox;
import com.plexus.sam.SAM;

/**
 * Action to be performed when a playlist is selected in the PlaylistPanel. This will show
 * the panel returned by <code>PlayList::getPanelClass()</code> or 
 * <code>PlayList::getEditPanelClass()</code> in the bottom part
 * of the PlaylistPanel.<br /><br />
 * This action is only intended to be used in a PlaylistBox.
 * 
 * @author plexus
 *
 */
public class PlaylistSelectedAction extends AbstractAction {
	private JSplitPane splitPane;
	private boolean editPanel;
	private boolean top;
	
	/**
	 * The splitpane where the panel must be shown.
	 * @param edit false : use  <code>PlayList::getPanelClass()</code>, true : use  <code>PlayList::getEditPanelClass()</code>
	 * @param top true : display panel as top/left component of the splitpane
	 * @param splitPane
	 */
	public PlaylistSelectedAction( JSplitPane splitPane, boolean edit, boolean top) {
		this.splitPane = splitPane;
		this.editPanel = edit;
		this.top = top;
	}
	
	/**
	 * implementation of AbstractAction
	 * 
	 * @param e
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof PlaylistBox) {
			PlaylistBox selectBox = (PlaylistBox) e.getSource();
			
			if (selectBox.getSelectedItem() != null) {
				Playlist selectedPlaylist = (Playlist)selectBox.getSelectedItem(); 
				Class[] constrParamT = {selectedPlaylist.getClass()};
				Constructor panelConstructor;
				try {
					if (editPanel)
						panelConstructor = selectedPlaylist.getEditPanelClass().getConstructor( constrParamT );
					else
						panelConstructor = selectedPlaylist.getPanelClass().getConstructor( constrParamT );
					
					Object[] constrParam = { selectedPlaylist };
					JComponent panel = (JComponent) panelConstructor.newInstance( constrParam );
					int divider = splitPane.getDividerLocation();
					if (top)
						splitPane.setTopComponent( panel );
					else
						splitPane.setBottomComponent( panel );
					splitPane.setDividerLocation(divider);
				} catch (SecurityException e1) {
					SAM.error("failed_setting_selected_playlist", e1);
				} catch (NoSuchMethodException e1) {
					SAM.error("failed_setting_selected_playlist", e1);
				} catch (IllegalArgumentException e1) {
					SAM.error("failed_setting_selected_playlist", e1);
				} catch (InstantiationException e1) {
					SAM.error("failed_setting_selected_playlist", e1);
				} catch (IllegalAccessException e1) {
					SAM.error("failed_setting_selected_playlist", e1);
				} catch (InvocationTargetException e1) {
					SAM.error("failed_setting_selected_playlist", e1);
				}
			}
		}
	}
}
	
