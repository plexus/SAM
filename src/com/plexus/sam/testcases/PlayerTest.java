/*
 * Created on 12-sep-2005
 *
 */
package com.plexus.sam.testcases;

import java.util.Iterator;

import com.plexus.sam.audio.Player;
import com.plexus.sam.audio.Repository;
import com.plexus.sam.audio.SimplePlaylist;
import com.plexus.sam.gui.actions.NextAction;
import com.plexus.sam.gui.actions.PauseAction;
import com.plexus.sam.gui.actions.PlayAction;
import com.plexus.sam.gui.actions.PreviousAction;
import com.plexus.sam.gui.actions.StopAction;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author plexus
 *
 */
public class PlayerTest extends TestCase {
	Player p;
	PreviousAction previous;
	NextAction next;
	PlayAction play;
	PauseAction pause;
	StopAction stop;
	Repository repos;
	SimplePlaylist pl;
	
	/**
	 * @param string
	 */
	public PlayerTest(String string) {
		super(string);
	}

	protected void setUp() throws Exception {
		p = new Player();
		previous = new PreviousAction(p);
		next = new NextAction(p);
		play = new PlayAction(p);
		stop = new StopAction(p);
		pause = new PauseAction(p);
		repos = new Repository();
		repos.load();
		pl = new SimplePlaylist();
		int j = 0;
		for (Iterator i = repos.getSongIds().iterator() ; i.hasNext() && j < 5 ;j++)
			pl.addSong(repos.getSong(((Long)i.next()).longValue()));
		assertTrue("Less than five songs in the repository, can't do the setup.",j==5);
	}
	
	public void testPlayer1() {
		assertNull(p.getPlaylist());
		assertFalse(p.hasNext());
		assertFalse(p.hasPrevious());
		assertFalse(p.isPlaying());
		assertNull(p.getCurrent());
		p.play();
		assertFalse(p.isPlaying());
	}
	
	public void testDelete() {
		p.setPlaylist(pl);
		pl.delete();
		assertNull("Player is still playing a deleted playlist",p.getPlaylist());
	}
	
	public void testActionEnabled() {
		p.setPlaylist(pl);
		assertEquals(pl, p.getPlaylist());
		
		assertFalse(p.hasPrevious());
		assertFalse(previous.isEnabled());
		
		assertFalse(p.isPlaying());
		assertTrue(play.isEnabled());
		
		assertFalse(stop.isEnabled());
		assertFalse(pause.isEnabled());
		
		assertTrue(p.hasNext());
		assertTrue(next.isEnabled());
	}
	
	public static Test suite() { 
	    TestSuite suite= new TestSuite(); 
	    suite.addTest(new PlayerTest("testDelete"));
	    suite.addTest(new PlayerTest("testPlayer1"));
	    suite.addTest(new PlayerTest("testActionEnabled"));
	    return suite;
	}
}
