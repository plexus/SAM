/*
 * Created on 6-mei-2005
 *
 */
package com.plexus.beans;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;


import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * Swing gebaseerde component voor een horizontale of verticale meter. Default is verticaal, onder naar boven.
 * De meter kan op twee manieren gebruikt worden. Ofwel stelt men een minimum en een maximum waarde in,
 * en ten slotte een huidige waarde, ofwel stelt men de huidige waarde in met een percentage. Deze
 * methoden kunnen ook door elkaar gebruikt worden.
 * 
 * @author Arne Brasseur
 */
public class JMeterBar extends JPanel {
	/********* constanten **************/
	/**
	 * Orientatie van een meter van onder naar boven 
	 */
	public static final int BOTTOM_TO_TOP = 1;
	
	/**
	 * Orientatie van een meter van boven naar onder
	 */
	public static final int TOP_TO_BOTTOM = 2;
	
	/**
	 * Orientatie van een meter van links naar rechts
	 */
	public static final int LEFT_TO_RIGHT = 3;
	
	/**
	 * Orientatie van een meter van rechts naar links
	 */
	public static final int RIGHT_TO_LEFT = 4;
	
	private int padding = 5;
	/********** javaBean properties ****/
	private float current_percentage;
	private float current_value;
	private float min = 0;
	private float max = 1;
	private String opschrift, subtext="";
	private int orientation;
	private int labelcount; //aantal labels naast de meter
    private boolean showLabels = false;
	
	/********** GUI componenten *******/
	private GridBagLayout layout;
	private JLabel tekstLabel;
	private JLabel subtekstLabel;
	private Meter meterLabel;
	
	/********* Configuratie ***********/
	 
	
	/**
	 * Constructor met gebruik van percenten. Orientatie : zie gedeclareerde constanten.
	 * 
	 * @param opschrift het opschrift van de meter
	 * @param percentage huidig ingesteld percentage
	 * @param orientatie richting waarin de meter beweegt
	 * @param aantallabels het aantal labels met opschrift naast de meter
	 */
	public JMeterBar(String opschrift, float percentage, int orientatie, int aantallabels) {
		setOpschrift(opschrift);
		setPercentage(percentage);
		setOrientatie(orientatie);
		createLayout();
		try {
			labelcount = aantallabels;
		} catch (NumberFormatException e) {
			labelcount = 5;
			System.err.println("The configured value for 'meter_numberoflabels' is not a valid integer.");
		} catch (Exception e) {
			labelcount = 5;
		}
	}
	
	
	
	/**
	 * Constructor met gebruik van percenten en 5 labels naast de meter. Orientatie : zie gedeclareerde constanten.
	 * 
	 * @param orientatie richting waarin de meter beweegt
	 * @param opschrift het opschrift van de meter
	 * @param percentage huidig ingesteld percentage
	 */
	public JMeterBar(int orientatie, String opschrift, float percentage) {
		this(opschrift, percentage,  orientatie, 5);
	}
	
	/**
	 * Constructor met gebruik van percenten en default orientatie (JMeterBar.BOTTOM_TO_TOP).
	 * 
	 * @param opschrift het opschrift van de meter
	 * @param percentage huidig ingesteld percentage
	 * @param aantallabels het aantal labels met opschrift naast de meter
	 */
	public JMeterBar(String opschrift,float percentage, int aantallabels) {		
		this(opschrift, percentage, BOTTOM_TO_TOP, aantallabels);
	}
	
	/**
	 * Constructor met gebruik van percenten en default orientatie (JMeterBar.BOTTOM_TO_TOP) 
	 * en vijf labels.
	 * 
	 * @param opschrift het opschrift van de meter
	 * @param percentage huidig ingesteld percentage
	 */
	public JMeterBar(String opschrift,float percentage) {		
		this(opschrift, percentage, 5);
	}
	
	
	/**
	 * Constructor met gebruik van minimum en maximum waarde. Orientatie : zie gedeclareerde constanten.
	 * 
	 * @param opschrift het opschrift van de meter
	 * @param min ondergrens van wat de meter kan tonen
	 * @param max bovengrens van wat de meter kan tonen
	 * @param waarde huidige waarde
	 * @param orientatie richting waarin de meter beweegt
	 * @param aantallabels aantal labels naast de meter
	 */
	public JMeterBar(String opschrift, float min, float max, float waarde, int orientatie, int aantallabels) {
		setOpschrift(opschrift);
		setMinimum(min);
		setMaximum(max);
		setWaarde(waarde);
		setOrientatie(orientatie);
		
		labelcount = aantallabels;
		
		createLayout();
	}
	

	/**
	 * Constructor met gebruik van minimum en maximum waarde. Orientatie : zie gedeclareerde constanten.
	 * 
	 * @param opschrift het opschrift van de meter
	 * @param min ondergrens van wat de meter kan tonen
	 * @param max bovengrens van wat de meter kan tonen
	 * @param waarde huidige waarde
	 * @param orientatie richting waarin de meter beweegt
	 */
	public JMeterBar(String opschrift, float min, float max, float waarde, int orientatie) {
		this(opschrift,  min, max, waarde, orientatie, 5);
	}
	/**
	 * Constructor met gebruik van minimum en maximum waarde, en default orientatie (JMeterBar.BOTTOM_TO_TOP)
	 * 
	 * @param opschrift het opschrift van de meter
	 * @param min ondergrens van wat de meter kan tonen
	 * @param max bovengrens van wat de meter kan tonen
	 * @param waarde huidige waarde
	 */
	public JMeterBar(String opschrift, float min, float max, float waarde) {
		this(opschrift, min, max, waarde, BOTTOM_TO_TOP);
	}
	
	/************** Layout ***********************/
	/**
	 * Creer de layout <br>
	 * <ul>
	 * 	<li>layoutmanager instellen (springlayout)</li>
	 * 	<li>tekstlabel en meterlabel aanmaken</li>
	 * 	<li>tekst- en meterlabel toevoegen aan het panel</li>
	 * 	<li>componentListener bij het panel registreren om bij resizen het tekstlabel centraal te houden</li>
	 * 	<li>redoLayout() oproepen om alles goed te zetten</li>
	 * </ul>
	 */
	private void createLayout() {
		layout = new GridBagLayout();
		this.setLayout(layout);
		tekstLabel = new JLabel(opschrift);
		subtekstLabel = new JLabel(subtext);
		meterLabel = new Meter(this);
		redoLayout();
	}
	
	/**
	 * (Her)doe de layout, bijvoorbeeld als de orientatie verandert.
	 */
	private void redoLayout() {
		this.removeAll();
		if (tekstLabel != null && meterLabel != null) {
			GridBagConstraints gbcM = new GridBagConstraints();
			GridBagConstraints gbcL = new GridBagConstraints();
			Insets insets = new Insets(padding, padding, padding, padding);
			Box labelBox = Box.createVerticalBox();
			labelBox.add(tekstLabel);
			labelBox.add(subtekstLabel);
			if (isAscending()) {
				this.add(labelBox);
				this.add(meterLabel);
			} else {
				this.add(meterLabel);
				this.add(labelBox);				
			}
			switch(orientation) {
				case LEFT_TO_RIGHT:
				case RIGHT_TO_LEFT:
					gbcL.weightx = 0;
					gbcL.weighty = 0;
					gbcL.insets = insets;
					gbcM.weightx = 1;
					gbcM.weighty = 1;
					gbcM.fill = GridBagConstraints.BOTH;
					gbcM.insets = insets;
					break;

				case TOP_TO_BOTTOM:					
				case BOTTOM_TO_TOP:
					gbcL.gridx=0;
					gbcM.gridx=0;
					gbcL.weightx = 0;
					gbcL.weighty = 0;
					gbcL.insets = insets;
					gbcM.weightx = 1;
					gbcM.weighty = 1;
					gbcM.fill = GridBagConstraints.BOTH;
					gbcM.insets = insets;					
					break;
					
			}
			
			layout.setConstraints(meterLabel, gbcM);
			layout.setConstraints(labelBox, gbcL);
		}
	}
	
	
	/**
	 * Stel een kleur in corresponderend met een bepaalde waarde. Voor tussenliggende waarden worden
	 * kleuren lineair geinterpoleerd.
	 * 
	 * @param c in te stellen kleur
	 * @param v corresponderende waarde
	 */
	public void addColorValuePair(Color c, float v) {
		if(meterLabel != null)
			meterLabel.addColor(c, v);
	}
	
	/************* JavaBean property accesors and mutators *************/
	/**
	 * Mimimumwaarde van de property <i>waarde</i>
	 * 
	 * @return minimum
	 */
	public float getMinimum() {
		return min;
	}
	
	/**
	 * Stel de mimimumwaarde in van de property <i>waarde</i>
	 * 
	 * @param min minimum
	 */
	public void setMinimum(float min) {
		this.min=min;
	}
	
	
	/**
	 * Maximumwaarde van de property <i>waarde</i>
	 * 
	 * @return maximum
	 */
	public float getMaximum() {
		return max;
	}
	
	/**
	 * Stel de maximumwaarde in van de property <i>waarde</i>
	 * 
	 * @param max maximum
	 */
	public void setMaximum(float max) {
		this.max=max;
	}
	
	
	/**
	 * De huidige waarde (gelegen tussen <i>minimum</i> en <i>maximum</i>, grenzen inclusief).
	 * 
	 * @return huidige waarde
	 */
	public float getWaarde() {
		return current_value;
	}
	
	/**
	 * Stel een waarde in gelegen tussen <i>minimum</i> en <i>maximum</i>, grenzen inclusief.
	 * 
	 * @param waarde huidige waarde
	 */
	public void setWaarde(float waarde) {
		this.current_value = waarde;
		if (current_value > max)
			current_value = max;
		if (current_value < min)
			current_value = min;
		this.current_percentage = (current_value - min) / (max - min);
		repaint();
	}
	
	
	/**
	 * De huidige ingestelde waarde, uitgedrukt als percentage.
	 * 
	 * @return 0 <= percentage <=1
	 */
	public float getPercentage() {
		return current_percentage;
	}
	
	/**
	 * Stel de huidige waarde in aan de hand van een percentage (tussen 0 en 1, grenzen inclusief).<br>
	 * De property <i>waarde</i> wordt automatisch aangepast, relatief ten opzichte van <i>minimum</i> en </maximum>.
	 * 
	 * @param percentage percentage van de meter dat gevuld is
	 */
	public void setPercentage(float percentage) {
		this.current_percentage = percentage;
		if (current_percentage > 1)
			current_percentage = 1;
		if (current_percentage < 0)
			current_percentage = 0;
		setWaarde(min + (max - min)*current_percentage);
	}
	
	/**
	 * Het opschrift, datgene wat de meterbar toont.
	 * 
	 * @return het huidige opschrift 
	 */
	public String getOpschrift() {
		return opschrift;
	}
	
	/**
	 * Stel het opschrift in, datgene wat de meterbar toont.
	 * 
	 * @param opschrift nieuw opschrift van de meter
	 */
	public void setOpschrift(String opschrift) {
		this.opschrift = opschrift;
		if (tekstLabel != null)
			tekstLabel.setText(opschrift);
		revalidate();
	}
	
	/**
	 * Het onderschrift, onder het eigenlijke opschrift
	 * 
	 * @return het huidig onderschrift 
	 */
	public String getOnderschrift() {
		return subtext;
	}
	
	/**
	 * Stel het onderschrift in, dat onder het opschrift komt
	 * 
	 * @param onderschrift het in te stellen onderschrift
	 */
	public void setOnderschrift(String onderschrift) {
		this.subtext = onderschrift;
		if (subtekstLabel != null)
			subtekstLabel.setText(subtext);
		revalidate();
	}
	
	
	/**
	 * Geeft de orientatie van deze meterbar. Zie gedeclareerde constanten.
	 * 
	 * @return de huidige orientatie 
	 */
	public int getOrientatie() {
		return orientation;
	}
	
	
	/**
	 * Stel de orientatie in van de meterbar. Zie gedeclareerde constanten.
	 * 
	 * @param orientatie de in te stellen orientatie
	 */
	public void setOrientatie(int orientatie) {
		if (orientatie > 0 && orientatie < 5) {
			this.orientation = orientatie;
			redoLayout();
		}
	}
	
	/**
	 * Vraag op of de labels naast de meter zichtbaar zijn
	 * 
	 * @return de showLabels property van deze JMeterBar
	 */
	public boolean getShowLabels() {
			return showLabels;
	}
		
	/**
	 * Stel in of er naast de meter labels horen te staan
	 * 
	 * @param b de toe te kennen waarde aan de showLabels property
	 */
	public void setShowLabels(boolean b) {
			this.showLabels = b;
	}
		
	
	/**
	 * Vraag op hoeveel labels er naast de meter staan
	 * 
	 * @return de labelCount property
	 */
	public int getLabelCount() {
			return labelcount;
	}
		
	/**
	 * Stel in hoeveel labels er naast de meter moeten komen te staan
	 * 
	 * @param c de labelCount property van deze JMeterBar
	 */
	public void setLabelCount(int c) {
		if (c < 2)
			this.labelcount = 2;
		else
			this.labelcount = c;
	}
	
	/**
	 * Stel het font in van deze JMeterBar, zowel van het basislabel, als van de labels naast de meter
	 * 
	 * @param f in te stellen font
	 */
	@Override
	public void setFont(Font f) {
		super.setFont(f);
		if(tekstLabel!=null)
			this.tekstLabel.setFont(f);
		if(subtekstLabel!=null)
			this.subtekstLabel.setFont(f);
		//De labels naast de meter zullen ook dit Font gebruiken via getFont
	}
	
	/**
	 * Stel de padding in tussen de deelcomponenten
	 * 
	 * @param p padding in px
	 */
	public void setPadding(int p) {
		this.padding = p;
		redoLayout();
		
	}
	/************* Hulpmethoden *******************/
	/**
	 * Is de orientatie van deze meterbar horizontaal : links naar rechts of rechts naar links
	 * 
	 * @return meter is horizontaal
	 */
	public boolean isHorizontal() {
		return ((orientation == LEFT_TO_RIGHT) || (orientation == RIGHT_TO_LEFT));
	}
	
	/**
	 * Stijgt de meter in dezelfde richting als de pixels : boven naar beneden of links naar rechts
	 * 
	 * @return meter is stijgend
	 */
	public boolean isAscending() {
		return ((orientation == TOP_TO_BOTTOM) || (orientation == LEFT_TO_RIGHT));
	}
	
	/**
	 * Remove all colorvalue mappings
	 *
	 */
	public synchronized void clear() {
		this.meterLabel.clear();
	}
	
	/************* Binnenklassen ******************/
	/**
	 * De eigenlijke metercomponent, toont de meter met de ingestelde kleuren (zie </code>addColor</code>).
	 * Heeft een property <code>showLabels</code> die bepaalt of er tekstlabels met waarden naast de meter komen te staan.
	 * 
	 *
	 */
	private class Meter extends JComponent {
		private JMeterBar parent;
	    private List<ColorValuePair> colors = new ArrayList<ColorValuePair>();
	    
		/**
		 * Creer meter met een JMeterBar als ouder.
		 * Deze ouder wordt gebruikt om de minimum, maximum en huidige waarde op te vragen.
		 * 
		 * @param parent oudercomponent
		 */
		public Meter(JMeterBar parent) {		
			this.parent = parent;
			this.setBackground(Color.LIGHT_GRAY);
			this.setOpaque(true);
		}
		
		/************** Publieke methodes *****************/
		/**
		 * Associeer een kleur met een bepaalde waarde. Kleuren tussen gegeven waarden worden geinterpoleerd.
		 * 
		 * @param c
		 * @param v
		 */
		public void addColor(Color c, float v) {
			if(colors.isEmpty())
				colors.add(new ColorValuePair(c,v));
			else {
				ListIterator<ColorValuePair> i = colors.listIterator();
				ColorValuePair cvp = i.next();
				while(i.hasNext() && cvp.getValue() < v) {					
					cvp = i.next();
				}
				if(cvp.getValue() > v)
					i.previous();
				i.add(new ColorValuePair(c,v));				
			}
		}
		
		
		/************* Interne keuken **************/
		/**
		 * Converteer een waarde (float) naar een pixel-waarde relatief in het vlak van de meter.
		 * 
		 * @param value waarde op interne schaal 
		 * @return relatieve meterpositie in pixels
		 */
		private int valueToPx(float value) {			
			float range = (parent.getMaximum() - parent.getMinimum());
			return Math.round((lengthInPx()*value)/range);							
		}
		
		
		/**
		 * Converteer een waarde van pixels naar value, int naar float, waarbij de int gelegen is in het interval
		 * [0, lengte van de meter in pixels], en de teruggeven waarde in het interval [Minimum, maximum] van het ouderobject.
		 * 
		 * @param px
		 * @return De overeenkomstige waarde met px
		 */
		private float pxToValue(int px) {
			if (px == 0)
				return parent.getMinimum();
			float range = (parent.getMaximum() - parent.getMinimum());
			return parent.getMinimum()+range*px/lengthInPx();
		}
		
		/**
		 * Bereken de kleur die een bepaalde positie in de meter krijgen moet.
		 * 
		 * @param px
		 * @return kleur
		 */
		private Color colorAtPosition(int px) {
			ColorValuePair c1,c2;
			synchronized (colors) {
				if(colors.isEmpty())
					return this.getBackground();
				
				
				ListIterator i = colors.listIterator();
				c1 = (ColorValuePair)i.next();
				
				
				if(px <= valueToPx(c1.getValue()) || (!i.hasNext()))
					return c1.getColor();
				
				
				c2 = (ColorValuePair)i.next();
				
				while(i.hasNext() && c2.getValue() < pxToValue(px)) {
					c1 = c2;
					c2 = (ColorValuePair)i.next();
				}
			}	
			if(px >= valueToPx(c2.getValue()))
				return c2.getColor();
			
			int r1 = c1.getColor().getRed();
			int g1 = c1.getColor().getGreen();
			int b1 = c1.getColor().getBlue();
			
			int r2 = c2.getColor().getRed();
			int g2 = c2.getColor().getGreen();
			int b2 = c2.getColor().getBlue();
			
			double factor1 = (pxToValue(px)-c1.getValue())/(c2.getValue()-c1.getValue());
			double factor2 = 1 - factor1;
			int r = (int)Math.round(factor2*r1+factor1*r2);
			int g = (int)Math.round(factor2*g1+factor1*g2);
			int b = (int)Math.round(factor2*b1+factor1*b2);
			
			if (r>255)
				r=255;
			if (g>255)
				g=255;
			if (b>255)
				b=255;
			
			if (r<0)
				r=0;
			if (g<0)
				g=0;
			if (b<0)
				b=0;
			
			
			return new Color(
					r,
					g,
					b					
			);			
		}
		
		/**
		 * Teken de meter
		 * 
		 * @param g grafische context
		 */
		private void paintMeter(Graphics2D g) {
			for(int i=0; i < this.lengthInPx() && pxToValue(i) < parent.getWaarde(); i++) {
				drawColoredLine(g, i);
			}
		}
		
		/**
		 * Teken een lijn op positie i met de gepaste kleur, zie <code>getColorAtPosition</code>.
		 * De richtinggevoelige code zit hier samengebracht.
		 * @param g grafische context
		 * @param i hoeveelste lijn (in pixels) in de betreffende richting
		 */
		private void drawColoredLine(Graphics2D g, int i) {
			Color c = colorAtPosition(i);
			int x1 = 0, x2 = 0, y1 = 0, y2 = 0;
			switch (parent.getOrientatie()) {
				case LEFT_TO_RIGHT:					
					y1 = 0;
					y2 = meterWidth();
					x1 = i;
					x2 = i;
					break;
				case RIGHT_TO_LEFT:
					y1 = 0;
					y2 = meterWidth();
					x1 = this.lengthInPx() - i;
					x2 = this.lengthInPx() - i;
					break;
					
				case TOP_TO_BOTTOM:					
					x1 = 0;
					x2 = meterWidth();
					y1 = i;
					y2 = i;
					break;
				case BOTTOM_TO_TOP:
					x1 = 0;
					x2 = meterWidth(); 
					y1 = this.lengthInPx() - i;
					y2 = this.lengthInPx() - i;
					break;
			}
			g.setColor(c);
			g.drawLine(x1,y1,x2,y2);
		}
		
		/**
		 * Teken de tekstlabels.
		 *  
		 * @param g grafische context
		 */
		private void paintLabels(Graphics2D g) {
			int count = parent.getLabelCount();
			float pxInterval = this.lengthInPx()/(count-1);
			float valueInterval = (parent.getMaximum()-parent.getMinimum())/(count-1);
			float pxCurr = 0;
			float valueCurr = parent.getMinimum();
			int px;
			String label;
			for(int p = 0; p < count; p++) {
				label = Integer.toString(Math.round(valueCurr));
				px = Math.round(pxCurr);
				if (p == count-1)
					px = lengthInPx()-1; 
				
				drawLabel(g, px, label);
				
				pxCurr += pxInterval;
				if(pxCurr > this.lengthInPx())
					pxCurr = this.lengthInPx();
				valueCurr +=valueInterval;
			}
				
		}
		
		/**
		 * Teken een maatstreepje en tekstlabel op de opgegeven grafische context, op positie pxCurr, met opgegeven tekst.
		 * 
		 * @param g grafische context
		 * @param pxCurr positie op de meter
		 * @param label in te stellen tekst
		 */
		private void drawLabel(Graphics2D g, int pxCurr, String label) {
			Font f = parent.getFont();
			FontRenderContext frc = g.getFontRenderContext();
			TextLayout text = new TextLayout(label, f, frc);
			Rectangle2D rect = text.getBounds();
			
			if(parent.isHorizontal()) {
				if(rect.getHeight() >= this.meterWidth()) {
					label = "...";
					text = new TextLayout(label, f, frc);
					rect = text.getBounds();
				}
			} else { 
				if(rect.getWidth() >= this.meterWidth()) {
					label = "...";
					text = new TextLayout(label, f, frc);
					rect = text.getBounds();
				}
			}
			
					
			int x1 = 0, x2 = 0, y1 = 0, y2 = 0; //lijnstuk
			float tekst_x = 0, tekst_y = 0; //tekstpositie
			switch (parent.getOrientatie()) {
				case LEFT_TO_RIGHT:
					x1 = pxCurr;
					x2 = pxCurr;
					y1 = meterWidth();
					y2 = (int)Math.round(this.getHeight() - rect.getHeight() - 2);
					
					tekst_x = (float)(pxCurr - (rect.getWidth()/2));
					if (tekst_x < 0)
						tekst_x = 0;
					if((tekst_x + rect.getWidth()) > this.getWidth())
						tekst_x = (float)(this.getWidth() - rect.getWidth() - 1);
					tekst_y = this.getHeight();
					break;
				case RIGHT_TO_LEFT:
					x1 = lengthInPx() - pxCurr - 1;
					x2 = lengthInPx() - pxCurr - 1;
					y1 = meterWidth();
					y2 = (int)Math.round(this.getHeight() - rect.getHeight() - 2);
					
					tekst_x = (float)(x1 - (rect.getWidth()/2));
					if (tekst_x < 0)
						tekst_x = 0;
					if((tekst_x + rect.getWidth()) > this.getWidth())
						tekst_x = (float)(this.getWidth() - rect.getWidth() - 1);
					tekst_y = this.getHeight();
					break;
				case TOP_TO_BOTTOM:
					x1 = meterWidth();
					x2 = (int)Math.round(this.getWidth() - rect.getWidth() - 1);
					y1 = pxCurr;
					y2 = pxCurr;
					
					tekst_x = (float)(this.getWidth() - rect.getWidth() - 2);
					tekst_y = (float)(pxCurr + (rect.getHeight()/2));
					if (tekst_y < rect.getHeight())
						tekst_y = (float)(rect.getHeight());
					if(tekst_y  > this.getHeight())
						tekst_y = this.getHeight() - 1;
					break;
				case BOTTOM_TO_TOP:
					x1 = meterWidth();
					x2 = (int)Math.round(this.getWidth() - rect.getWidth() - 2);
					y1 = lengthInPx() - pxCurr - 1;
					y2 = lengthInPx() - pxCurr - 1;
					tekst_x = (float)(this.getWidth() - rect.getWidth() - 1);
					tekst_y = (float)(this.getHeight() - pxCurr + (rect.getHeight()/2));
					if (tekst_y < rect.getHeight())
						tekst_y = (float)(rect.getHeight());
					if(tekst_y  > this.getHeight())
						tekst_y = this.getHeight() - 1;
			}
			g.setColor(Color.BLACK);
			if (x1 <= x2 && y1 <= y2)
				g.drawLine(x1,y1,x2,y2);
			
			if(parent.isHorizontal()) {
				if(rect.getHeight() < this.meterWidth()) {
					text.draw(g, tekst_x, tekst_y);
				}
			} else {
				if(rect.getWidth() < this.meterWidth()) {
					text.draw(g, tekst_x, tekst_y);
				}
			}
			
		}
		
		/**
		 * Teken deze component op de grafische context. Deze methode wordt opgeroepen door de GUI thread.
		 * 
		 * @param g grafische context
		 */
		@Override
	    protected void paintComponent(Graphics g) {
	    	super.paintComponents(g);
	    	if (isOpaque()) { //paint background
	            g.setColor(getBackground());
	            g.fillRect(0, 0, getWidth(), getHeight());
	        }
	        Graphics2D g2d = (Graphics2D)g.create();
	        
	        paintMeter(g2d);
	        if (parent.getShowLabels()) 
	        	paintLabels(g2d);
	        	        
	        g2d.setColor(Color.BLACK);
	        if(parent.isHorizontal())
	        	g2d.draw(new Rectangle(0,0,this.getWidth()-1,this.meterWidth()));
	        else
	        	g2d.draw(new Rectangle(0,0,this.meterWidth(),this.getHeight()-1));
	        g2d.dispose(); 
	    }
	
	    /**
	     * Lengte in pixels van deze meter
	     * 
	     * @return lengte van de meter
	     */
	    private int lengthInPx() {
	    	if(parent.isHorizontal()) 
				return this.getWidth();
			return this.getHeight();
	    }
	    
	    /**
	     * De breedte van de meter zelf, afhankelijk van of de labels aanstaan is dit de volledige, of de halve breedte van het label.
	     * 
	     * @return breedte van de meter in pixels
	     */
	    private int meterWidth() {
	    	if(parent.isHorizontal())
	    		return Math.round(parent.getShowLabels()?this.getHeight()/2:this.getHeight()-1);
	    	return Math.round(parent.getShowLabels()?this.getWidth()/2:this.getWidth()-1);
	    }
	    
	    public void clear() {
	    	synchronized (colors) {
	    		this.colors.clear();
	    		this.addColor(Color.BLUE, 0);
	    	}
		}
	    
	}

	/**
	 * Groepering van een kleur en een waarde
	 * 	
	 */
	private class ColorValuePair {
		private Color c;
		private float v;
		
		/**
		 * Stel kleur in en corresponderende waarde
		 * 
		 * @param c kleur
		 * @param v waarde
		 */
		public ColorValuePair(Color c, float v) {
			this.c = c;
			this.v = v;
		}
		
		/**
		 * Stel kleur in en waarde 0
		 * 
		 * @param c kleur
		 */
		public void setColor(Color c) {
			this.c = c;
		}
		
		/**
		 * Stel waarde in en kleur null
		 * @param v
		 */
		public void setValue(float v) {
			this.v = v;
			this.c = null;
		}
		
		/**
		 * @return float-waarde 
		 */
		public float getValue() {
			return v;
		}
		
		/**
		 * @return kleur
		 */
		public Color getColor() {
			return c;
		}
		
		/**
		 * @return "ColorValuePair[v:\<waarde\>,c:(\<R\>,\<G\>,\<B\>)"
		 */
		@Override
		public String toString() {
			return "ColorValuePair[v:"+v+",c:("+c.getRed()+","+c.getGreen()+","+c.getBlue()+")]";
		}
		
		
	}
}
