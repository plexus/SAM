/*
 * Created on 27-aug-2005
 *
 */
package com.plexus.sam;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import com.plexus.sam.audio.PlaylistSet;
import com.plexus.sam.audio.Player;
import com.plexus.sam.audio.Repository;
import com.plexus.sam.comm.SerialPortConnection;
import com.plexus.sam.config.ConfigGroup;
import com.plexus.sam.config.Configuration;
import com.plexus.sam.config.StaticConfig;
import com.plexus.sam.event.RepositorySynchronizer;
import com.plexus.sam.gui.SAMgui;
import com.plexus.sam.gui.dialogs.AboutBox;

/**
 * @author plexus
 *
 */
public class SAM {
	/**
	 * The repository of songs
	 */
	public static Repository repos;
	
	/**
	 * The class that handles the synchronization of the repository
	 */
	public static RepositorySynchronizer reposSync;
	
	/**
	 * The set of playlists
	 */
	public static PlaylistSet playlists;
	
	/**
	 * The audio player
	 */
	public static Player player;
	
	/**
	 * The gui
	 */
	public static SAMgui gui;
	
	public static SerialPortConnection sConn;
	
	/**
	 * The general configuration
	 */
	//private ConfigGroup generalConfig = Configuration.getConfigGroup( "general" );
	
	/**
	 * The different resourceBundles used by the app
	 */
	private static Map<String,ResourceBundle> resourceBundles = new HashMap<String,ResourceBundle>();
	
	/**
	 * Default constructur
	 *
	 */
	private SAM() {
		try {
			//i18n
			loadLocale();
			
			//repository
			repos = new Repository();
			repos.load();
			
			//playlists
			playlists = new PlaylistSet();
			playlists.load();
			
			//keep the repository in sync with the remote repository
			reposSync = new RepositorySynchronizer( repos );
			
			//player
			player = new Player();
			
			gui = SAMgui.singleInstance();
			Dimension d = new Dimension();
			ConfigGroup guiconfig = Configuration.getConfigGroup("gui");
			try {
				d.height = Integer.parseInt(guiconfig.get("mainwindow_height"));
				d.width = Integer.parseInt(guiconfig.get("mainwindow_width"));
			} catch(NumberFormatException e) {
				d.height = 600;
				d.width = 800;
			}
			
			Point p = new Point();
			try {
				p.x = Integer.parseInt(guiconfig.get("mainwindow_location_x"));
				p.y = Integer.parseInt(guiconfig.get("mainwindow_location_y"));
			} catch(NumberFormatException e) {
				p.x = p.y = 0;
			}
			gui.setSize(d);
			gui.setLocation(p);
			
			gui.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
			gui.addWindowListener(new GuiListener());
			gui.setVisible(true);
			
			AboutBox b = new AboutBox();
			b.setVisible(true);
		} catch (Throwable t) {
			fatal(t.toString(), t);
		}
	}

	/**
	 * @param t
	 */
	public static void fatal(String msg, Throwable t) {
		SAM.error("unrecoverabl_error", msg, t);
		(new GuiListener()).windowClosing(null);
	}

	/**
	 * Load the locale settings from the configuration
	 */
	public static void loadLocale() {
		final ConfigGroup i18nConfig = Configuration.getConfigGroup( "i18n" );
		if (i18nConfig.get( "set_locale" ).toLowerCase().equals( "true" )) {
			Locale.setDefault( 
					new Locale( i18nConfig.get( "language_code" ), i18nConfig.get( "country_code" ) ) 
					);
			
		}
	}
	
	/**
	 * The SAM executable code
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new SAM();
	}
	
	/**
	 * @deprecated
	 * @param c
	 * @param e
	 * @param msg
	 */
	public static void error( Class c, Exception e, String msg ) {
		error("Unknown error in ", c.getCanonicalName()+"/n/n/"+msg, e);
	}
	
	/**
	 * Show an error dialog and write something to the error log
	 * 
	 * @param e
	 * @param errorCode
	 */
	public static void error(String errorCode, Exception e) {
		SAM.error(errorCode, (String) null, e);
	}
	
	public static void error(String errorCode, String msg,Throwable e) {
		JOptionPane.showMessageDialog(
				SAM.gui, 
				getBundle("error").getString(errorCode)+"\n\n"+(msg == null ? "" : msg+"\n\n")+
				getBundle("error").getString("general_error")+" "+StaticConfig.get("error_log"),
				getBundle("error").getString("general_error_title"),
				JOptionPane.ERROR_MESSAGE);
		try {
			OutputStream os = new FileOutputStream(StaticConfig.get("error_log"), true);
			PrintWriter fw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(os)), false);
			fw.println("----------------------------------------------------------------");
			fw.println(e.getClass().getCanonicalName());
			fw.println(getBundle("error").getString(errorCode));
			if (msg != null)
				fw.println("message : "+msg);
			else
				fw.println("message : "+e.getMessage());
			fw.println("Stack trace:");
			fw.println(Calendar.getInstance().getTime());
			for (StackTraceElement ste : e.getStackTrace())
				fw.println(ste.toString());
			fw.flush();
			fw.close();
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(
					SAM.gui, 
					getBundle("error").getString("unable_to_write_to_error_log")+"\n\n"+
					e1.getMessage(),
					getBundle("error").getString("general_error_title"),
					JOptionPane.ERROR_MESSAGE);
		}
		
	}
	
	/**
	 * Dynamically load resourcebundles from the i18n package and return a reference.
	 * If no bundle is found for the current locale, the default english bundle is 
	 * returned. If the english bundle can't be loaded, we print a message, do the
	 * stack trace and exit with error code -1.
	 * 
	 * @param name of the bundle
	 * @return the bundle
	 */
	public static ResourceBundle getBundle( String name ) {
		try {
			if (! resourceBundles.containsKey(name))
				resourceBundles.put(name, ResourceBundle.getBundle(StaticConfig.get("i18n_package")+"."+name));
		} catch (MissingResourceException e) {
			try {
				resourceBundles.put(name, ResourceBundle.getBundle(StaticConfig.get("i18n_package")+"."+name, Locale.ENGLISH));
			} catch (MissingResourceException e1) {
				System.err.println("Default (English) ResourceBundle '"+name+"' not found. Aborting.");
				e1.printStackTrace();
				System.exit(-1);
			}
		}
		return resourceBundles.get(name);
	}
	
	/**
	 * 
	 * @param p
	 */
	public static void setPlayer(Player p) {
		player = p;
	}
	
	/**
	 * 
	 * @author plexus
	 *
	 */
	public static class GuiListener implements WindowListener {

		/**
		 * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
		 */
		public void windowOpened(WindowEvent e) {
			//
		}

		/**
		 * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
		 */
		public void windowClosing(WindowEvent e) {
			int answer;
			if (e != null)
				answer = JOptionPane.showConfirmDialog(gui, SAM.getBundle("gui").getString("confirm_close"));
			else
				answer = JOptionPane.OK_OPTION;
			if (answer==JOptionPane.OK_OPTION) {
				playlists.save();
				ConfigGroup configGroup = Configuration.getConfigGroup("gui");
				configGroup.set("mainwindow_width", ""+gui.getWidth());
				configGroup.set("mainwindow_height", ""+gui.getHeight());
				configGroup.set("mainwindow_location_x", ""+gui.getLocation().x);
				configGroup.set("mainwindow_location_y", ""+gui.getLocation().y);
				configGroup.set("playerpanel_dividerlocation", ""+gui.getPlayerSplitterPosition());
				configGroup.set("playlistpanel_dividerlocation", ""+gui.getPlaylistSplitterPosition());
				configGroup.set("triggerPanel_dividerlocation", ""+gui.getTriggerSplitterPosition());
				configGroup.set("last_playlist", ""+player.getPlaylist());
				configGroup.set("volume", ""+player.getVolume());
				Configuration.save();
				if (sConn != null && sConn.isOpen())
					sConn.closeConnection();
				System.exit(0);
			}
			
		}

		/**
		 * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
		 */
		public void windowClosed(WindowEvent e) {
			//
		}

		/**
		 * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
		 */
		public void windowIconified(WindowEvent e) {
			//
		}

		/**
		 * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
		 */
		public void windowDeiconified(WindowEvent e) {
			//
		}

		/**
		 * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
		 */
		public void windowActivated(WindowEvent e) {
			//
		}

		/**
		 * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
		 */
		public void windowDeactivated(WindowEvent e) {
			//
		}
	}
}
