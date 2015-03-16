/*
 * Created on 27-sep-2005
 *
 */
package com.plexus.sam.testcases;

import java.util.Calendar;

import com.plexus.sam.SAM;
import com.plexus.sam.audio.Player;
import com.plexus.sam.audio.Playlist;
import com.plexus.sam.audio.PlaylistSet;
import com.plexus.sam.audio.Repository;
import com.plexus.sam.event.EventRule;
import com.plexus.sam.event.RulemodelListener;
import com.plexus.sam.event.AutoplayRule;
import com.plexus.sam.event.Autoplayer;
import com.plexus.util.Time;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Test the autoplayer component
 * 
 * @author plexus
 *
 */
public class AutoplayTest extends TestCase implements RulemodelListener {
	private Autoplayer autoplayer;
	private AutoplayRule lastAdded;
	private AutoplayRule lastRemoved;
	private AutoplayRule lastChanged;
	private Playlist playlist0;
	private Playlist playlist1;
	private Player player;
	private Time start1, start2, stop1, stop2;
	
	/**
	 * Necessary constructor
	 * @param string
	 */
	public AutoplayTest(String string) {
		super(string);
	}

	/**
	 * The setup
	 */
	@Override
	public void setUp() {
		SAM.loadLocale();
		SAM.player = new Player();
		player = SAM.player;
		SAM.repos = new Repository();
		SAM.repos.load();
		SAM.playlists = new PlaylistSet();
		SAM.playlists.load();
		autoplayer = new Autoplayer();
		autoplayer.clear();
		autoplayer.addAutoplayListener(this);
		playlist0 = SAM.playlists.getPlaylist(0);
		playlist1 = SAM.playlists.getPlaylist(1);
		this.assertNotSame(playlist0, playlist1);
		autoplayer.addRule();
		lastAdded.setDayofweek(AutoplayRule.EVERYDAY);
		Calendar c = Calendar.getInstance();
		lastAdded.setPlaylist(playlist0);
		start1 = new Time(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE)+1);
		stop1 = new Time(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE)+2);
		lastAdded.setStart(start1);
		lastAdded.setStop(stop1);
		
		autoplayer.addRule();
		lastAdded.setDayofweek(AutoplayRule.EVERYDAY);
		c = Calendar.getInstance();
		lastAdded.setPlaylist(playlist1);
		start2 = new Time(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE)+1);
		stop2 = new Time(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE)+3);
		lastAdded.setStart(start2);
		lastAdded.setStop(stop2);
		
	}
	
	/**
	 * Test if the scheduler works
	 *
	 */
	public void testScheduler() {
		long time = System.currentTimeMillis();
		long time2 = System.currentTimeMillis();
		while (time2 - time < 90*1000)
			time2 = System.currentTimeMillis();
		assertEquals("Rule did not set right playlist.",player.getPlaylist(), playlist0);
		assertTrue("Rule did not start player.",player.isPlaying());
		
		time = time2 = System.currentTimeMillis();
		while (time2 - time < 60*1000)
			time2 = System.currentTimeMillis();
		assertFalse("Rule did not stop player.",player.isPlaying());
		
		time = time2 = System.currentTimeMillis();
		while (time2 - time < 60*1000)
			time2 = System.currentTimeMillis();
		assertEquals("Rule did not set right playlist.",player.getPlaylist(), playlist1);
		assertTrue("Rule did not start player.",player.isPlaying());
		
		time = time2 = System.currentTimeMillis();
		while (time2 - time < 60*1000)
			time2 = System.currentTimeMillis();
		assertFalse("Rule did not stop player.",player.isPlaying());
	}
	
	/**
	 * Test if loading and saving the rules works
	 *
	 */
	public void testPersistance() {
		autoplayer.save();
		autoplayer = new Autoplayer();
		assertEquals("Didn't load or save the rules.",autoplayer.size(), 2);
		AutoplayRule rule1 = autoplayer.getRule(0);
		AutoplayRule rule2 = autoplayer.getRule(1);
		assertEquals("Day of week not saved or loaded correctly", rule1.getDayofweek(), AutoplayRule.EVERYDAY);
		assertEquals("Start time rule 1 not saved/loaded correctly", rule1.getStart(), start1);
		assertEquals("Stop time rule 1 not saved/loaded correctly",rule1.getStop(), stop1);
		assertEquals("Start time rule 2 not saved/loaded correctly",rule2.getStart(), start2);
		assertEquals("Stop time rule 2 not saved/loaded correctly",rule2.getStop(), stop2);
	}
	
	/**
	 * Make this a testSuite
	 * @return a TestSuite instance with all the tests in this class
	 */
	public static Test suite() {
		TestSuite testSuite = new TestSuite();
		//testSuite.addTest(new AutoplayTest("testScheduler"));
		testSuite.addTest(new AutoplayTest("testPersistance"));
		return testSuite;
		
	}

	public void ruleAdded(EventRule rule) {
		lastAdded = (AutoplayRule)rule;
		System.out.println(lastAdded);
	}

	public void ruleRemoved(EventRule rule) {
		lastRemoved = (AutoplayRule)rule;
	}

	public void ruleChanged(EventRule rule) {
		lastChanged = (AutoplayRule)rule;
	}
}
