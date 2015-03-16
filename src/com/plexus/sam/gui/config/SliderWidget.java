/*
 * Created on 13-okt-2005
 *
 */
package com.plexus.sam.gui.config;

import java.awt.GridLayout;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SliderWidget extends ConfigurationWidget implements ChangeListener {
	private JSlider slider;
	
	public SliderWidget(String group, String key, String descriptionKey) {
		super(group, key, descriptionKey);
		slider = new JSlider(0,1000);
		setLayout(new GridLayout(0,2));
		add(this.descriptionLabel);
		add(slider);
		
		slider.addChangeListener(this);
	}
	
	public SliderWidget() {
		this("","","");
	}
	
	public int getMinimum() {
		return slider.getMinimum();
	}

	public int getMaximum() {
		return slider.getMaximum();
	}
	
	public void setMinimum(int m) {
		slider.setMinimum(m);
	}
	
	public void setMaximum(int m) {
		slider.setMaximum(m);
	}
	
	@Override
	public void setValue(String value) {
		super.setValue(value);
		int val = Integer.parseInt(value);
		if (val >= slider.getMinimum() && val <= slider.getMaximum())
			slider.setValue(val);
	}

	public void stateChanged(ChangeEvent e) {
		setValue(""+slider.getValue());
		
	}
}
