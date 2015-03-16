package com.plexus.sam.audio;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import com.plexus.sam.SAM;
import com.plexus.sam.config.Configuration;

/**
 * This class takes care of transferring bytes from
 * an AudioInputStream (acquired from a Song) to a
 * SourceDataLine (output line) acquired from the AudioSystem
 * default Mixer (audio device). <br />
 * In the current implementation only one SongStream can be active
 * at a time, otherwise they will write to the output in turns
 * instead of mixing the signal, and the result would not be 
 * pretty.
 * 
 * TODO: Make the audio device configurable
 * @author plexus
 */
public class SongStream implements Runnable {
	/**
	 * Debugging on/off
	 */
	private boolean DEBUG = false;
	
	/**
	 * The buffer size, I use 4096 since this is
	 * the most common size for a disk block
	 */
	public static final int EXTERNAL_BUFFER_SIZE = 4096;
	
	/**
	 * The audio ouput
	 */
	private SourceDataLine outputLine;
	
	/**
	 * The song we're playing
	 */
	private Song song;
	
	private AudioFormat songFormat;
	/**
	 * Volume control
	 */
	private FloatControl volume;
	
	/**
	 * When no volume is available we have to do our own gain
	 */
	private double gain;
	
	/**
	 * Pan control
	 */
	//private FloatControl pan;

	/**
	 * Total number of bytes written, so we know were we are
	 * in the song
	 */
	private long bytesWritten = 1;
	
	/**
	 * Is the Thread running?
	 */
	private boolean running = false;

	/**
	 * Our thread
	 */
	private Thread myThread;

	/**
	 * Is the stream on pause
	 */
	private boolean paused = false;
	
	/**
	 * Player to notify when we're done
	 */
	private Player player;

	private boolean outputInitFailed = false;
	
	private Compressor compressor;
	
	/**
	 * Constructor needs a player object to notify when
	 * we're done
	 * 
	 * @param player to notify
	 */
	public SongStream(Player player) {
		this.player = player;
		compressor = new Compressor();
	}
	
	/**
	 * Constructor for testing purposes
	 *
	 */
	public SongStream() {
		this(null);
	}
	
	/**
	 * Set the song this songStream will play
	 * 
	 * @param s
	 */
	public void setMedia(Song s) {
		song = s;
		debug("setMedia "+s);
	}
	
	

	/**
	 * Initialise the output line
	 *
	 */
	public void initLine() {
		debug("--------------initLine");
		if (outputLine == null) {
			bytesWritten = EXTERNAL_BUFFER_SIZE;
			try {
				if ( song != null && song.getAudioStream() != null) {
					if (outputLine == null) {
						createLine();
						openLine();
						if (outputLine.isControlSupported(FloatControl.Type.MASTER_GAIN))
						{
							volume = (FloatControl) outputLine.getControl(FloatControl.Type.MASTER_GAIN);
							debug("master gain supported");
						} else if (outputLine.isControlSupported(FloatControl.Type.VOLUME)) {
							volume = (FloatControl) outputLine.getControl(FloatControl.Type.MASTER_GAIN);
							debug("volume supported");
						} else
							debug("!!!  volume not supported");
					
					} else {
						AudioFormat outputFormat = outputLine.getFormat();
						songFormat = song.getAudioStream().getFormat();
						if ( ! outputFormat.equals(songFormat) ) {
							outputLine.close();
							openLine();
						}
					}
				} else {
					if (song == null)
						debug ("song == null");
					else
						debug ("song.getAudioStream == null");
				}
			} catch (LineUnavailableException e) {
				SAM.error("init_output_failed",e);
				this.outputInitFailed = true;
			}
		}
		setVolume();
	}

	/**
	 * create the outputline
	 * 
	 * @throws LineUnavailableException
	 */
	private void createLine() throws LineUnavailableException {
		debug("createLine");
		if ( song != null && song.getAudioStream() != null) {
			DataLine.Info	info = new DataLine.Info(SourceDataLine.class, songFormat, AudioSystem.NOT_SPECIFIED);
			outputLine = (SourceDataLine) AudioSystem.getLine(info);
		} 
	}
	

	/**
	 * Open the outputline so it starts transmitting data.
	 * 
	 * @throws LineUnavailableException when the audio system is unavailable
	 */
	private void openLine() throws LineUnavailableException {
		debug("openLine");
		if ( outputLine == null || song == null )
		{
			return;
		}
		
		outputLine.open(outputLine.getFormat(), outputLine.getBufferSize());
		
	}


	/**
	 * Start playback in a new thread.
	 *
	 */
	public Thread start()
	{
		debug("start");
		
		if (!running) {
			myThread = new Thread(this);
			synchronized (myThread) {
				running = true;
				myThread.start();
				if (outputLine !=null)
					outputLine.start();
				if (paused)
					paused = false;
			}
		}
		return myThread;
	}


	/**
	 * Stop playback.
	 *
	 */
	public void stop()
	{
		debug("stop");
		if (running)
		{
			running = false;
			if (outputLine != null)
			{
				outputLine.stop();
				outputLine.flush();
				outputLine.close();
				song.closeStream();
				debug("outputLine stop/flush");
			} else {
				debug("outputLine == null");
			}

		} else {
			debug("not running");
		}
		if (paused)
			paused = false;
	}


	/**
	 * Stop the playback 
	 */
	public synchronized void pause()
	{
		debug ("pause");
		paused = true;
		running = false;
		outputLine.stop();
	}

	/**
	 * Length of the song
	 * @return length in ms
	 */
	public long getLength() {
		return song.getLength();
	}
	
	/**
	 * Get the position in the song
	 * @return position in ms
	 */
	public long getPosition() {
		if (songFormat == null && song.getAudioStream() != null) {
			songFormat = song.getAudioStream().getFormat();
		} else if (song.getAudioStream() == null)
			return 0;
		long frameSize = songFormat.getFrameSize();
		float sampleRate = songFormat.getSampleRate();
		
		return Math.round( bytesWritten / (frameSize *  (sampleRate/1000)) );
	}
	
	/**
	 * Implementation of Runnable.
	 * Note that start() will correctly initialize a new Thread,
	 * so no threads should be started outside this object.
	 */
	public void run() {
		if (outputLine == null || song.getAudioStream() == null) {
			notifyDone();
			SAM.error("bad_songfile", song.getFile(), new Exception());
			SAM.repos.deleteSong(song);
		} else
			synchronized (song.getAudioStream()) {
				debug("run");
				running = true;
				boolean do_overlap = Configuration.getConfigGroup("audio").get("do_overlap").equals("true");
				boolean callback_done = false;
				debug("do_overlap : "+do_overlap);
				long overlap = 2000;
				if (do_overlap) {
					try {
						overlap = Long.parseLong(Configuration.getConfigGroup("audio").get("overlap_ms"));
					} catch (Throwable t) {
						overlap = 2000;
					}
				}
				debug ("overlap : "+overlap+" ms");
				
				AudioInputStream inputStream = song.getAudioStream();
				int	nBytesRead = 0;
				
				byte[]	abData = new byte[EXTERNAL_BUFFER_SIZE];
				
				if (inputStream == null) {
					debug("inputstream == null");
					notifyDone();
				} else {
					while (nBytesRead != -1 && running)
					{
						synchronized (this) {
							try
							{
								nBytesRead = inputStream.read(abData, 0, abData.length);
							}
							catch (IOException e)
							{
								SAM.error("AudioInputStream_IOException", e);
							}
							if (nBytesRead >= 0)
							{
								if (volume == null) {
									char c;
									if (this.songFormat.isBigEndian()) {
										for (int i=0; i<nBytesRead; i++) {
											
										}
									}
								}
								//compressor.compress(abData, 0, nBytesRead);
								
								int	nBytesWritten = outputLine.write(abData, 0, nBytesRead);
								
								if (nBytesWritten > 0)
									bytesWritten += nBytesWritten;
								
							}
							if (do_overlap && !callback_done && (this.getLength() - this.getPosition() + 15 <= overlap || this.getLength() <= overlap) && player != null) {
								debug("doing overlap callback");
								callback_done = true;
								notifyDone();
							} 
						}
					}
					if (!paused)
						outputLine.drain();
				}
				
				if (!do_overlap && nBytesRead == -1 && player != null) {
					debug("doing done callback no overlap");
					notifyDone();
				}
				else if (!callback_done && do_overlap && nBytesRead == -1) {
					debug("doing late overlap callback");
					notifyDone();
				}
				
				if (nBytesRead == -1) {
					stop();
				}
			}
		
	}

	/**
	 * 
	 */
	private void notifyDone() {
		if (player != null)
			player.done(myThread);
	}
	
	
	
	/**
	 * is the thread running?
	 * @return true when the thread is running
	 */
	public boolean isRunning() {
		return ( myThread != null && myThread.isAlive() && running ); 
	}
	
	/**
	 * Output a debugging message
	 * 
	 * @param string
	 */
	private void debug(String string) {
		if (DEBUG )
			System.out.println("songstream "+string);
	}
	
	/**
	 * @return Returns the volume.
	 */
	public FloatControl getVolume() {
		return volume;
	}
	
	/**
	 * Set this songstreams volume according to that set in the player
	 *
	 */
	public void setVolume() {
		if (player != null) {
			double gain = (player.getVolume()/1000); //between 0 and 1
			setVolume(gain);
		}
	}
	
	public void setVolume(double gain) {
		this.gain = gain;
		if (volume != null) {
			float dB = (float)(Math.log(gain)/Math.log(10.0)*20.0);
			volume.setValue( dB );
		}
	}
	
	public boolean isPaused() {
		return paused;
	}
	
	public Song getSong() {
		return this.song;
	}
	
	public boolean isOutputInitialized() {
		return !outputInitFailed;
	}

	/**
	 * @return Returns the outputLine.
	 */
	public SourceDataLine getOutputLine() {
		return outputLine;
	}
	
}
