/*
 * Created on 24-sep-2005
 *
 */
package com.plexus.util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Implementation of Document that makes the document only accept integers.
 */
public class IntegerDocument extends PlainDocument {
	/**
	 * 
	 *
	 */
	public IntegerDocument() {
		super();
	}
	
	/**
	 * @param offs
	 * @param str
	 * @param a
	 * @throws BadLocationException
	 */
	@Override
	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
		
		if (str == null) {
			return;
		}
		try {
			Integer.parseInt(str);
			super.insertString(offs, str, a);
		} catch (NumberFormatException e) {}
	}
}