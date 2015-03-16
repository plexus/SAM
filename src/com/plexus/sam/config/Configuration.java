/*
 * Created on 27-aug-2005
 *
 */
package com.plexus.sam.config;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory;    
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.SAXException;  

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.plexus.sam.SAM;

/**
 * The configuration class can <code>load()</code> or <code>save()</code>
 * itself to an XML file. The location of this file is determined by
 * calling <code>StaticConfig.get("config-path")</code>.
 * <br /><br />
 * The configuration is divided into groups, <code>ConfigGroup</code>, i.e.
 * net, audio, ... each containing a number of key/value pairs.
 * 
 * @author plexus
 */
public class Configuration {
	private static Document configDocument;
	private static Map<String, ConfigGroup> configGroups = new HashMap<String, ConfigGroup>();
	private static ResourceBundle error = SAM.getBundle("error");
	
	static {
		load();
	}
	
	/**
	 * Load the configuration from the configuration file defined in StaticConfig.	 
	 */
	private static void load() {
		try {
			String configLocation = StaticConfig.get( "config-path" );
			File configFile = new File( configLocation );
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbFactory.newDocumentBuilder();
			configDocument = db.parse( configFile );
			createGroups( configDocument.getElementsByTagName( "group" ) );
		}
		catch (SAXException e) {
			SAM.fatal(error.getString("config_parse_error"), e);
		}
		catch (ParserConfigurationException e) {
			SAM.fatal(error.getString("config_parse_error"), e);
		}
		catch (IOException e) {
			SAM.fatal(error.getString("config_open_error"), e);
		}
	}
	
	/**
	 * Saves the configuration to the file defined in StaticConfig.
	 *
	 */
	public static void save() {
		try {
			TransformerFactory tFactory =
				TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			
			DOMSource source = new DOMSource( configDocument );
			StreamResult result = new StreamResult( new FileOutputStream( new File( StaticConfig.get("config-path") ) ) );
			transformer.transform( source, result );
			
			result.getOutputStream().close();
		} catch (IOException e) {
			SAM.error("config_write_error", e);
		}
		
		catch (TransformerConfigurationException e) {
			SAM.error("config_write_error", e);
		}
		catch (TransformerException e) {
			SAM.error("config_write_error", e);
		} 
	}
	
	/**
	 * The DOM document that stores the configuration information. This is mainly provided for
	 * the factory functions that create different nodes.
	 * 
	 * @return configuration DOM document
	 */
	public static Document getConfigDocument() {
		return configDocument;
	}
	
	/**
	 * Get ConfigGroup by name
	 * 
	 * @param name
	 * @return configgroup or null if name doesn't exist
	 */
	public static ConfigGroup getConfigGroup(String name) {
		if (configGroups.containsKey( name ))
			return configGroups.get( name );
		return null;
	}
	
	/**
	 * Create the configuration group objects from their nodes.
	 * @param groupNodes
	 */
	private static void createGroups(NodeList groupNodes) {
		for(int i=0; i<groupNodes.getLength(); i++) {
			ConfigGroup cg = new ConfigGroup( groupNodes.item(i) );
			configGroups.put( cg.getName(), cg );
		}
	}

	/**
	 * Convenience method for accesing a value in a group
	 * @param group
	 * @param key
	 * @return value
	 */
	public static String get(String group, String key) {
		return getConfigGroup(group).get(key);
	}
}
