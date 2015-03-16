/*
 * Created on 28-aug-2005
 *
 */
package com.plexus.sam.audio;

import java.io.File;

import javax.sound.sampled.AudioInputStream;

/**
 * Interface with methods that should be implemented for
 * every supported type of Song. 
 * 
 * @author plexus
 */
public interface Song {
	
	public long getId();
	public void setId(long id);
	
	public String getFile();
	public void setFile(String f);
	
	
    public AudioInputStream getAudioStream();
    public void closeStream();
    public String getTitle();
	public String getAuthor();
	public String getAlbum();
	public int getTrack();
	public String getGenre();
	public String getYear();
	public long getLength(); //in microseconds
	/**
	 * 
	 */
	public boolean delete();
	public AudioInputStream reinitialiseStream();
	
}
