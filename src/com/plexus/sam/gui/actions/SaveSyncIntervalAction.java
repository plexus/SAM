/*
 * Created on 14-sep-2005
 *
 */
package com.plexus.sam.gui.actions;

import com.plexus.sam.gui.IntervalChooser;
import com.plexus.sam.config.Configuration;
import com.plexus.sam.SAM;
import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;

/**
 * @author plexus
 *
 */
public class SaveSyncIntervalAction extends AbstractAction {
	private IntervalChooser chooser;
	
	public SaveSyncIntervalAction(IntervalChooser chooser) {
		this.chooser = chooser;
		this.putValue(NAME, SAM.getBundle("gui").getString("save"));
	}
	
	public void actionPerformed(ActionEvent e) {
		Configuration.getConfigGroup("sync").set("interval", ""+chooser.getSeconds());
		Configuration.save();
	}
	
}
