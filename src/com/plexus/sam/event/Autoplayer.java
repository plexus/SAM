/*
 * Created on 27-sep-2005
 *
 */
package com.plexus.sam.event;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.plexus.sam.SAM;
import com.plexus.sam.config.Configuration;
import com.plexus.sam.config.StaticConfig;
import com.plexus.util.Time;

/**
 * The autoplayer takes care of starting and stopping the player acording to certain rules.
 * This component also acts as a model, by registering an {@link RulemodelListener}, components
 * can be notified when a rule is added, changed, or removed.<br /><br />
 * {@link #addRule()} will create a new rule with the {@link AutoplayRule} default values, and
 * schedule it for execution. After this the rule can be altered directly, the changes will
 * propagate to this component wich propagates them to its listeners. The scheduled events
 * are also notified directly of changes to the AutoplayRules so they can be rescheduled if
 * necessary.
 * 
 * @author plexus
 *
 */
public class Autoplayer implements PropertyChangeListener {
	private static final boolean DEBUG = false;
	private EventScheduler scheduler;
	private SortedMap<AutoplayRule, RuleEvents> eventMap;
	private List<RulemodelListener> listenerList;
	private ResourceBundle error = SAM.getBundle("error");
	
	/**
	 * Default constructor
	 *
	 */
	public Autoplayer() {
		debug("new Autoplayer");
		this.scheduler = new EventScheduler();
		eventMap = new TreeMap<AutoplayRule, RuleEvents>();
		listenerList = new LinkedList<RulemodelListener>();
		if (Configuration.getConfigGroup("general").get("autoplayer").equals("on"))
			scheduler.start();
		
		load();
	}

	/**
	 * Add a new rule
	 *
	 */
	public void addRule() {
		debug ("new rule");
		AutoplayRule rule = new AutoplayRule();
		rule.setPlaylist(SAM.playlists.getPlaylist(0));
		addRule(rule);
	}

	/**
	 * Add a new rule
	 *
	 * @param rule the rule to add
	 */
	public void addRule(AutoplayRule rule) {
		debug ("addRule "+rule);
		RuleEvents events = new RuleEvents();
		//events.rule = rule;
		events.start = new AutoplayEvent(scheduler, rule, true);
		events.stop = new AutoplayEvent(scheduler, rule, false);
		eventMap.put(rule, events);
		scheduler.addEvent(events.start);
		scheduler.addEvent(events.stop);
		rule.addPropertyChangeListener(this);
		save();
		fireRuleAdded(rule);
	}
	
	/**
	 * Remove a rule
	 * 
	 * @param rule rule to be removed
	 */
	public void removeRule(AutoplayRule rule) {
		debug("remove rule "+rule);
		RuleEvents events = eventMap.get(rule);
		if (events != null) {
			scheduler.removeEvent(events.start);
			scheduler.removeEvent(events.stop);
			eventMap.remove(rule);
			rule.removePropertyChangeListener(this);
			save();
			fireRuleRemoved(rule);
		}
	}
	
	/**
	 * Notify a change in one of the AutoplayRules
	 * @param evt the changeevent
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() instanceof AutoplayRule) {
			AutoplayRule rule = (AutoplayRule)evt.getSource();
			save();
			fireRuleChanged( rule );
			debug ("rule has changed:"+evt.getSource());
		}
	}
	
	/**
	 * Add a listener
	 * @param l listener to add
	 */
	public void addAutoplayListener(RulemodelListener l) {
		listenerList.add(l);
	}
	
	/**
	 * remove a listener
	 * @param l listener to be removed
	 */
	public void removeAutoplayListener(RulemodelListener l) {
		listenerList.remove(l);
	}
	
	/**
	 * Notify listeners a rule was added
	 * @param rule rule that was added
	 */
	private void fireRuleAdded(EventRule rule) {
		for (RulemodelListener l : listenerList)
			l.ruleAdded(rule);
	}
	
	/**
	 * Notify listener a rule was removed
	 * 
	 * @param rule that was removed
	 */
	private void fireRuleRemoved(EventRule rule) {
		for (RulemodelListener l : listenerList)
			l.ruleRemoved(rule);
	}
	
	/**
	 * Notify listener a rule was changed
	 * 
	 * @param rule rule that was changed
	 */
	private void fireRuleChanged(EventRule rule) {
		for (RulemodelListener l : listenerList)
			l.ruleChanged(rule);
	}
	
	/**
	 * 
	 * @return the number of rules registered
	 */
	public int size() {
		return eventMap.size();
	}
	
	/**
	 * Return a rule at a specified index
	 * @param index index of the rule
	 * @return specified rule, or <code>null</code> if no such index
	 */
	public AutoplayRule getRule(int index) {
		for (AutoplayRule a : eventMap.keySet()) {
			if (index-- == 0)
				return a;
		}
		return null;
	}
	
	/**
	 * Load the autoplayrules from a file specified in the {@link com.plexus.sam.config.StaticConfig}
	 *
	 */
	public void load() {
		DefaultHandler handler = new XMLHandler(this);
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser;
		try {
			saxParser = factory.newSAXParser();
			saxParser.parse( new File(StaticConfig.get("autoplayer_path")), handler );
		} catch (Exception e) {
			System.err.println(error .getString("autoplay_load_error"));
			SAM.error("loading_autoplayrules_failed", e);
		}
        
	}
	
	/**
	 * Save the autoplayrules to a file specified in the {@link com.plexus.sam.config.StaticConfig}
	 *
	 */
	public void save() {
		try {
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element autoplayer = document.createElement("autoplayer");
			for (AutoplayRule rule : eventMap.keySet()) {
				autoplayer.appendChild( ruleNode(rule, document) );
			}
			document.appendChild(autoplayer);
			TransformerFactory tFactory =
				TransformerFactory.newInstance();
			Transformer transformer;
			
			transformer = tFactory.newTransformer();
			DOMSource source = new DOMSource( document );
			StreamResult result;
			
			result = new StreamResult( new FileOutputStream( new File( StaticConfig.get("autoplayer_path") ) ) );
			
			transformer.transform( source, result );
			
			
			result.getOutputStream().close();
			
		} catch (Exception e) {
			System.err.println(error .getString("autoplay_save_error"));
			SAM.error("saving_autoplayrules_failed", e);
		}
	
	}
	
	/**
	 * Create a w3c DOM {@link Element} {@link Node} from an autoplayrule,
	 * a {@link Document} is needed for context (factory methods).
	 * 
	 * @param rule
	 * @param doc
	 * @return element node
	 */
	private Node ruleNode(AutoplayRule rule, Document doc) {
		Element ruleNode = doc.createElement("autoplayrule");
		
		ruleNode.setAttribute("day", ""+rule.getDayofweek());
		ruleNode.setAttribute("playlist", (rule.getPlaylist() == null ? "" : rule.getPlaylist().getName()) );
		
		Element start = doc.createElement("start");
		start.setAttribute("hour", ""+rule.getStart().getHours());
		start.setAttribute("minute", ""+rule.getStart().getMinutes());
		ruleNode.appendChild(start);

		Element stop = doc.createElement("stop");
		stop.setAttribute("hour", ""+rule.getStop().getHours());
		stop.setAttribute("minute", ""+rule.getStop().getMinutes());
		ruleNode.appendChild(stop);
		
		return ruleNode;
	}
	
	/**
	 * Remove all rules from the autoplayer
	 *
	 */
	public void clear() {
		eventMap.clear();
		scheduler.clear();
	}
	
	/**
	 * Enable the autoplayer, so the player will be automatically started or stopped
	 *
	 */
	public void enable() {
		debug("enable");
		if (Configuration.getConfigGroup("general").get("autoplayer").equals("off")) {
			//We remove all the scheduled events and add them again
			//so they will be correctly rescheduled
			scheduler.clear();
			for (AutoplayRule rule : eventMap.keySet()) {
				scheduler.addEvent( eventMap.get(rule).start );
				scheduler.addEvent( eventMap.get(rule).stop );
			}
			scheduler.start();
			Configuration.getConfigGroup("general").set("autoplayer","on");
			Configuration.save();
		}
	}
	
	/**
	 * Disable the autoplayer, so no more automatic starts or stops will happen
	 *
	 */
	public void disable() {
		debug("disable");
		if (Configuration.getConfigGroup("general").get("autoplayer").equals("on")) {
			scheduler.stop();
			Configuration.getConfigGroup("general").set("autoplayer","off");
			Configuration.save();
		}
	}
	
	/**
	 * 
	 * @return true when the autoplayer is enabled
	 * @see #enable()
	 * @see #disable()
	 */
	public boolean isEnabled() {
		return Configuration.getConfigGroup("general").get("autoplayer").equals("on");
	}
	
	/**
	 * Grouping of a rule and its two corresponding events
	 * 
	 * @author plexus
	 *
	 */
	private class RuleEvents {
		/**
		 * The stored rule
		 */
		//public AutoplayRule rule;
		
		/**
		 * The event that starts the player
		 */
		public AutoplayEvent start;
		
		/**
		 * The event that stops the player
		 */
		public AutoplayEvent stop;
	}
	
	/**
	 * The handler for the SAX parser to parse the XML file containing our persistent
	 * AutoplayRules.
	 * 
	 * @author plexus
	 *
	 */
	private class XMLHandler extends DefaultHandler {
		//This field hides a field from autoplayer, Uncomment to specify debugging only for this class
		//private boolean DEBUG = true;
		private Autoplayer autoplayer;
        private AutoplayRule rule;
       
        /**
         * Set the Autoplayer where the parsed rules will be added to
         * @param player
         */
		public XMLHandler(Autoplayer player) {
			debug ("new XMLHandler "+player);
			this.autoplayer = player;
		}

		/**
		 * Called when a new element is parsed
		 * 
		 * @param namespaceURI 
		 * @param sName 
		 * @param qName 
		 * @param attrs 
		 * 
		 */
		@Override
        public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) {
			debug ("startElement namespaceURI=\""+namespaceURI+"\" sName=\""+sName+"\" qName=\""+qName+"\"");
			
        	String eName;
        	if (sName.equals(""))
        		eName = qName;
        	else
        		eName = sName;
        	
        	if (eName.equals("autoplayrule")) {
        		rule = new AutoplayRule();
        		rule.setDayofweek(Integer.parseInt(attrs.getValue("day")));
        		rule.setPlaylist(SAM.playlists.getPlaylist(attrs.getValue("playlist")));
        	} else if (eName.equals("start")) {
        		Time s = new Time();
        		s.setHours(Integer.parseInt(attrs.getValue("hour")));
        		s.setMinutes(Integer.parseInt(attrs.getValue("minute")));
        		rule.setStart(s);
        	} else if (eName.equals("stop")) {
        		Time s = new Time();
        		s.setHours(Integer.parseInt(attrs.getValue("hour")));
        		s.setMinutes(Integer.parseInt(attrs.getValue("minute")));
        		rule.setStop(s);
        	}
        }
        
        /**
		 * Called when an element is finished parsing
		 * 
		 * @param namespaceURI 
		 * @param sName 
		 * @param qName 
         * @throws SAXException 
		 */
        @Override
		public void endElement(String namespaceURI, String sName,  String qName ) throws SAXException {
        	debug ("endElement namespaceURI=\""+namespaceURI+"\" sName=\""+sName+"\" qName=\""+qName+"\"");
        	String eName;
        	if (sName.equals(""))
        		eName = qName;
        	else
        		eName = sName;
        	
        	if (eName.equals("autoplayrule") && rule != null && rule.getPlaylist() != null) {
        		autoplayer.addRule(rule);
        		debug ("adding rule" + rule);
        	}
        }
        
        /**
         * 
         * @param msg debug message
         */
        private void debug(String msg) {
        	if (DEBUG)
        		System.out.println("Autoplayer$XMLHandler "+msg);
        }
	}
	/**
	 * Print a debug message preceded by "Autoplayer "
	 * 
	 * @param s debug message
	 */
	private void debug(String s) {
		if (DEBUG)
			System.out.println("Autoplayer "+s);
	}

}
