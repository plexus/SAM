/*
 * Created on 2-sep-2005
 *
 */
package com.plexus.sam.audio;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import javax.sound.sampled.UnsupportedAudioFileException;

import com.plexus.sam.SAM;
import com.plexus.sam.config.ConfigGroup;
import com.plexus.sam.config.Configuration;
import com.plexus.sam.net.FileTransferModule;
import com.plexus.sam.net.RemoteFile;

/**
 * Repository of songs and songgroups.
 * 
 * @author plexus
 */
public class Repository {
	/**
	 * Mapping of Id to Song
	 */
	private Map<Long, Song> songs;
	
	/**
	 * Mapping of songgroupname to SongGroup
	 */
	private Map<String, SongGroup> songGroups;
	
	/**
	 * The next Id to be used for a new song
	 */
	private long nextId = 1;
	
	
	/**
	 * General configuration
	 */
	private ConfigGroup general;

	/**
	 * En/disable debugging
	 */
	private boolean DEBUG = false;
	
	/**
	 * Notify when songs are added/removed 
	 */
	private PropertyChangeSupport listeners;
	
	/**
	 * Default constructor
	 */
	public Repository() {
		debug("new Repository()"); 
		this.songs = new HashMap<Long, Song>();
		this.songGroups = new HashMap<String, SongGroup>();
		this.general = Configuration.getConfigGroup( "general" );
		this.listeners = new PropertyChangeSupport( this );
	}
	
	
	/**
	 * Save the repository data to an XML file. @see Repository#load()
	 */
	public void save() {
	    XMLEncoder e;
		try {
			e = new XMLEncoder(
			        new BufferedOutputStream(
			            new FileOutputStream(general.get("repository_xml_path"))));
			
			e.writeObject(new Long(nextId));
			Collection songGroupSet = songGroups.values();			
			for (Iterator i = songGroupSet.iterator() ; i.hasNext() ; ) {
		    	SongGroup sg = (SongGroup)i.next();
		    			    	e.writeObject(sg);
		    }
		    e.close();
		} catch (FileNotFoundException ex) {
			SAM.error("bad_repository_xml_path", ex);
		}
	}
	
	/**
	 * Load the repository from disk (from an XML file written
	 * by XMLEncoder).
	 * The path to the file comes from the general configuration.
	 */
	public void load() {
		XMLDecoder d;
		debug ("loading from path "+general.get("repository_xml_path"));
		try {
			d = new XMLDecoder(
			        new BufferedInputStream(
			            new FileInputStream(
			            		general.get("repository_xml_path"))));
			try {
				nextId = (( Long )d.readObject()). longValue() ;
				try {
					debug ("loaded nextId="+nextId);
					SongGroup sg;
					while (true) {
						sg = (SongGroup) d.readObject();
						songGroups.put(sg.getName(), sg);
						addSongsFromSongGroup( sg );
					}
					
				} catch(ArrayIndexOutOfBoundsException e) {
					//End of XML file
					d.close();
				}
			} catch (NoSuchElementException e) {
				//repository file is empty!
				nextId = 1;
			}
		} catch (FileNotFoundException e) {
			nextId =1;
			SAM.error("'bad_repository_xml_path", e);
		}
	}

	/**
	 * Add all the songs in a songgroup to the repository songs-map.
	 * 
	 * @param sg
	 */
	private void addSongsFromSongGroup (SongGroup sg) {
		debug("Adding songgroup '"+sg+"'");
		Song[] s = sg.getSongs();
		for(int i = 0; i < s.length ; i++) {
			debug("adding song "+s[i]);
			if (s[i] != null) {
				long id = s[i].getId();
				songs.put( new Long( id ), s[i] );
				if ( nextId <= id )
					nextId = id+1;
			}
		}
	}
	
	/**
	 * Get a specific song by identifier
	 * 
	 * @param id
	 * @return null if there is no song with this id.
	 */
	public Song getSong(long id) {
		return  songs.get(new Long(id));
		
	}
	
	/**
	 * Get a songgroup by its name
	 * 
	 * @param name
	 * @return null if the songgroup doesn't exist
	 */
	public SongGroup getSongGroup(String name) {
		return songGroups.get(name);
	}
	
	/**
	 * @return a set with all the known songgroup names
	 */
	public Set getSongGroupNames() {
		return songGroups.keySet();
	}
	
	/**
	 * @return a set with all current ids (Long objects).
	 */
	public Set getSongIds() {
		return songs.keySet();
	}
	
	/**
	 * Synchronize with the remote repository, adding and removing
	 * songs accordingly.
	 * 
	 * @throws Exception
	 */
	public void synchronize() throws Exception  {
		Collection<SongGroup> currentGroups = new TreeSet<SongGroup>(songGroups.values());
		
		Class fileTransferClass = null;
		FileTransferModule fileTransfer = null;
		try {
			fileTransferClass = ClassLoader.getSystemClassLoader().loadClass( general.get("filetransfer_class") );
			fileTransfer = (FileTransferModule)fileTransferClass.newInstance();
			
			fileTransfer.connect();
			
			if (fileTransfer.isConnected()) {
				List remoteDir = fileTransfer.getDirectory(general.get("repository_remote"));
				
				RemoteFile rf = null;
				for (Iterator i = remoteDir.iterator() ; i.hasNext() ; ) {
					rf = (RemoteFile) i.next();
					if ( rf.isDir() ) {			
						
						recurseIntoDir(fileTransfer, general.get("repository_remote"), rf.getName(), rf.getName());
						
						currentGroups.remove( songGroups.get( rf.getName() ) );
					}			
				}
				
				if (general.get("repository_remote_personal") != "")
					recurseIntoDir(fileTransfer, general.get("repository_remote_personal"), "", SAM.getBundle("sam").getString("personal_songgroupname"));
				else if (songGroups.containsKey(SAM.getBundle("sam").getString("personal_songgroupname")))
					songGroups.remove(SAM.getBundle("sam").getString("personal_songgroupname"));
						
				//Delete groups that no longer exist.
				
				File sgDir = null;
				
				for (SongGroup sg :currentGroups) {	
					sg.deleteAll();
					songGroups.remove( sg.getName() );
					sgDir = new File( general.get("repository_local")+"/"+sg.getName() );
					sgDir.delete();
				} 
				listeners.firePropertyChange("synchronize",null,"");
			} else {
				SAM.error("fileserver_could_not_connect", new Exception());
			}
			fileTransfer.disConnect();
		} catch (InstantiationException e) {
			SAM.error("bad_filetransfer_class", e);
		} catch (IllegalAccessException e) {//andere error code
			SAM.error("bad_filetransfer_class", e);
		} catch (ClassNotFoundException e) {
			SAM.error("bad_filetransfer_class", e);
		}
	
		
	}

	/**
	 * Recurse into a (remote) directory, getting the files in that directory
	 * 
	 * @param fileTransfer
	 * @param groupName
	 * @throws Exception
	 */
	private void recurseIntoDir( FileTransferModule fileTransfer, String baseDir, String dir, String groupName ) throws Exception {
		Collection currentSongs = null;
		currentSongs = songsOfGroup(groupName);
						
		List remoteDir = fileTransfer.getDirectory(baseDir+"/"+dir);
		
		RemoteFile rf = null;
		for (Iterator i = remoteDir.iterator() ; i.hasNext() ; ) {
			rf = (RemoteFile) i.next();
						if ( rf.isDir() ) {				
				recurseIntoDir( fileTransfer, baseDir+"/"+dir, rf.getName(), rf.getName() );
			} else {
				Song s = songGroups.get(groupName).getSongByFile( general.get("repository_local")+File.separator+groupName+File.separator+rf.getName() );
				if (s != null) {
					currentSongs.remove(s);
				} else {
					String local = general.get("repository_local")+File.separator+groupName+File.separator+rf.getName();
					String remote = baseDir+"/"+dir+"/"+rf.getName();
					if (knownFormat(rf.getName())) {
						fileTransfer.getFile( remote, local );
						addSong( groupName, rf.getName() );
					}
				}
			}
		}

		deleteSongs( currentSongs );
		
	}

	private boolean knownFormat(String name) {
		return name.endsWith(".mp3") || name.endsWith(".MP3");
	}


	/**
	 * Add a song to the repository in a given group. The concrete songclass (i.e. Mp3Song
	 * is determined by the file extension.
	 * 
	 * @param group
	 * @param song
	 */
	private void addSong(String group, String song) {
				Song s = null;
		SongGroup sg = null;
		if ( song.endsWith(".mp3") || song.endsWith(".MP3") )
			try {
				s = new Mp3Song( new File( general.get("repository_local")+"/"+group+"/"+song ) , nextId() );
				if (songGroups.containsKey(group)) {
					sg = songGroups.get( group );
				} else {
					sg = new SongGroup();
					sg.setName(group);
					songGroups.put(group, sg);
				}
				sg.add(s);
				songs.put( new Long( s.getId() ), s );
				listeners.firePropertyChange("addSong", null, s);
			} catch (UnsupportedAudioFileException e) {
				SAM.error("invalid_mp3", e);
			} catch (IOException e) {
				SAM.error("invalid_mp3", e);
			}
	}

	
	/**
	 * Get the songs in a given group as a new Collection (One that can be changed
	 * without changing the underlying group). If the group doesn't exist, an
	 * empty collection (HashSet) is returned.
	 *  
	 * @param dirName
	 * @return a collection of songs
	 */
	private Collection songsOfGroup(String dirName) {
		Collection<Song> currentSongs;
		if (! songGroups.containsKey( dirName )) {
						SongGroup newGroup = new SongGroup();
			newGroup.setName( dirName );
			songGroups.put( dirName, newGroup );
			
			File newDir = new File( general.get("repository_local")+"/"+dirName );
			newDir.mkdir();
			currentSongs = new HashSet<Song>();
		} else {
			currentSongs = new HashSet<Song>(songGroups.get( dirName ).getSongList());
		}
		return currentSongs;
	}

	/**
	 * Delete all the songs in a collection
	 * 
	 * @param deleteSongs
	 */
	private void deleteSongs(Collection deleteSongs) {
		Song s = null;
		for (Iterator i = deleteSongs.iterator() ; i.hasNext() ; ) {
			s = (Song) i.next();
			deleteSong (s);
		}
	}
	
	/**
	 * Remove a song from the repository
	 * 
	 * @param s
	 */
	public void deleteSong( Song s ) {
		if (s!=null) {
			s.delete();
			
			SongGroup sg = null;
			for (Iterator i2 = songGroups.values().iterator() ; i2.hasNext() ;) {
				sg = (SongGroup) i2.next();
				sg.remove( s );
			}
			songs.remove( new Long( s.getId() ) ) ;
			listeners.firePropertyChange("removeSong", null, s);
		}
	}

	
	/**
	 * @param string
	 */
	private void debug(String string) {
		if (DEBUG)
			System.out.println("Repository "+string);
	}

	/**
	 * Get the next to be used id for a song. This counter will only go up, no
	 * two songs should ever have the same id in history.
	 * 
	 * @return next unused identifier
	 */
	private long nextId() {
		long id = nextId++;
		return id;
	}
	
	
	
	/*********************** PropertyChangeSupport ************************/
	/**
	 * @param listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
	}
	/**
	 * @param propertyName
	 * @param listener
	 */
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(propertyName, listener);
	}
	/**
	 * @param listener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);
	}
	/**
	 * @param propertyName
	 * @param listener
	 */
	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(propertyName, listener);
	}
}
