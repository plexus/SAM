/*
 * Created on 15-sep-2005
 *
 */
package com.plexus.sam.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import com.plexus.sam.audio.DynamicPlaylist;

/**
 * Panel that shows the information in a dynamic playlist
 * 
 * @author plexus
 *
 */
public class DynamicPlaylistPanel extends JPanel implements PropertyChangeListener {
	private DynamicPlaylist playlist;
	private ChartPanel chartPanel;
	private JFreeChart chart;
	private DefaultPieDataset data;
	private SongInfoPanel infoPanel;
	
	/**
	 * Set the playlist to display
	 * 
	 * @param playlist
	 */
	public DynamicPlaylistPanel(DynamicPlaylist playlist) {
		this.setLayout(new BorderLayout());
		this.playlist = playlist;
		this.infoPanel = new SongInfoPanel("up_next");
		infoPanel.update(playlist.nextSong());
		this.add(infoPanel, BorderLayout.NORTH);
		playlist.addPropertyChangeListener(this);
		drawChart();
	}
	
	private void drawChart() {
		data = new DefaultPieDataset();
		DynamicPlaylist.Relevance[] values = playlist.getValues();
		for ( int i = 0; i < values.length ; i++) {
			data.setValue(values[i].getGroupname(), values[i].getValue());
		}
		chart = ChartFactory.createPieChart3D("", // chart
				// title
				data, // data
				true, // include legend
				true, false);
		PiePlot3D plot = (PiePlot3D) chart.getPlot();
		plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
		plot.setNoDataMessage("");
		plot.setCircular(false);
		plot.setLabelGap(0.02);
		plot.setForegroundAlpha(0.7F);
		plot.setLabelGenerator(new LabelGenerator());
		plot.setLegendLabelGenerator(new LabelGenerator());
		if (chartPanel != null)
			this.remove(chartPanel);
		chartPanel = new ChartPanel(chart, false);
		chartPanel.setPreferredSize(new java.awt.Dimension(600, 370));
		chartPanel.setMouseZoomable(true, false);
		this.add(chartPanel, BorderLayout.CENTER);
	}

	
	/**
	 * Listen for changes in the playlist to update the panel
	 * @param evt
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		drawChart();
		infoPanel.update(playlist.nextSong());
	}
	
	/**
	 * Class to generate the labels we use in our chart
	 * 
	 * @author plexus
	 *
	 */
	protected static class LabelGenerator implements PieSectionLabelGenerator 
	{
		/**
		 * Generate a label for a certain dataset and key
		 * @param data 
		 * @param key 
		 * @return string representation of the key
		 */
		public String generateSectionLabel(PieDataset data,Comparable key)
		{
			return (String)key;
		}
	}
}
