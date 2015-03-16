/*
 * Created on 9-sep-2005
 *
 */
package com.plexus.sam.testcases;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.plexus.sam.audio.DynamicPlaylist;
import com.plexus.sam.audio.Playlist;
import com.plexus.sam.audio.SimplePlaylist;
import com.plexus.sam.config.StaticConfig;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * These are tests that can be run on any type of playlists.
 * Each test is ran for every playlist type in the static config.
 * 
 * @author plexus
 */
public class PlayListTest extends TestCase {
	private Playlist[] playlists;
	private Class[] plClasses;
	
	
	public PlayListTest(String s) {
		super(s);
	}
	
	protected void setUp() {
		plClasses = StaticConfig.playlistClasses();
	}
	
	/**
	 * Test if all classes returned by StaticConfig.playlistClasses
	 * implement the PlayList interface
	 *
	 */
	public void testIsPlaylistClass() {
		for (int i = 0 ; i < plClasses.length ; i++) {
			Class[] interfaces = plClasses[i].getInterfaces();
			boolean check = false;
			for (int j = 0 ; j < interfaces.length ; j++) {
				if (interfaces[j].equals(Playlist.class))
					check = true;
			}
			assertTrue(check);
		}
	}
	
	/**
	 * Test wether the getPanelClass and getEditPanelClass methods
	 * of different playlists return subclasses of JComponent with a constructor
	 * that takes a playlist of the same type
	 */
	public void testPanelClass()  {
		Playlist pl;
		for (int i = 0 ; i < plClasses.length ; i++) {
			try {
				pl = (Playlist)plClasses[i].newInstance();
				
				try {
					Class[] constrParamT = {Playlist.class};
					Object[] constrParam = {pl};
					
					Object panel = pl.getPanelClass().getConstructor(constrParamT).newInstance(constrParam);
					Object editPanel = pl.getEditPanelClass().getConstructor(constrParamT).newInstance(constrParam);
					assertTrue(panel instanceof JComponent);
					assertTrue(editPanel instanceof JComponent);
				} catch (SecurityException e) {
					assertTrue(false);
				} catch (NoSuchMethodException e) {
					assertTrue("No constructor of with a playlist as parameter!", false);
				} catch (IllegalArgumentException e) {
					assertTrue(false);
				} catch (InvocationTargetException e) {
					e.getCause().printStackTrace();
					assertTrue("Couldn't construct a panel for "+pl.getClass()+"\n"+e.getCause(),false);
				}
			} catch (InstantiationException e1) {
				assertTrue(false);
			} catch (IllegalAccessException e1) {
				assertTrue(false);
			}
		}
	}
	
	public static Test suite() { 
	    TestSuite suite= new TestSuite(); 
	    suite.addTest(new PlayListTest("testIsPlaylistClass"));
	   // suite.addTest(new PlayListTest("testPanelClass"));
	    return suite;
	}

}
