/*
 * Created on 8-sep-2005
 *
 */

package com.plexus.sam.gui;

import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.plexus.sam.audio.SongGroup;
import com.plexus.sam.gui.models.DynamicPlaylistModel;

/**
 * A component part of the DynamicPlaylistEditPanel, that shows the name of a songgroup,
 * and gives a slider to adjust the relevance of the songgroup.
 * 
 * @author plexus
 */
public class GroupRelevanceSlider extends JPanel implements PropertyChangeListener {
	private DynamicPlaylistModel model;
	private SongGroup songGroup;
	private JSlider slider;
	
	/**
	 * Make a new slider that corresponds with the given songgroup, and communicates with the model.
	 * 
	 * @param sg
	 * @param model
	 */
	public GroupRelevanceSlider(SongGroup sg, DynamicPlaylistModel model) {
		super();
		this.model = model;
		this.songGroup = sg;
		
		this.slider = new JSlider(SwingConstants.HORIZONTAL, 0, 99 , Math.round( model.getRelevance(sg) ));
		slider.addChangeListener( new SliderListener() );
		slider.setMajorTickSpacing(48);
		slider.setMinorTickSpacing(12);
		slider.setPaintTicks(true);
		model.addPropertyChangeListener(this);
		this.setLayout(new GridLayout(0,3));
		JLabel label = new JLabel(sg.getName());
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		this.add(label);
		this.add(slider);
		this.add(new JPanel());
	}

	/**
	 * Notifications from the model. First checks wether it is our property that has changed,
	 * then if the value is different from ours, and then adjusts the slider.
	 * 
	 * @param evt Event from the model
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("relevance["+songGroup.getName()+"]") && 
				Math.round( ((Float)evt.getNewValue()).floatValue() ) != slider.getValue() )
			slider.setValue( Math.round( model.getRelevance(songGroup) ) );
		
	}
	
	
	/**
	 * Mini inner class to listen to the slider and adjust the model.
	 */
	private class SliderListener implements ChangeListener {
		/**
		 * Notification of the slider.
		 * @param e 
		 */
		public void stateChanged(ChangeEvent e) {
			model.setRelevance(songGroup, slider.getValue() );
		}
	}

	
}
