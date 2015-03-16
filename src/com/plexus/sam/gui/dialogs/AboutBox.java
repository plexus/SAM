package com.plexus.sam.gui.dialogs;

import java.awt.GridLayout;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.plexus.sam.SAM;
import com.plexus.sam.config.Configuration;
public class AboutBox extends JDialog {
	String version = Configuration.getConfigGroup("general").get("version");
	String text = "SAM Automatic Musicplayer v"+version+"/n"+
					"Designed and created by Arne Brasseur /n"+
					"for info and bugreports : Arne.Brasseur@gmail.com ./n/n"+
					"This program is copyrighted intellectual property of the Author /n"+
					"All multiplication, distribution or adaptation without explicit /n"+
					"written permisson is strictly prohibited.";
	
	public AboutBox() {
		this.setLayout(new GridLayout(text.split("/n").length, 1));
		this.setSize(450,190);
		this.setResizable(false);
		this.setLocation((SAM.gui.getWidth()-450)/2 , (SAM.gui.getHeight()-190)/2);
		for (String s : text.split("/n")) {
			JLabel about = new JLabel(s);
			about.setHorizontalTextPosition(SwingConstants.CENTER);
			about.setVerticalTextPosition(SwingConstants.CENTER);
			this.add(about);
		}
		
	}
}
