/*
 * Created on 27-aug-2005
 *
 */
package com.plexus.sam.config;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



/**
 * This class represents a group of configuration values, it is identified in the Configuration
 * by its name. It reads and writes directly from the DOM tree of the configuration XML file.
 * <br /><br />
 * Changes can be notified by registering a ChangeListener.
 * 
 * @author plexus
 *
 */
public class ConfigGroup {
	private String name;
	private Node groupNode;
	private NodeList values;
	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
	
	/**
	 * Initialise when given the node in the DOM tree representing this group.<br />
	 * The name, and the value-elements are extracted. 
	 * @param groupNode
	 */
	public ConfigGroup(Node groupNode) {
		Attr  nameAttr = (Attr)groupNode.getAttributes().getNamedItem("name");
		name = nameAttr.getValue();
		values = groupNode.getChildNodes();
		this.groupNode=groupNode;
	}
	
	/**
	 * Get a value from this configurationgroup.
	 * 
	 * @param key
	 * @return corresponding value or null when no value is set.
	 */
	public String get(String key) {
		for(int i=0 ; i<values.getLength() ; i++) {
			Node value = values.item(i);						
			if (value instanceof Element) { 			
				Attr keyAttr = (Attr)value.getAttributes().getNamedItem("key"); 
				if (keyAttr != null && keyAttr.getValue().equals(key))
					return value.getTextContent();
			}
		}
		return null;
	}
	
	/**
	 * Set a value with a certain key. When the key is not in use a new value entry is 
	 * created.
	 * 
	 * @param key
	 * @param text
	 */
	public void set(String key, String text) {
		for(int i=0 ; i<values.getLength() ; i++) {
			Node value = values.item(i);
			if (value instanceof Element) {
				Attr keyAttr = (Attr)value.getAttributes().getNamedItem("key"); 			
				if (keyAttr != null && keyAttr.getValue().equals(key)) {
					String oldValue = value.getTextContent();
					value.setTextContent(text);
					changeSupport.firePropertyChange(key, oldValue, text);					
					return;
				}
			}
		}

        Element valueElement = Configuration.getConfigDocument().createElement("value");
		valueElement.setAttribute( "key", key);
		String oldText = valueElement.getTextContent();
		valueElement.setTextContent( text );
		groupNode.appendChild(valueElement);
		changeSupport.firePropertyChange(key, oldText, text);
	}
	
	/**
	 * Returns the name of this configGroup.
	 * 
	 * @return identifying configgroup name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Add a listener to be notified of configuration changes.
	 * 
	 * @param l listener object
	 */
	public void addChangeListener(PropertyChangeListener l) {
		changeSupport.addPropertyChangeListener(l);
	}
	
	/**
	 * Remove a changeListener from the listener list.
	 * 
	 * @param l listener object
	 */
	public void removeChangeListener(PropertyChangeListener l) {
		changeSupport.removePropertyChangeListener(l);
	}
}
