/*
 * Created on 10-sep-2005
 *
 */
package com.plexus.sam.gui;

import java.util.ResourceBundle;

import com.plexus.sam.SAM;

/**
 * Wrapper of a class object returning its name as defined in the resourcebundle com.plexus.sam.i18n.classes
 * by the toString() method.
 *
 */
public class NamedClass {
	private Class mClass;
	private String name;
	private static ResourceBundle classNames = SAM.getBundle("classes");
	
	/**
	 * Store the class and the name of it fetched from the ResourceBundle com.plexus.sam.i18n.classes.
	 * @param c
	 */
	public NamedClass(Class c) {
		mClass = c;
		
		//fail fast, class name must be in resourcebundle 'classes'
		name = classNames.getString( mClass.getSimpleName() );
	}
	
	/**
	 * @return Returns the class.
	 */
	public Class theClass() {
		return mClass;
	}
	
	/**
	 * @return Returns the name.
	 */
	public String toString() {
		return name;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
}
