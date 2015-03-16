/*
 * Created on 11-okt-2005
 *
 */
package com.plexus.sam.comm;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineEvent.Type;

import com.plexus.sam.SAM;
import com.plexus.sam.audio.Player;
import com.plexus.sam.audio.Song;
import com.plexus.sam.audio.SongGroup;
import com.plexus.sam.audio.SongStream;
import com.plexus.sam.config.ConfigGroup;
import com.plexus.sam.config.Configuration;
import com.plexus.sam.config.StaticConfig;
import com.plexus.util.Hex;

public class TriggerModel implements ByteListener, LineListener, PropertyChangeListener {
	private boolean DEBUG = false;
	private List<Trigger> triggers;
	private StringBuffer buffer;
	private Player player;
	private long fadeoutmillis;
	private double fadeout_volume;
	private double oldVolume;
	private double volume;
	private SongStream stream;
	private List<TriggerListener> listeners;
	private SerialPortConnection sConnection;
	
	public TriggerModel(Player player) {
		debug("new TriggerModel");
		buffer = new StringBuffer();
		triggers = new ArrayList<Trigger>();
		this.player = player;
		listeners = new LinkedList<TriggerListener>();
		
		ConfigGroup config = Configuration.getConfigGroup("triggers");
		try {
			fadeoutmillis = Long.parseLong(config.get("fadeout_millis"));
		} catch (Exception e) {
			fadeoutmillis = 150;
		}
		
		try {
			fadeout_volume = Double.parseDouble(config.get("fadeout_volume"));
		} catch (Exception e) {
			fadeout_volume = 250;
		}
		
		try {
			volume = Double.parseDouble(config.get("volume"));
		} catch (Exception e) {
			volume = 800;
		}
		
		load();
		
		config.addChangeListener(this);
	}
	
	public void load() {
		XMLDecoder d;
		try {
			d = new XMLDecoder(
					new BufferedInputStream(
							new FileInputStream( StaticConfig.get("trigger-path") )));
					
			try {
				while (true) 
					triggers.add((Trigger)d.readObject());
			} catch(ArrayIndexOutOfBoundsException e) {
				//End of XML file
				d.close();
			}
		} catch (Exception e) {//
		}
	}
	
	public void save() {
		debug("save called");
		XMLEncoder e;
		try {
			e = new XMLEncoder(
			        new BufferedOutputStream(
			            new FileOutputStream( StaticConfig.get("trigger-path") )));
			try {
				for (Trigger t :triggers)
					e.writeObject(t);
				
			} catch (Exception ex) {//
			} finally {
				e.close();
			}
		} catch (Exception ex) {//
		}
	}
	
	public synchronized void byteReceived(byte b) {
		debug("byteReceived "+Hex.byteToHex((char)b));
		buffer.append((char)b);
		debug("byteReceived >> buffer is "+Hex.stringToHex(buffer.toString()));
		int longest = 0;
		for (Trigger t : triggers) {
			if (t.trigger.length() > longest)
				longest = t.trigger.length();
			if (buffer.length() > 0 && Hex.stringToHex(buffer.toString()).endsWith(t.getTriggerHex())) {
				debug ("byteReceived >> triggering "+t);
				buffer = new StringBuffer();
				triggerSong(t.song);
				fireTriggerFired(t);
			} else {
				debug ("byteReceived >> skipping "+t);
			}
		}
		if (buffer.length() > longest) {
			buffer.delete(0, buffer.length()-longest);
			debug ("byteReceived >> buffer trimmed "+Hex.stringToHex(buffer.toString()));
		}
	}
	
	/**
	 * 
	 * @param song using null will stop the last stream started
	 */
	private void triggerSong(Song song) {
		debug("triggersong "+song);
		
		
			if (song == null && stream !=null) {
				stream.stop();//this will become a fadeout method
				debug("triggersong >> stopping stream "+stream.getSong());
			} else if (song != null){
				if (stream != null) {
					synchronized (stream) { 
						if(stream.getOutputLine()!= null && stream.getOutputLine().isActive()) { 
							stream.stop();
							debug("triggersong >> previous stream was still active : "+stream.getSong());
						} else
							oldVolume = player.getVolume();
					}
				} else
					oldVolume = player.getVolume();
					
				debug("triggersong >> oldVolume "+oldVolume+" targetVolume "+fadeout_volume);
				
				player.fadeTo(fadeout_volume, fadeoutmillis);
				
				stream = new SongStream();
				synchronized (stream) {
					stream.setMedia(song);
					stream.initLine();
					stream.setVolume(volume/1000);
					if (stream.getOutputLine() != null) {
						stream.getOutputLine().addLineListener(this);
						stream.start();
					} else {
						player.fadeTo(oldVolume, fadeoutmillis);
					}
				}
				
				
				
				debug("triggersong >> stream started : "+stream.getSong());
			}
		
	}

	public static class Trigger {
		public String trigger;
		public Song song;
		
		public Trigger() {
			trigger = "";
		}
		
		public Trigger(String t, Song s) {
			trigger = t;
			song = s;
		}

		public long getSong() {
			if (song == null)
				return 0;
			return song.getId();
		}
		
		public void setSong(long id) {
			if (id == 0)
				song = null;
			else
				song = SAM.repos.getSong(id);
		}
		
		public String getTrigger() {
			return trigger;
		}
		
		public String getTriggerHex() {
			return Hex.stringToHex(trigger);
		}
		
		public void setTriggerHex(String h) {
			trigger = Hex.hexToString(h);
		}
		
		@Override
		public String toString() {
			String songString = (song == null ? "stop" : song.getAuthor()+"-"+song.getTitle());
			return "Trigger{"+getTriggerHex()+","+songString+"}";
		}
	}

	public void update(LineEvent event) {
		debug (" lineEvent");
		synchronized (stream) {
			if (event.getType() == LineEvent.Type.STOP && event.getLine().equals(stream.getOutputLine())) {
				debug (" lineEvent >> STOP && comes from most recently started stream");
				player.fadeTo(oldVolume, fadeoutmillis);
			}
		}
	}

	public void addTrigger(Trigger t) {
		debug("addTrigger "+t);
		triggers.add(t);
		fireTriggerAdded(t);
		save();
	}
	
	public void removeTrigger(Trigger t) {
		debug("removeTrigger "+t);
		triggers.remove(t);
		fireTriggerRemoved(t);
		save();
	}
	
	public void addTriggerListener(TriggerListener tl) {
		debug ("addTriggerListener "+tl);
		listeners.add(tl);
	}
	
	public void removeTriggerListener(TriggerListener tl) {
		debug ("removeTriggerListener "+tl);
		listeners.remove(tl);
	}
	
	private void fireTriggerAdded(Trigger t) {
		for (TriggerListener tl : listeners)
			tl.triggerAdded(t);
	}
	private void fireTriggerRemoved(Trigger t) {
		for (TriggerListener tl : listeners)
			tl.triggerRemoved(t);
	}
	private void fireTriggerFired(Trigger t) {
		for (TriggerListener tl : listeners)
			tl.triggerFired(t);
	}

	/**
	 * @return Returns the oldVolume.
	 */
	public double getOldVolume() {
		return oldVolume;
	}

	public void clear() {
		List<Trigger> copy = new ArrayList<Trigger>(triggers);
		for (Trigger t : copy) {
			removeTrigger(t);
		}
		
	}
	
	public void activate() {
		debug("activate");
		if (sConnection == null) {
			sConnection = new SerialPortConnection();
			SAM.sConn = sConnection;
		}
		if (!sConnection.isOpen()) {
			sConnection.openConnection();
			if (sConnection.isOpen())
				sConnection.addByteListener(this);
			else
				SAM.error("trigger_activate_failed", new Exception());
		}
	}
	
	public boolean isActive() {
		return sConnection != null && sConnection.isOpen();
	}
	
	public void deActivate() {
		debug("deActivate");
		if (isActive()) {
			sConnection.removeByteListener(this);
			sConnection.closeConnection();
		}
	}
	
	public SerialPortConnection getConnection() {
		return sConnection;
	}
	
	public int triggerSize() {
		return triggers.size();
	}
	
	public Trigger getTrigger(int index) {
		return triggers.get(index);
	}
	
	private void debug(String msg) {
		if (DEBUG)
			System.out.println("TriggerModel "+msg);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("fadeout_millis")) {
			try {
				fadeoutmillis = Long.parseLong(""+evt.getNewValue());
			} catch (Exception e) {
				fadeoutmillis = 150;
			}
		} else if (evt.getPropertyName().equals("fadeout_volume")) {
			try {
				fadeout_volume = Double.parseDouble(""+evt.getNewValue());
			} catch (Exception e) {
				fadeout_volume = 250;
			}
		} else if (evt.getPropertyName().equals("volume")) {
			try {
				volume = Double.parseDouble(""+evt.getNewValue());
			} catch (Exception e) {
				volume = 800;
			}
		}
	}
}
