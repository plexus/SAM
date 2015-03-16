/*
 * Created on 12-sep-2005
 *
 */
package com.plexus.sam.gui;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Iterator;
import java.util.Set;

import javax.swing.AbstractListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.plexus.sam.audio.Repository;
import com.plexus.sam.audio.Song;
import com.plexus.sam.audio.SongGroup;
import com.plexus.sam.gui.table.SongCellRenderer;

/**
 * Panel with a list of SongGroups and a list of songs in the selected songgroup.
 * Register PropertyChangeListeners to be notified when the selected song (property
 * selected) changes.
 * 
 * @author plexus
 *
 */
public class SongBrowser extends JPanel implements ListSelectionListener, PropertyChangeListener {
	/**
	 * The song selected in the songlist, null if none selected
	 */
	public Song selected;
	
	/**
	 * The selected group in the grouplist
	 */
	public SongGroup group;
	
	/**
	 * Our listeners
	 */
	private PropertyChangeSupport listeners;
	
	/**
	 * The gui components, a list with groups and one with songs
	 */
	private JList groupList, songList;
	
	/**
	 * The repository to browse
	 */
	public Repository repos;
	
	/**
	 * Set the repository to browse in
	 * 
	 * @param repos
	 */
	public SongBrowser(Repository repos) {
		super();
		this.repos = repos;
		this.listeners = new PropertyChangeSupport(this);
		this.setLayout(new GridLayout(1,2));
		this.group = null;
		this.selected = null;
		this.groupList = new JList(new GroupListModel());
		this.songList = new JList(new SongListModel());
		
		groupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		songList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		groupList.setCellRenderer(new SongCellRenderer());
		songList.setCellRenderer(new SongCellRenderer());
		
		groupList.addListSelectionListener( this );
		songList.addListSelectionListener( this );
		
		this.add( new JScrollPane( groupList ) );
		this.add( new JScrollPane( songList ) );
		
		repos.addPropertyChangeListener(this);
		groupList.setVisibleRowCount(5);
	}
	
	/**
	 * @return Returns the selected song.
	 */
	public Song getSelected() {
		return selected;
	}
	/**
	 * @param selected The song to set selected.
	 */
	public void setSelected(Song selected) {
		if (group == null || !group.getSongList().contains(selected)) {
			Set groupNames = repos.getSongGroupNames();
			SongGroup sg;
			Iterator i = groupNames.iterator();
			if (i.hasNext()) {
				sg = repos.getSongGroup( (String)i.next() );
				while (i.hasNext() && !sg.getSongList().contains( selected )) 
					sg = repos.getSongGroup( (String)i.next() );
			
				if (sg.getSongList().contains( selected ))
					groupList.setSelectedValue(sg, true);
			}
		}
		if (group.getSongList().contains(selected))
			songList.setSelectedValue(selected, true);
	}
	
	/**
	 * Delegate method
	 * @param listener 
	 */
	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
	}
	/**
	 * Delegate method
	 * @param propertyName 
	 * @param listener 
	 */
	@Override
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(propertyName, listener);
	}
	/**
	 * Delegate method
	 * @param listener 
	 */
	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);
	}
	/**
	 * Delegate method
	 * @param propertyName 
	 * @param listener 
	 */
	@Override
	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(propertyName, listener);
	}

	
	/**
	 * @return Returns the selected group.
	 */
	public SongGroup getGroup() {
		return group;
	}
	/**
	 * @param group The group to set selected.
	 */
	public void setGroup(SongGroup group) {
		groupList.setSelectedValue( group, true );
	}

	/**
	 * Listen to the list with groups/songs, and adjust the properties group/selected,
	 * plus update the songlist when a new songgroup is selected.
	 * @param e 
	 * 
	 */
	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource().equals(groupList)) {
			SongGroup oldGroup = group;
			group = (SongGroup) groupList.getSelectedValue();
			((SongListModel)songList.getModel()).update();
			songList.getSelectionModel().clearSelection();
			listeners.firePropertyChange("group", oldGroup, group);
		}
		else if (e.getSource().equals(songList)) {
			Song oldSelected = selected;
			selected = (Song)songList.getSelectedValue();
			listeners.firePropertyChange("selected", oldSelected, selected);
		}
	}

	
	/**
	 * Model for the list with songgroups
	 * 
	 * @author plexus
	 */
	private class GroupListModel extends AbstractListModel {
		
		/**
		 * Number of songgroups
		 * @return number of songgroups
		 */
		public int getSize() {
			return repos.getSongGroupNames().size();
		}

		/**
		 * Iterate over the songlist and return the correct one
		 * @param index 
		 * @return the songgroup at the given index
		 */
		public Object getElementAt(int index) {
			int j = 0;
			Iterator i = repos.getSongGroupNames().iterator();
			while( j < index ) { 
				j++ ; 
				i.next(); 
			}
			return repos.getSongGroup( (String) i.next() );
		}
		
		/**
		 * Update the grouplist
		 */
		public void update() {
			this.fireContentsChanged(this, 0, getSize());
		}
	}
	
	/**
	 * Model for the list with songs
	 * 
	 * @author plexus
	 */
	private class SongListModel extends AbstractListModel {
		
		
		/**
		 * @return number of songs in the selected songgroup, 0 if none selected
		 */
		public int getSize() {
			if (group == null)
				return 0;
			return group.size();
		}

		/**
		 * Return the song at index
		 * @param index
		 * @return the song at the given index
		 */
		public Object getElementAt(int index) {
			return group.getSongByIndex( index );
		}
		
		/**
		 * Update the songlist according to the newly selected songgroup
		 */
		public void update() {
			this.fireContentsChanged(this, 0, getSize());
		}
	}
	


	public void setEnabled(boolean b) {
		groupList.setEnabled(b);
		songList.setEnabled(b);
	}
	
	/**
	 * Delegate methods so one can listen to the songlist for double (or triple or N) clicks.
	 * @param l 
	 */
	@Override
	public void addMouseListener(MouseListener l) {
		songList.addMouseListener(l);
	}

	/**
	 * When the repository changes, we have to update.
	 * @param evt
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		((GroupListModel)this.groupList.getModel()).update();
		((SongListModel)this.songList.getModel()).update();
	}
}
