/*
 * Created on 28-aug-2005
 *
 */
package com.plexus.sam.audio;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.tritonus.share.sampled.file.TAudioFileFormat;

import com.plexus.sam.SAM;


/**
 * Implementation of the Song interface for mp3's.
 * 
 * @author plexus
 */
public class Mp3Song implements Song {
	private long id;
	private File songFile;
	private Map<String, Object> properties;
	private ResourceBundle i18n = SAM.getBundle( "sam" );
	private AudioInputStream pcmStream;
	
	private boolean DEBUG = false;
	private static String[] genre = new String[148];
	
	static {
		genre[0] = "Blues";
		genre[1] = "Classic Rock";
		genre[2] = "Country";
		genre[3] = "Dance";
		genre[4] = "Disco";
		genre[5] = "Funk";
		genre[6] = "Grunge";
		genre[7] = "Hip-Hop";
		genre[8] = "Jazz";
		genre[9] = "Metal";
		genre[10] = "New Age";
		genre[11] = "Oldies";
		genre[12] = "Other";
		genre[13] = "Pop";
		genre[14] = "R&B";
		genre[15] = "Rap";
		genre[16] = "Reggae";
		genre[17] = "Rock";
		genre[18] = "Techno";
		genre[19] = "Industrial";
		genre[20] = "Alternative";
		genre[21] = "Ska";
		genre[22] = "Death Metal";
		genre[23] = "Pranks";
		genre[24] = "Soundtrack";
		genre[25] = "Euro-Techno";
		genre[26] = "Ambient";
		genre[27] = "Trip-Hop";
		genre[28] = "Vocal";
		genre[29] = "Jazz+Funk";
		genre[30] = "Fusion";
		genre[31] = "Trance";
		genre[32] = "Classical";
		genre[33] = "Instrumental";
		genre[34] = "Acid";
		genre[35] = "House";
		genre[36] = "Game";
		genre[37] = "Sound Clip";
		genre[38] = "Gospel";
		genre[39] = "Noise";
		genre[40] = "Alternative Rock";
		genre[41] = "Bass";
		genre[42] = "Soul";
		genre[43] = "Punk";
		genre[44] = "Space";
		genre[45] = "Meditative";
		genre[46] = "Instrumental Pop";
		genre[47] = "Instrumental Rock";
		genre[48] = "Ethnic";
		genre[49] = "Gothic";
		genre[50] = "Darkwave";
		genre[51] = "Techno-Industrial";
		genre[52] = "Electronic";
		genre[53] = "Pop-Folk";
		genre[54] = "Eurodance";
		genre[55] = "Dream";
		genre[56] = "Southern Rock";
		genre[57] = "Comedy";
		genre[58] = "Cult";
		genre[59] = "Gangsta";
		genre[60] = "Top 40";
		genre[61] = "Christian Rap";
		genre[62] = "Pop/Funk";
		genre[63] = "Jungle";
		genre[64] = "Native US";
		genre[65] = "Cabaret";
		genre[66] = "New Wave";
		genre[67] = "Psychadelic";
		genre[68] = "Rave";
		genre[69] = "Showtunes";
		genre[70] = "Trailer";
		genre[71] = "Lo-Fi";
		genre[72] = "Tribal";
		genre[73] = "Acid Punk";
		genre[74] = "Acid Jazz";
		genre[75] = "Polka";
		genre[76] = "Retro";
		genre[77] = "Musical";
		genre[78] = "Rock & Roll";
		genre[79] = "Hard Rock";
		genre[80] = "Folk";
		genre[81] = "Folk-Rock";
		genre[82] = "National Folk";
		genre[83] = "Swing";
		genre[84] = "Fast Fusion";
		genre[85] = "Bebob";
		genre[86] = "Latin";
		genre[87] = "Revival";
		genre[88] = "Celtic";
		genre[89] = "Bluegrass";
		genre[90] = "Avantgarde";
		genre[91] = "Gothic Rock";
		genre[92] = "Progressive Rock";
		genre[93] = "Psychedelic Rock";
		genre[94] = "Symphonic Rock";
		genre[95] = "Slow Rock";
		genre[96] = "Big Band";
		genre[97] = "Chorus";
		genre[98] = "Easy Listening";
		genre[99] = "Acoustic";
		genre[100] = "Humour";
		genre[101] = "Speech";
		genre[102] = "Chanson";
		genre[103] = "Opera";
		genre[104] = "Chamber Music";
		genre[105] = "Sonata";
		genre[106] = "Symphony";
		genre[107] = "Booty Bass";
		genre[108] = "Primus";
		genre[109] = "Porn Groove";
		genre[110] = "Satire";
		genre[111] = "Slow Jam";
		genre[112] = "Club";
		genre[113] = "Tango";
		genre[114] = "Samba";
		genre[115] = "Folklore";
		genre[116] = "Ballad";
		genre[117] = "Power Ballad";
		genre[118] = "Rhytmic Soul";
		genre[119] = "Freestyle";
		genre[120] = "Duet";
		genre[121] = "Punk Rock";
		genre[122] = "Drum Solo";
		genre[123] = "Acapella";
		genre[124] = "Euro-House";
		genre[125] = "Dance Hall";
		genre[126] = "Goa";
		genre[127] = "Drum & Bass";
		genre[128] = "Club-House";
		genre[129] = "Hardcore";
		genre[130] = "Terror";
		genre[131] = "Indie";
		genre[132] = "BritPop";
		genre[133] = "Negerpunk";
		genre[134] = "Polsk Punk";
		genre[135] = "Beat";
		genre[136] = "Christian Gangsta Rap";
		genre[137] = "Heavy Metal";
		genre[138] = "Black Metal";
		genre[139] = "Crossover";
		genre[140] = "Contemporary Christian";		
		genre[141] = "Christian Rock"		;
		genre[142] = "Merengue"		;
		genre[143] = "Salsa";
		genre[144] = "Trash Metal";
		genre[145] = "Anime";
		genre[146] = "Jpop";
		genre[147] = "Synthpop"; 
	}
	
	/************************ constructors **************************/
	/**
	 * Default constructor for XMLEnconder
	 *
	 */
	public Mp3Song() {
		debug("new song default constructor");
	}
	
	/**
	 * Construct a mp3 song from the corresponding type. Each song
	 * in the repository should have a unique id.
	 * 
	 * @param f
	 * @param id
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 */
	public Mp3Song(File f, long id) throws UnsupportedAudioFileException, IOException {
		debug("new song "+id+" "+f);
		assert f.exists() : f.getAbsolutePath();
		this.songFile = f;
		this.id = id;
		initProperties();
	}
	

	/********************* private methods ***********************
	/**
	 * Get the mp3 properties from the songFile
	 * 
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 */
	private void initProperties() throws UnsupportedAudioFileException, IOException {
		debug("initProperties");
		if (songFile != null) {
			AudioFileFormat baseFileFormat = AudioSystem.getAudioFileFormat(songFile);
			
			//TAudioFileFormat properties
			if ( baseFileFormat instanceof TAudioFileFormat ) {
			    properties = ( (TAudioFileFormat)baseFileFormat ).properties();
			    debug( "properties from TAudioFileFormat "+properties);
			} else {
				properties = new HashMap<String, Object>();
				debug("propeties = new HashMap(), no instance of TAudioFileFormat");
			}
		} else {
			debug("songFile == null");
		}
	}

	/********************* getters / setters **********************/
	/**
	 * Get the unique id of this song. The id is used to make songs
	 * persistent.
	 * 
	 * @see com.plexus.sam.audio.Song#getId()
	 */
	public long getId() {		
		return id;
	}

	
	/**
	 * Set the unique id of this song. The id is used to make songs
	 * persistent.
	 * 
	 * @see com.plexus.sam.audio.Song#setId(long)
	 */
	public void setId(long id) {
		this.id = id;
	}
	
	/**
	 * Get the path tot the file this Song represents.
	 * 
	 * @see com.plexus.sam.audio.Song#getFile()
	 */
	public String getFile() {
		
		if(songFile != null) {
			return songFile.getAbsolutePath();
		}
		return null;
		
	}
	
	/**
	 * Set the file of this song.
	 * 
	 * @param f
	 */
	public synchronized void setFile(String f) {
		songFile = new File(f);
		assert songFile.exists() : songFile.getAbsolutePath();
		if (songFile.exists()) {
			try {
				initProperties();
			} catch (UnsupportedAudioFileException e) {
				SAM.error("unsupported_audio_file", f, e);
				SAM.repos.deleteSong(this);
			} catch (IOException e) {
				SAM.error("couldnt_read_mp3_id3_tag", f, e);
				SAM.repos.deleteSong(this);
			}
		}
		else
			SAM.repos.deleteSong(this);
			
	}
	

	
	/**
	 * Get the title form the mp3-properties.
	 * 
	 * @see com.plexus.sam.audio.Song#getTitle()
	 */
	public String getTitle() {
		if ( properties != null && properties.containsKey( "title" ) )
			return (String)properties.get( "title");
		return i18n.getString( "unknown_title" );
	}

	/**
	 * Get the author form the mp3-properties.
	 * 
	 * @see com.plexus.sam.audio.Song#getAuthor()
	 */
	public String getAuthor() {
		if ( properties != null && properties.containsKey( "author" ) )
			return (String)properties.get( "author");
		return i18n.getString( "unknown_author" );
		
	}

	/**
	 * Get the album form the mp3-properties.
	 * 
	 * @see com.plexus.sam.audio.Song#getAlbum()
	 */
	public String getAlbum() {
		if ( properties != null &&  properties.containsKey( "album" ) )
			return (String)properties.get( "album");
		return i18n.getString( "unknown_album" );
	}

	/**
	 * Get the track number form the mp3-properties.
	 * 
	 * @see com.plexus.sam.audio.Song#getTrack()
	 */
	public int getTrack() {
		try {
			if ( properties != null &&  properties.containsKey( "mp3.id3tag.track" ) )
				return Integer.parseInt( (String)properties.get( "mp3.id3tag.track" ) );
			return 0;
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	/**
	 * Get the genre form the mp3-properties.
	 * 
	 * @see com.plexus.sam.audio.Song#getGenre()
	 */
	public String getGenre() {
		if (  properties != null &&  properties.containsKey( "mp3.id3tag.genre" ) ) {
			String g = (String)properties.get( "mp3.id3tag.genre");
			try {
				if (g.charAt(0) == '(' && g.charAt(g.length()-1) == ')')
					g=g.substring(1, g.length()-1);
				int i =Integer.parseInt(g);
				g = genre[i];
			} catch (Throwable t) {}
			return g;
		}
		return i18n.getString( "unknown_genre" );
	}

	/**
	 * Get the year form the mp3-properties.
	 * 
	 * @see com.plexus.sam.audio.Song#getYear()
	 */
	public String getYear() {
		if ( properties != null &&  properties.containsKey( "date" ) )
			return (String)properties.get( "date");
		return "";
	}

	/**
	 * Get the length of this song in ms.
	 * 
	 * @see com.plexus.sam.audio.Song#getLength()
	 */
	public long getLength() {		
		if ( properties != null &&  properties.containsKey( "duration" ) )
			return ( (Long)properties.get( "duration" ) ).longValue()/1000;
		return Math.round( getAudioStream().getFrameLength() / getAudioStream().getFormat().getFrameRate() );
	}

	



	/**
	 * Return an AudioInputStream to play this file.
	 * If something goes wrong (Audio file not supported, or IO error)
	 * null is returned.
	 * 
	 * @see com.plexus.sam.audio.Song#getAudioStream()
	 * @return stream or null if exception is raised
	 */
	public synchronized AudioInputStream getAudioStream() {
		//debug("getAudioInputStream");
		if (pcmStream != null)
			return pcmStream;
		try {
			AudioInputStream inStream = AudioSystem.getAudioInputStream(songFile);
			AudioFormat baseFormat = inStream.getFormat ();
			AudioFormat targetFormat = new AudioFormat (
					AudioFormat.Encoding.PCM_SIGNED,
					baseFormat.getSampleRate(),
					16,
					baseFormat.getChannels(),
					baseFormat.getChannels() * 2,
					baseFormat.getSampleRate(),
					false
			);
		
			pcmStream = AudioSystem.getAudioInputStream(targetFormat, inStream);
			return pcmStream;
		} catch (UnsupportedAudioFileException e) {
			SAM.error("Unsupported_audio_format", songFile.getAbsolutePath(), e);
			return null;
		} catch (IOException e) {
			debug("IOError : mp3");
			return null;
		}

	}

	public synchronized void closeStream() {
		try {
			if (pcmStream != null)
				pcmStream.close();
		} catch (IOException e) {
		}
		pcmStream = null;
	}
	
	/**
	 * Simple string representation.
	 * 
	 * @return 'Mp3Song <some/file/name> id=<numeric-id>'
	 */
	@Override
	public String toString() {
		return "Mp3Song "+songFile + " id="+id;
	}
	
	/**
	 * Delete the file this song is associated with.
	 * 
	 * @return true if the file is succesfully deleted.
	 */
	public boolean delete() {
		if (songFile.exists())
			return songFile.delete();
		return false;
	}
	
	/**
	 * Ouput a debug message (if DEBUG is true). 
	 * @param string
	 */
	private void debug(String string) {
		if (DEBUG)
			System.out.println("Mp3Song ["+this.id+"] "+string);
	}
	
	/**
	 * Reinitialise the audioinputstream, so it can start playing from the 
	 * beginning again.
	 * 
	 * @return the AudioInputStream for this song, located at the start
	 */
	public synchronized AudioInputStream reinitialiseStream() {
		if (pcmStream == null)
			return getAudioStream();
		
		try {
			pcmStream.close();
		} catch (IOException e) {
			debug("couldn't reinitialise stream : IOException");
		}
		pcmStream = null;
		return getAudioStream();
		
	}
	
	/**
	 * Implemtation of object.
	 * 
	 * @param s object to compare with
	 * @return true if the object is instanceof Mp3Song and equal to this object
	 */
	@Override
	public boolean equals(Object s) {
		if (s instanceof Mp3Song)
			return this.id == ((Mp3Song)s).id;
		return false;
	}
}
