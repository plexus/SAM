/*
 * Created on 13-okt-2005
 *
 */
package com.plexus.sam.gui.table;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.EventObject;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import com.plexus.sam.comm.TriggerModel;
import com.plexus.sam.gui.Icons;
import com.plexus.util.Hex;



public class LaunchTriggerCellRenderer extends JLabel implements TableCellRenderer {
	private TriggerModel model;
	
	
	
	public LaunchTriggerCellRenderer(TriggerModel myModel, JTable table) {
		super(Icons.get("play_small"));
		this.model = myModel;
		table.addMouseListener(new LaunchTriggerTableListener(table));
		
		
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		
		
		if (isSelected) {
			setBackground(table.getSelectionBackground());
			setForeground(table.getSelectionForeground());
		} else {
			setBackground(table.getBackground());
			setForeground(table.getForeground());
		}
		setOpaque(true);
		return this;
	}

	private class LaunchTriggerTableListener implements MouseListener {
		
		private JTable table;
		
		public LaunchTriggerTableListener(JTable table) {
		
			this.table = table;
		}
		
		public void mouseClicked(MouseEvent e) {
			if (table.getColumnModel().getColumnIndexAtX(e.getX()) == 2) {
				int row = e.getY() / table.getRowHeight();
				if (row < table.getRowCount() && row >= 0) {
					String hex = (String)table.getModel().getValueAt(row, 2);
					for (char c : Hex.hexToString(hex).toCharArray()) {
						model.byteReceived((byte)c);
					}		
				}
			}
			
		}

		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

	}
	


}
