/*
 * Created on 11-sep-2005
 *
 */
package com.plexus.sam.gui.actions;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.JDialog;

import com.plexus.sam.SAM;

/**
 *
 */
public class CancelDialogAction extends AbstractAction {
	private JDialog dialog;
	private static ResourceBundle i18n = SAM.getBundle("gui");
	
	public CancelDialogAction(JDialog dialog) {
		this.dialog = dialog;
		this.putValue( AbstractAction.NAME, i18n.getString("cancel") );
	}
	
	public void actionPerformed(ActionEvent e) {
		dialog.setVisible(false);
	}

}
