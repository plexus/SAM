/*
 * Created on 29-sep-2005
 *
 */
package com.plexus.sam.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;

import com.plexus.sam.SAM;
import com.plexus.sam.audio.RulesPlaylist;
import com.plexus.sam.gui.actions.PlaylistRuleAddAction;
import com.plexus.sam.gui.actions.PlaylistRuleDeleteAction;
import com.plexus.sam.gui.models.RuleplaylistTableModel;
import com.plexus.sam.gui.table.DayofweekCellEditor;
import com.plexus.sam.gui.table.DayofWeekCellRenderer;
import com.plexus.sam.gui.table.SongCellRenderer;
import com.plexus.sam.gui.table.TimeCellEditor;
import com.plexus.sam.gui.table.TimeCellRenderer;

/**
 * 
 * @author plexus
 *
 */
public class RulesPlaylistEditPanel extends JSplitPane {
	private RulesPlaylist  playlist;
	private ResourceBundle i18n = SAM.getBundle("gui");
	private SongBrowser browser;
	/**
	 * 
	 * @param playlist
	 */
	public RulesPlaylistEditPanel(RulesPlaylist playlist) {
		this.playlist = playlist;
		//this.setOrientation(JSplitPane.VERTICAL_SPLIT);
		this.setResizeWeight(0.5d);
		this.setTopComponent(new DynamicPlaylistEditPanel(playlist));
		
		JTable table = new JTable(new RuleplaylistTableModel(playlist));
		
		TableColumnModel cmodel = table.getColumnModel();
		cmodel.getColumn(0).setHeaderValue(i18n.getString("DAY"));
		cmodel.getColumn(1).setHeaderValue(i18n.getString("TIME"));
		cmodel.getColumn(2).setHeaderValue(i18n.getString("SONG"));
		
		browser = new SongBrowser(SAM.repos);
		browser.setEnabled(false);
		
		cmodel.getColumn(0).setCellEditor(new DayofweekCellEditor());
		cmodel.getColumn(1).setCellEditor(new TimeCellEditor());
		cmodel.getColumn(2).setCellEditor(new SongCellEditor(browser));
		
		cmodel.getColumn(0).setCellRenderer(new DayofWeekCellRenderer());
		cmodel.getColumn(1).setCellRenderer(new TimeCellRenderer());
		cmodel.getColumn(2).setCellRenderer(new SongCellRenderer());
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(new JButton(new PlaylistRuleAddAction(playlist)));
		
		buttonPanel.add(new JButton(new PlaylistRuleDeleteAction(table, playlist)));
		
		JSplitPane bottomPanel = new JSplitPane();
		bottomPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
		bottomPanel.setResizeWeight(0.5d);
		
		JScrollPane tablePane = new JScrollPane(table);
		JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel.add(tablePane, BorderLayout.CENTER);
		tablePanel.add(buttonPanel, BorderLayout.NORTH);
		
		bottomPanel.setTopComponent(tablePanel);
		bottomPanel.setBottomComponent(browser);
		
		this.setBottomComponent(bottomPanel);
	}

	public SongBrowser getBrowser() {
		return browser;
	}
	
	private static class SongCellEditor extends AbstractCellEditor implements TableCellEditor, MouseListener {
		private SongBrowser browser;
		public SongCellEditor(SongBrowser browser) {
			this.browser = browser;
			browser.addMouseListener(this);
		}
		
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			browser.setEnabled(true);
			JLabel label = new JLabel();
			label.setBackground(Color.GRAY);
			return label;
		}

		
		public Object getCellEditorValue() {
			browser.setEnabled(false);
			return browser.getSelected();
		}

		public void mouseClicked(MouseEvent e) {
			super.stopCellEditing();
		}

		public void mousePressed(MouseEvent e) {}

		public void mouseReleased(MouseEvent e) {}

		public void mouseEntered(MouseEvent e) {}

		public void mouseExited(MouseEvent e) {}
		
	}
}
