/*
 * Created on 28-sep-2005
 *
 */
package com.plexus.sam.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;

import com.plexus.sam.SAM;
import com.plexus.sam.event.Autoplayer;
import com.plexus.sam.gui.actions.AddAutoplayRuleAction;
import com.plexus.sam.gui.actions.RemoveAutoplayRuleAction;
import com.plexus.sam.gui.models.AutoplayTableModel;
import com.plexus.sam.gui.table.AutoplayRuleTable;

/**
 * Top level panel to manage the rules of the autoplayer, and en/disable the autoplayer
 * 
 * @author plexus
 *
 */
public class AutoplayerPanel extends JPanel implements ActionListener {
	private Autoplayer autoplayer;
	private ResourceBundle gui = SAM.getBundle("gui");
	private JRadioButton enable, disable;
	
	/**
	 * Set the autoplayer to display and modify in this panel
	 * 
	 * @param autoplayer
	 */
	public AutoplayerPanel(Autoplayer autoplayer) {
		this.autoplayer = autoplayer;
		this.setLayout(new BorderLayout());
		AutoplayRuleTable table = new AutoplayRuleTable(new AutoplayTableModel(autoplayer));
		
		
		JPanel enablePanel = new JPanel();
		enable = new JRadioButton(gui.getString("enable_autoplayer"));
		disable = new JRadioButton(gui.getString("disable_autoplayer"));
		
		ButtonGroup group = new ButtonGroup();
		group.add(enable);
		group.add(disable);
		
		enable.addActionListener(this);
		disable.addActionListener(this);
		
		enablePanel.add(enable);
		enablePanel.add(disable);
		enablePanel.setBorder(LineBorder.createBlackLineBorder());
	
		enable.setSelected( autoplayer.isEnabled() );
		disable.setSelected(! autoplayer.isEnabled() );
		
		
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(new JButton(new AddAutoplayRuleAction(autoplayer)));
		buttonPanel.add(new JButton(new RemoveAutoplayRuleAction(autoplayer, table)));
		buttonPanel.setBorder(LineBorder.createBlackLineBorder());
		
		add(enablePanel, BorderLayout.NORTH);
		add(new JScrollPane( table ), BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	/**
	 * Enable or disable the autoplayer when the radiobuttons are pressed
	 * @param e
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(enable))
			autoplayer.enable();
		else if (e.getSource().equals(disable))
			autoplayer.disable();
	}

	//Autoplayer todo lijstje:
	//celeditor
	//dees paneel
}
