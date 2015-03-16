/*
 * Created on 11-sep-2005
 *
 */
package com.plexus.sam.gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.plexus.beans.JMeterBar;
import com.plexus.sam.SAM;
import com.plexus.sam.audio.Player;
import com.plexus.sam.audio.Song;

/**
 * Panel that shows information about a song : artist, title, album and possibly a bar that indicates the
 * current position.
 * 
 * @author plexus
 *
 */
class SongInfoPanel extends JPanel {
	private boolean DEBUG = false;
	private JLabel artist;
	private JLabel title;
	private JLabel album;
	private JMeterBar meter;
	private ResourceBundle i18n = SAM.getBundle("gui");
	private Player player = SAM.player;
	private Timer timer = new Timer();
	private TimerTask task;
	
	/**
	 * Number of ms between upating the meterbar
	 */
	private long update_interval = 100;
	
	/**
	 * Create a panel without a bar that indicates current songposition
	 * 
	 * @param title_key
	 */
	public SongInfoPanel(String title_key) {
		this.setBorder(new TitledBorder(new LineBorder(Color.BLACK), i18n.getString(title_key)));
		
		artist = new JLabel();
		title = new JLabel();
		album = new JLabel();
		
		this.setLayout(new GridLayout(3,1));
		this.add(artist);
		this.add(title);
		this.add(album);
		
	}
	
	/**
	 * Create songinfopanel, possible with a meterbar showing the song position
	 * 
	 * @param title_key
	 * @param time_bar
	 */
	public SongInfoPanel(String title_key, boolean time_bar) {
		this.setBorder(new TitledBorder(new LineBorder(Color.BLACK), i18n.getString(title_key)));
		
		artist = new JLabel();
		title = new JLabel();
		album = new JLabel();
		
		if (time_bar) {
	//		this.setLayout(new GridLayout(3,1));
			this.setLayout(new GridLayout(4,1));
			meter = new JMeterBar(JMeterBar.LEFT_TO_RIGHT, "", 0f);
		} else {
			this.setLayout(new GridLayout(3,1));
		}
		this.add(artist);
		this.add(title);
		this.add(album);
		this.add(meter);
		
		task = new UpdateTask(this);
		timer.schedule(new UpdateTask(this), update_interval, update_interval);
		
	}
	
	/**
	 * Update the song information
	 * @param song
	 */
	public void update(Song song) {
		if (song == null) {
			artist.setText("");
			title.setText("");
			album.setText("");
		} else {
			artist.setText(song.getAuthor());
			title.setText(song.getTitle());
			album.setText(song.getAlbum());
		}
		if (meter != null && task == null) {
			synchronized (meter) {
				meter.clear();
				meter.addColorValuePair(Color.RED, SAM.player.getLength());
			}
			task = new UpdateTask(this);
			
			timer.schedule(task, update_interval, update_interval);
			
			
			
		}
	}
	
	public void updateMeter() {
		if (meter != null) {
			if (player.getPosition() == 0) {
				meter.setPercentage(0);
				debug("meter.setPercentage(0)");
			}
			else {
				meter.setMaximum(player.getLength());
				meter.setWaarde(player.getPosition());
				meter.clear();
				meter.addColorValuePair(Color.BLUE, 0);
				meter.addColorValuePair(Color.RED, SAM.player.getLength());
				//meter.setPercentage(player.getLength()/player.getPosition());
				debug("meter.setPercentage("+player.getLength()/player.getPosition()+")");
			}
			
			if (!player.isPlaying() && task != null) {
				task.cancel();
				task = null;
				timer.purge();
			}
		}
	}
	
	private class UpdateTask extends TimerTask {
		private SongInfoPanel panel;

		public UpdateTask(SongInfoPanel panel) {
			this.panel = panel;
		}
		
		@Override
		public synchronized void run() {
			panel.updateMeter();
		}
	}
	
	public void debug(String msg) {
		if (DEBUG )
			System.out.println("SongInfoPanel "+msg);
	}
}