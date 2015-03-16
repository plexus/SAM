/*
 * Created on 12-okt-2005
 *
 */
package com.plexus.sam.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.plexus.sam.SAM;
import com.plexus.sam.comm.ByteListener;
import com.plexus.sam.comm.TriggerModel;
import com.plexus.sam.comm.TriggerModel.Trigger;
import com.plexus.sam.config.Configuration;
import com.plexus.sam.gui.table.LaunchTriggerCellRenderer;
import com.plexus.sam.gui.table.SongCellRenderer;
import com.plexus.sam.gui.table.TriggerSongCellRenderer;
import com.plexus.sam.gui.table.TriggerTableModel;
import com.plexus.util.Hex;
import com.plexus.util.HexDocument;

public class TriggerPanel extends JPanel implements ActionListener, ListSelectionListener, ByteListener {
	private TriggerModel myModel;
	private SongBrowser browser;
	private JRadioButton enable, disable;
	private JTextField hexField;
	private JTable triggerTable;
	private JCheckBox stopTrigger;
	private JSplitPane splitPane;
	
	public TriggerPanel(TriggerModel model) {
		ResourceBundle i18n = SAM.getBundle("gui");
		myModel = model;
		if (Configuration.get("triggers", "active").equals("true"))
			myModel.activate();
		
		this.setLayout(new BorderLayout());
		
		JPanel enablePanel = new JPanel();
		enable = new JRadioButton(i18n.getString("triggers_active"));
		disable = new JRadioButton(i18n.getString("triggers_inactive"));
		
		ButtonGroup group = new ButtonGroup();
		group.add(enable);
		group.add(disable);
		
		enable.addActionListener(this);
		disable.addActionListener(this);
		
		enablePanel.add(enable);
		enablePanel.add(disable);
		enablePanel.setBorder(LineBorder.createBlackLineBorder());
	
		enable.setSelected( myModel.isActive() );
		disable.setSelected(! myModel.isActive() );
		
		this.add(enablePanel, BorderLayout.NORTH);
		
		splitPane = new JSplitPane();
		
		triggerTable = new JTable(new TriggerTableModel(myModel));
		triggerTable.getColumnModel().getColumn(0).setHeaderValue(i18n.getString("HEX"));
		triggerTable.getColumnModel().getColumn(1).setHeaderValue(i18n.getString("SONG"));
		triggerTable.getColumnModel().getColumn(2).setHeaderValue(i18n.getString("TEST"));
		
		triggerTable.getColumnModel().getColumn(1).setCellRenderer(new TriggerSongCellRenderer());
		triggerTable.getColumnModel().getColumn(2).setCellRenderer(new LaunchTriggerCellRenderer(myModel, triggerTable) );
		triggerTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		triggerTable.getSelectionModel().addListSelectionListener(this);
		
		splitPane.setLeftComponent(new JScrollPane(triggerTable));
		
		
		JPanel hexPanel = new JPanel();
		hexPanel.setLayout(new GridLayout(0,3));
		hexPanel.add(new JLabel(i18n.getString("HEX")));
		
		hexField = new JTextField();
		hexField.setDocument(new HexDocument());
		hexField.setEnabled(false);
		
		hexPanel.add(hexField);
		
	    stopTrigger = new JCheckBox(i18n.getString("make_stop_trigger")); 
		stopTrigger.setSelected(false);
		hexPanel.add(stopTrigger);
		
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BorderLayout());
		
		rightPanel.add(hexPanel, BorderLayout.NORTH);
		
		
		browser = new SongBrowser(SAM.repos);
		browser.setEnabled(false);
		
		rightPanel.add(browser, BorderLayout.CENTER);
		
		splitPane.setRightComponent(rightPanel);
		splitPane.setResizeWeight(.5d);
		try {
			splitPane.setDividerLocation(Integer.parseInt(Configuration.get("gui", "triggerPanel_dividerlocation")));
		} catch (Exception e) {}
		
		this.add(splitPane, BorderLayout.CENTER);
		
	
		JPanel addremovepanel = new JPanel();
		addremovepanel.add(new JButton(new AddTriggerAction(myModel, triggerTable, i18n.getString("add_trigger"))));
		addremovepanel.add(new JButton(new RemoveTriggerAction(myModel, triggerTable, i18n.getString("remove_trigger"))));
		addremovepanel.add(new JButton(new SaveChangesAction(browser, hexField, model, triggerTable, stopTrigger, i18n.getString("save_changes"))));
		
		this.add(addremovepanel, BorderLayout.SOUTH);
		
		if (myModel.isActive())
			myModel.getConnection().addByteListener(this);
		
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(enable)) {
			myModel.activate();
			if (!myModel.isActive()) {
				disable.setSelected(true);
			} else {
				myModel.getConnection().addByteListener(this);
			}
			save();
		} else if (e.getSource().equals(disable)) {
			myModel.getConnection().removeByteListener(this);
			myModel.deActivate();
			save();
		}
	}
	
	
	public void save() {
		Configuration.getConfigGroup("triggers").set("active", (myModel.isActive() ? "true" : "false"));
		
	}

	public void valueChanged(ListSelectionEvent e) {
		if (triggerTable.getSelectedRowCount() == 1) {
			Trigger t = myModel.getTrigger(triggerTable.getSelectedRow());
			if (t.song == null)
				stopTrigger.setSelected(true);
			else {
				browser.setSelected(t.song);
				stopTrigger.setSelected(false);
			}
			hexField.setText(t.getTriggerHex());
			browser.setEnabled(true);
			hexField.setEnabled(true);
			stopTrigger.setEnabled(true);
		} else {
			browser.setEnabled(false);
			hexField.setEnabled(false);
			stopTrigger.setEnabled(false);
		}
	}
	
	public static class AddTriggerAction extends AbstractAction {
		private TriggerModel model;
		private JTable table;
		public AddTriggerAction(TriggerModel model, JTable table, String caption) {
			this.model = model;
			this.table = table;
			this.putValue(NAME, caption);
		}

		public void actionPerformed(ActionEvent e) {
			model.addTrigger(new Trigger());
		}
	}
	
	
	public static class RemoveTriggerAction extends AbstractAction implements ListSelectionListener {
		private TriggerModel model;
		private JTable table;
		
		public RemoveTriggerAction(TriggerModel model, JTable triggerTable, String caption) {
			this.putValue(NAME, caption);
			this.model = model;
			this.table = triggerTable;
			triggerTable.getSelectionModel().addListSelectionListener(this);
			this.setEnabled(table.getSelectedRowCount() == 1);
		}
		
		public void actionPerformed(ActionEvent e) {
			model.removeTrigger(model.getTrigger(table.getSelectedRow()));
		}

		public void valueChanged(ListSelectionEvent e) {
			this.setEnabled(table.getSelectedRowCount() == 1);
		}
	}
	
	public static class SaveChangesAction extends AbstractAction implements ListSelectionListener {
		private SongBrowser browser;
		private JTextField hexField;
		private JTable table;
		private TriggerModel model;
		private JCheckBox stop;
		
		public SaveChangesAction(SongBrowser browser, JTextField hexField, TriggerModel model, JTable triggerTable, JCheckBox t, String caption) {
			this.putValue(NAME, caption);
			this.browser = browser;
			this.hexField = hexField;
			this.model = model;
			this.table = triggerTable;
			this.stop=t;
			table.getSelectionModel().addListSelectionListener(this);
		}

		public void actionPerformed(ActionEvent e) {
			Trigger t = model.getTrigger(table.getSelectedRow());
			if (stop.isSelected())
				t.song = null;
			else
				t.song = browser.getSelected();
			stop.setEnabled(false);
			browser.setEnabled(false);
			t.setTriggerHex(hexField.getText());
			hexField.setEnabled(false);
			table.getSelectionModel().clearSelection();
			model.save();
		}

		public void valueChanged(ListSelectionEvent e) {
			this.setEnabled(table.getSelectedRowCount() == 1);
		}
	}

	public void byteReceived(byte b) {
		if (hexField.isEnabled())
			hexField.setText(hexField.getText().concat( Hex.byteToHex((char)b) ));
	}

	public int getSplitterPosition() {	
		return splitPane.getDividerLocation();
	}
}
