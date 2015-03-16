/*
 * Created on 23-sep-2005
 *
 */
package com.plexus.sam;


import java.beans.XMLEncoder;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import com.plexus.sam.gui.config.ChoiceWidget;
import com.plexus.sam.gui.config.ConfigGroupPanel;
import com.plexus.sam.gui.config.ConfigurationPanel;
import com.plexus.sam.gui.config.FileWidget;
import com.plexus.sam.gui.config.NumberWidget;
import com.plexus.sam.gui.config.TextWidget;
import com.plexus.sam.SAM;

/**
 * 
 * @author plexus
 *
 */
public class BuildConfigGui extends JFrame {
	private String path = "data/config_gui.xml";
	
	/**
	 * 
	 *
	 */
	public BuildConfigGui() {
		/**
		 * 
		 */
		ConfigurationPanel configPanel = new ConfigurationPanel();
		ConfigGroupPanel ftpPanel = new ConfigGroupPanel();
		ConfigGroupPanel localePanel = new ConfigGroupPanel();
		localePanel.setGroupname("locale");
		ftpPanel.setGroupname("ftp");
		
		ChoiceWidget cw = new ChoiceWidget();
		cw.setGroup("i18n");
		cw.setKey("set_locale");
		cw.setDescriptionKey("locale_local");
		Map<String,String> map = new HashMap<String,String>();
		map.put("true", "true");
		map.put("false", "false");
		cw.setKeyValueMap(map);
		ArrayList w1 = new ArrayList();
		w1.add(cw);
		localePanel.setWidgets(w1);
		
	
		
		TextWidget text1 = new TextWidget("ftp", "user_id", "ftp_uid");
		ArrayList w2 = new ArrayList();
		w2.add(text1);
		
		w2.add(new NumberWidget("ftp", "control_port", "ftp_control_port"));
		w2.add(new FileWidget("general", "repository_local", "general_repository_local"));
		ftpPanel.setWidgets(w2);
		
		ArrayList<ConfigGroupPanel> v = new ArrayList<ConfigGroupPanel>();
		v.add(ftpPanel);
		v.add(localePanel);
		configPanel.setGroupPanels(v);
		//configPanel.addGroupPanel(ftpPanel);
		//configPanel.addGroupPanel(localePanel);
		
		try {
			XMLEncoder enc = new XMLEncoder(new FileOutputStream(path));
			//enc.writeObject(ftpPanel);
			//enc.writeObject(localePanel);
			enc.writeObject(configPanel);
			enc.close();
			System.out.println("...");
			//XMLDecoder dec = new XMLDecoder(new FileInputStream(path));
			//this.add((Component)dec.readObject());
			//this.setVisible(true);
		} catch (FileNotFoundException e) {
			SAM.error("config_gui_filenotfound", e);
		}
		
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new BuildConfigGui();
	}

}
