/*
 * Created on 29-sep-2005
 *
 */
package com.plexus.sam.gui.actions;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;

import com.plexus.sam.SAM;
import com.plexus.sam.event.Autoplayer;

/**
 * Action that adds a rule to the autoplayer
 * 
 * @author plexus
 *
 */
public class AddAutoplayRuleAction extends AbstractAction {
	private Autoplayer autoplayer;
	private static ResourceBundle i18n = SAM.getBundle("gui");
	
	/**
	 * Set the autoplayer component wher the rule should be added to
	 *  
	 * @param autoplayer
	 */
	public AddAutoplayRuleAction(Autoplayer autoplayer) {
		this.autoplayer = autoplayer;
		this.putValue( Action.NAME, i18n.getString("add_autoplayrule") );
	}
	
	/**
	 * Action : add a rule to the {@link Autoplayer}
	 * @param e action event
	 */
	public void actionPerformed(ActionEvent e) {
		autoplayer.addRule();
	}
}
