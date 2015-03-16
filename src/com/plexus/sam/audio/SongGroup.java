/*
 * Created on 2-sep-2005
 *
 */
package com.plexus.sam.audio;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * A songgroup is typically a group of songs of the same genre,
 * for every directory in the repository directory we make a new 
 * songgroup named after the directory. 
 * 
 * @author plexus
 *
 */
public class SongGroup implements Comparable{
	/**
	 * List of songs
	 */
	private List<Song> songs;
	
	/**
	 * Name of the songgroup, equal to its directory name
	 */
	private String name;

	/**
	 * En/disable debugging
	 */
	private boolean DEBUG = false;
	
	/**
	 * Default constructor
	 */
	public SongGroup() {
		songs = new ArrayList<Song>();
		
	}
	
	/**
	 * Get the songs in this songgroup
	 * 
	 * @return array of songs
	 */
	public Song[] getSongs() {
		Object[] o = songs.toArray();
		Song[] s = new Song[o.length]; 
		System.arraycopy(o, 0, s, 0, s.length);
		return s;
	}
	
	/**
	 * @param songs the songs to set
	 */
	public void setSongs( Song[] songs ) {
		debug("setSongs "+songs);
		this.songs = new ArrayList<Song>();
		if ( songs != null )
			for ( int i = 0 ; i < songs.length ; i++ ) {
				this.songs.add(songs[i]);
				debug ("added song "+songs[i]);
			}
	}
	
	/**
	 * @param string
	 */
	private void debug(String string) {
		if (DEBUG)
			System.out.println("SongGroup "+string);
		
	}

	/**
	 * Get the list of songs in this songgroups
	 * 
	 * @return list of songgroups
	 */
	public List<Song> getSongList() {
		return songs;
	}
	
	/**
	 * @return the name of the songgroup
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param n the name to set
	 */
	public void setName(String n) {
		this.name = n;
	}
	
	/**
	 * Return the song with path equal to the path denoted by the File object.
	 * 
	 * @param f Path of a song
	 * @return song or null if the song is not in this songgroup
	 */
	public Song getSongByFile(String f) {
		
		for (Song s : songs) {
			if (s != null && f.equals( s.getFile() ) ) {
				return s;
			}
		}
		return null;
	}
	
	/**
	 * Remove a song from the songgroup
	 * 
	 * @param s song to be removed
	 * @return true if the song was in this songgroup
	 */
	public boolean remove( Song s ) {
		return songs.remove( s );
	}
	
	/**
	 * Add a song
	 * @param s song to add
	 */
	public void add(Song s) {
		songs.add(s);
	}
	
	/**
	 * Delete all songs in this songgroup from disk. This delegates
	 * the deletion to the song objects.
	 */
	public void deleteAll() {
		Song s = null;
		for (ListIterator i = songs.listIterator() ; i.hasNext() ; ) {
			s = (Song)i.next();
			s.delete();
		}
	}
	
	/**
	 * String representation, same as getName()
	 */
	public String toString() {
		return name;
	}

	/**
	 * Implementation of comparable, uses String::compareTo()
	 * to get an alphabetical order.
	 * @param arg0
	 * @return -1 if arg0 is not a songGroup
	 */
	public int compareTo(Object arg0) {
		if (arg0 instanceof SongGroup)
			return name.compareTo( ((SongGroup)arg0) .name );
		else return -1;
	}
	
	/**
	 * Get a song by its index in the songlist
	 * 
	 * @param index
	 * @return
	 */
	public Song getSongByIndex(int index) {
		return (Song) songs.get(index);
	}
	
	/**
	 * @return the number of songs in this group
	 */
	public int size() {
		return songs.size();
	}
}
