/*
 * Created on 11-okt-2005
 *
 */
package com.plexus.sam.comm;

import com.plexus.sam.comm.TriggerModel.Trigger;

public interface TriggerListener {
	public void triggerAdded(Trigger t);
	public void triggerRemoved(Trigger t);
	public void triggerFired(Trigger t);
}
