/*
 * Created on 11-okt-2005
 *
 */
package com.plexus.sam.testcases;

import com.plexus.sam.SAM;
import com.plexus.sam.audio.Song;
import com.plexus.sam.comm.TriggerListener;
import com.plexus.sam.comm.TriggerModel;
import com.plexus.sam.comm.TriggerModel.Trigger;
import com.plexus.sam.config.StaticConfig;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TriggersTest extends TestCase implements TriggerListener {
	public TriggerModel model;
	public Song song;
	public Trigger trigger;
	public Trigger stoptrigger;
	private Trigger lastAdded;
	private Trigger lastFired;
	
	
	public TriggersTest(String s) {
		super(s);
	}
	
	protected void setUp() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				SAM.main(new String[0]);
			}});
		t.start();
		
		wachteffe(5000);
		
		model = new TriggerModel(SAM.player);
		model.clear();
		song = SAM.repos.getSong( ( (Long)(SAM.repos.getSongIds().iterator().next()) ).longValue() );
		
		trigger = new Trigger("AB", song);
		stoptrigger = new Trigger("STO", null);
		
		model.addTriggerListener(this);
		
		model.addTrigger(trigger);
		assert lastAdded == trigger;
		
		model.addTrigger(stoptrigger);
		assert lastAdded == stoptrigger;
		
		SAM.player.setVolume(800);
		SAM.player.play();
	}
	
	public void testSingleTrigger() {
		wachteffe(4000);
		model.byteReceived((byte)'C');
		model.byteReceived((byte)'A');
		model.byteReceived((byte)'B');
		model.byteReceived((byte)'V');
		assert lastFired == trigger;
		wachteffe(10000);
		assert SAM.player.getVolume() < 800;
		model.byteReceived((byte)'S');
		model.byteReceived((byte)'T');
		model.byteReceived((byte)'O');
		assert lastFired == stoptrigger;
		wachteffe(5000);
		assert SAM.player.getVolume() == 800;
		SAM.gui.dispose();
	}
	
	public void testStartStop() {
		wachteffe(4000);
		model.byteReceived((byte)'C');
		model.byteReceived((byte)'A');
		model.byteReceived((byte)'B');
		model.byteReceived((byte)'V');
		SAM.player.play();
		assert lastFired == trigger;
		assert model.getOldVolume() == 800;
		wachteffe(2000);
		SAM.player.pause();
		assert model.getOldVolume() == 800;
		wachteffe(1000);
		SAM.player.play();
		assert model.getOldVolume() == 800;
		wachteffe(1000);
		SAM.player.stop();
		assert model.getOldVolume() == 800;
		wachteffe(1000);
		assert SAM.player.getVolume() < 800;
		model.byteReceived((byte)'S');
		model.byteReceived((byte)'T');
		model.byteReceived((byte)'O');
		assert lastFired == stoptrigger;
		SAM.player.play();
		wachteffe(2000);
		SAM.player.stop();
		wachteffe(2000);
		assert SAM.player.getVolume() == 800;
		SAM.gui.dispose();
	}
	
	private void wachteffe(long ms) {
		long start = System.currentTimeMillis();
		while (true)
			if (System.currentTimeMillis() - start >= ms)
				return;
	}
	
	public static Test suite() {
		TestSuite suite= new TestSuite(); 
	    suite.addTest(new TriggersTest("testSingleTrigger"));
	    suite.addTest(new TriggersTest("testStartStop"));
	    return suite;
	}

	public void triggerAdded(Trigger t) {
		lastAdded = t;
	}

	public void triggerRemoved(Trigger t) {
		// TODO Auto-generated method stub
		
	}

	public void triggerFired(Trigger t) {
		lastFired = t;
		
	}

}
