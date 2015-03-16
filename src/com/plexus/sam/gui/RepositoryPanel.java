/*
 * Created on 14-sep-2005
 *
 */
package com.plexus.sam.gui;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import com.plexus.sam.SAM;
import com.plexus.sam.config.ConfigGroup;
import com.plexus.sam.config.Configuration;
import com.plexus.sam.gui.actions.SaveSyncIntervalAction;
import com.plexus.sam.gui.actions.SynchronizeNowAction;
import com.plexus.sam.gui.actions.ToggleAutoSyncAction;

/**
 * Top-level panel to browse through the repository, with some widgets to synchronize.
 * 
 * @author plexus
 *
 */
public class RepositoryPanel extends JPanel implements MouseListener {
	private JButton syncNow, toggleSync, save;
	private IntervalChooser interval;
	private ConfigGroup syncConfig = Configuration.getConfigGroup("sync");
	private SongBrowser browser;
	
	/**
	 *	Default constructor 
	 */
	public RepositoryPanel() {
		this.browser = new SongBrowser( SAM.repos );
		this.setLayout(new BorderLayout());
		JPanel topPanel = new JPanel();
		syncNow = new JButton(new SynchronizeNowAction(SAM.reposSync));
		toggleSync = new JButton(new ToggleAutoSyncAction());
		interval = new IntervalChooser(Integer.parseInt(syncConfig.get("interval")));
		save = new JButton(new SaveSyncIntervalAction( interval ));
		topPanel.add(syncNow);
		topPanel.add(toggleSync);
		topPanel.add(interval);
		topPanel.add(save);
		topPanel.setBorder(LineBorder.createBlackLineBorder());
		this.add(topPanel, BorderLayout.NORTH);
		this.add(browser, BorderLayout.CENTER);
		browser.addMouseListener(this);
	}
	
	/**
	 * When doubleclicked on a song, we immediatly play the song.
	 *  
	 */
	public void mouseClicked(MouseEvent e) {
		if(e.getClickCount() == 2)
			SAM.player.playNow( browser.getSelected() );		
	}
	
	public void mousePressed(MouseEvent e) {}
	
	public void mouseReleased(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {}
	
	public void mouseExited(MouseEvent e) {}
	
	
	
}
