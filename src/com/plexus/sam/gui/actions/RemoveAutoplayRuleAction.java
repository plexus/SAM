/*
 * Created on 29-sep-2005
 *
 */
package com.plexus.sam.gui.actions;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.plexus.sam.SAM;
import com.plexus.sam.event.Autoplayer;
import com.plexus.sam.gui.table.AutoplayRuleTable;


/**
 * Action that removes the rule that was selected in a {@link AutoplayRuleTable}.
 * @author plexus
 *
 */
public class RemoveAutoplayRuleAction extends AbstractAction implements ListSelectionListener  {
	private Autoplayer autoplayer;
	private AutoplayRuleTable table;
	private static ResourceBundle i18n = SAM.getBundle("gui");
	
	/**
	 * Set autoplayer where the rule selected in the table should be removed from.
	 * 
	 * @param autoplayer
	 * @param table
	 */
	public RemoveAutoplayRuleAction(Autoplayer autoplayer, AutoplayRuleTable table) {
		this.autoplayer = autoplayer;
		this.table = table;
		table.getSelectionModel().addListSelectionListener(this);
		this.putValue(Action.NAME, i18n.getString("remove_autoplayrule"));
	}
	
	/**
	 * Action : Remove selected rule
	 * @param e
	 */
	public void actionPerformed(ActionEvent e) {
		autoplayer.removeRule(autoplayer.getRule(table.getSelectedRow()));
	}

	/**
	 * Listen for selection changes, when no rule is selected we disable
	 * @param e
	 */
	public void valueChanged(ListSelectionEvent e) {
		this.setEnabled(table.getSelectedRow() != -1);
	}

}
