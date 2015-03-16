/*
 * Created on 11-sep-2005
 *
 */
package com.plexus.sam.testcases;

import java.util.Iterator;
import java.util.Locale;

import com.plexus.sam.SAM;
import com.plexus.sam.audio.Player;
import com.plexus.sam.audio.Playlist;
import com.plexus.sam.audio.Repository;
import com.plexus.sam.audio.SimplePlaylist;
import com.plexus.sam.audio.Song;
import com.plexus.sam.audio.SongGroup;
import com.plexus.sam.config.ConfigGroup;
import com.plexus.sam.config.Configuration;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author plexus
 *
 */
public class SimplePlaylistTest extends TestCase {
	private Player player;
	private SimplePlaylist playlist;
	private Repository repos;
	private Song song1, song2, song3;
	private ConfigGroup i18nConfig = Configuration.getConfigGroup( "i18n" );
	
	public SimplePlaylistTest(String s) {
		super(s);
	}
	
	public void setUp() {
		if (i18nConfig.get( "set_locale" ).toLowerCase().equals( "true" )) {
			Locale.setDefault( 
					new Locale( i18nConfig.get( "language_code" ), i18nConfig.get( "country_code" ) ) 
					);
			
		}
		repos = new Repository();
		repos.load();
		player = new Player();
		SAM.setPlayer(player);
		playlist = new SimplePlaylist();
		if (repos.getSongGroupNames().size() > 0) {
			SongGroup sg = repos.getSongGroup((String)repos.getSongGroupNames().iterator().next());
			Iterator i = sg.getSongList().iterator();
			assertTrue("Not enough songs in songgroup "+sg+" to do the testing.",i.hasNext());
			song1 = (Song)i.next();
			assertTrue("Not enough songs in songgroup "+sg+" to do the testing.",i.hasNext());
			song2 = (Song)i.next();
			assertTrue("Not enough songs in songgroup "+sg+" to do the testing.",i.hasNext());
			song3 = (Song)i.next();
		} else {
			assertTrue("Repository is empty!",false);
		}
		playlist.addSong(song1);
		playlist.addSong(song2);
		playlist.addSong(song2);
		playlist.addSong(song3);
	}

	public void setPlaylist() {
		player.setPlaylist(playlist);
		assertEquals(playlist, player.getPlaylist());
		assertFalse(player.hasPrevious());
		assertTrue(player.hasNext());
		assertEquals(song1, player.getCurrent());
	}
	
	public void playingOrder() {
		setPlaylist();
		
		assertTrue(player.hasNext());
		player.next();
		assertEquals(song2, player.getCurrent());
		
		assertTrue(player.hasNext());
		player.next();
		assertEquals(song2, player.getCurrent());
		
		assertTrue(player.hasNext());
		player.next();
		assertEquals(song3, player.getCurrent());
		assertFalse(player.hasNext());
		
		assertTrue(player.hasPrevious());
		player.previous();
		assertEquals(song2, player.getCurrent());
		
		assertTrue(player.hasPrevious());
		player.previous();
		assertEquals(song2, player.getCurrent());
		
		assertTrue(player.hasPrevious());
		player.previous();
		assertEquals(song1, player.getCurrent());
		assertFalse(player.hasPrevious());
	}
	
	public static Test suite() { 
	    TestSuite suite= new TestSuite(); 
	    suite.addTest(new SimplePlaylistTest("setPlaylist"));
	    suite.addTest(new SimplePlaylistTest("playingOrder"));
	    return suite;
	}
}
