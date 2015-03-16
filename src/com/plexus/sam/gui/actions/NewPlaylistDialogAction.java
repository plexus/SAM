/*
 * Created on 10-sep-2005
 *
 */
package com.plexus.sam.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.Component;
import java.awt.Frame;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.plexus.sam.SAM;
import com.plexus.sam.config.StaticConfig;
import com.plexus.sam.gui.NamedClass;
import com.plexus.sam.gui.SAMgui;
import com.plexus.sam.gui.dialogs.NewPlaylistDialog;

import java.awt.BorderLayout;
import java.util.ResourceBundle;


/**
 * @author plexus
 *
 */
public class NewPlaylistDialogAction extends AbstractAction {

	private static ResourceBundle i18n = SAM.getBundle("gui");
	
	/**
	 * 
	 */
	public NewPlaylistDialogAction() {
		this.putValue(AbstractAction.NAME, i18n.getString("new_playlist"));
		this.putValue(AbstractAction.SHORT_DESCRIPTION, i18n.getString("new_playlist_tooltip"));
	}
	
	public void actionPerformed(ActionEvent e) {
		new NewPlaylistDialog(SAMgui.singleInstance());
	}

}
