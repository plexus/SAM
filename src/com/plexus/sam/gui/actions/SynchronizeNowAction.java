/*
 * Created on 14-sep-2005
 *
 */
package com.plexus.sam.gui.actions;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;

import com.plexus.sam.SAM;
import com.plexus.sam.event.RepositorySynchronizer;

/**
 * Immediatly synchronize the repository
 * 
 */
public class SynchronizeNowAction extends AbstractAction {
	private RepositorySynchronizer reposSync;
	private ResourceBundle i18n = SAM.getBundle("gui");
	private boolean allreadySyncing = false;
	
	/**
	 * Set the object where the syncNow method will be called upon
	 * @param reposSync
	 */
	public SynchronizeNowAction( RepositorySynchronizer reposSync ) {
		this.reposSync = reposSync;
		this.putValue(NAME, i18n.getString("sync_now"));
	}
	
	/**
	 * Action : Synchronize now
	 */
	public void actionPerformed(ActionEvent e) {
		if (!this.allreadySyncing) {
			allreadySyncing = true;
			(new Thread(new Runnable (){
				public void run() {
					reposSync.syncNow();
					allreadySyncing = false;
				}
			})).start();
		}
	}

}
