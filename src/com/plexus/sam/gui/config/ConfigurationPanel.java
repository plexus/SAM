/*
 * Created on 23-sep-2005
 *
 */
package com.plexus.sam.gui.config;

import java.util.ArrayList;
import java.util.Vector;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.plexus.sam.config.ConfigGroup;

/**
 * Top level panel to control the confiuration. 
 * 
 * @see com.plexus.sam.config.Configuration
 * @author plexus
 */
public class ConfigurationPanel extends JPanel implements ListSelectionListener {
	/**
	 * The panels with widgets, each controlling one {@link ConfigGroup}
	 */
	private Vector<ConfigGroupPanel> groupPanels;

	/**
	 * The listmodel for the JList
	 */
	private DefaultListModel listModel;
	
	/**
	 * The list component which allows selecting a configuration group
	 */
	private JList groupList;
	
	/**
	 * We use this cardlayout to display one ConfigGroupPanel at a time
	 */
	private CardLayout panelStack;
	
	/**
	 * This panel will contain the ConfigGroupPanels
	 */
	private JPanel panelStackContainer;
	
	/**
	 * Set the list of ConfigGroupPanels
	 * 
	 * @param groupPanels
	 */
	public ConfigurationPanel (Vector<ConfigGroupPanel> groupPanels) {
		this.groupPanels = groupPanels;
		this.listModel = new DefaultListModel();
		for (ConfigGroupPanel cgp : groupPanels)
			listModel.addElement(cgp);
		this.groupList = new JList(listModel);
		groupList.setCellRenderer(new GroupCellRenderer());
		groupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		this.panelStack = new CardLayout();
		this.panelStackContainer = new JPanel(panelStack);
		
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		
		this.setLayout(gbl);
		gbc.weightx=1;
		gbc.weighty=1;
		gbc.fill=GridBagConstraints.BOTH;
		this.add(new JScrollPane(groupList), gbc);
		
		gbc.gridx=1;
		gbc.weightx=3;
		this.add(panelStackContainer, gbc);
		
		for (ConfigGroupPanel cgp : groupPanels) {
			panelStack.addLayoutComponent(cgp, cgp.getGroupname());
			panelStackContainer.add(cgp);
		}
		
		groupList.getSelectionModel().addListSelectionListener( this );
	}
	
	/**
	 * Initialize with an empty configgrouppanel list.
	 */
	public ConfigurationPanel () {
		this(new Vector<ConfigGroupPanel>());	
	}
	
	
	/**
	 * @return Returns the groupPanels.
	 */
	public ArrayList<ConfigGroupPanel> getGroupPanels() {
		return new ArrayList<ConfigGroupPanel>(groupPanels);
	}

	/**
	 * @param groupPanels The groupPanels to set.
	 */
	public void setGroupPanels(ArrayList<ConfigGroupPanel> groupPanels) {
		for (ConfigGroupPanel cgp: groupPanels)
			this.addGroupPanel(cgp);
	}
	
	
	/**
	 * Add a ConfigGroupPanel to the list
	 * 
	 * @param cgp
	 */
	private void addGroupPanel(ConfigGroupPanel cgp) {
		panelStackContainer.add(cgp, cgp.getGroupname());
		listModel.addElement(cgp);
		groupPanels.add(cgp);
	}
	
	/**
	 * Add a ConfigGroupPanel to the list at a specific index
	 * 
	 * @param index
	 * @param cgp
	 */
	/*private void addGroupPanel( ConfigGroupPanel cgp, int index ) {
		System.out.println("addGroupPanel "+cgp);
		panelStackContainer.add(cgp, index);
		panelStack.addLayoutComponent(cgp, cgp.getGroupname());
		
		listModel.add(index, cgp);
		groupPanels.add(index, cgp);
	}*/
	
	/**
	 * Remove a configgrouppanel
	 * 
	 * @param cgp
	 */
	public void removeGroupPanel( ConfigGroupPanel cgp ) {
		panelStackContainer.remove(cgp);
		
		listModel.removeElement(cgp);
	}
	
	/**
	 * Remove a configgrouppanel at a specific index
	 * 
	 * @param index
	 */
	public void removeGroupPanel( int index ) {
		panelStackContainer.remove(index);
		
		listModel.remove( index );
	}
	
	/**
	 * @return Number of ConfigGroupPanel objects in our list.
	 */
	public int panelCount() {
		return listModel.getSize();
	}
	
	/**
	 * Implementation of ListSelectionListener
	 * 
	 * @param e transmitted event
	 */
	public void valueChanged(ListSelectionEvent e) {
		ConfigGroupPanel cgp = (ConfigGroupPanel)groupList.getSelectedValue();
		panelStack.show(panelStackContainer, cgp.getGroupname());
	}	
	
	
	
	/**
	 * Cellrenderer for the list with configgroups
	 * 
	 * @author plexus
	 */
	private static class GroupCellRenderer extends DefaultListCellRenderer {
		/**
		 * Render a {@link ConfigGroupPanel}, this method returns its superclass counterpart,
		 * but converts the give value to a String containing the caption property of the 
		 * Panel.
		 * 
		 * @param list list to render for
		 * @param value {@link ConfigGroupPanel} to render
		 * @param index index of the item
		 * @param isSelected true if the item is selected (different background color)
		 * @param cellHasFocus true if the cell has focus (surrounding box)
		 * 
		 * @return a JLabel to display
		 * 
		 */
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			return super.getListCellRendererComponent(list, ((ConfigGroupPanel)value).getCaption(), index, isSelected, cellHasFocus);
		}
	}

}
