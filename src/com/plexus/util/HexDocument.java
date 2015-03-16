/*
 * Created on 12-okt-2005
 *
 */
package com.plexus.util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class HexDocument extends PlainDocument {

	
	@Override
	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
		StringBuffer buffer = new StringBuffer();
		for (char c : str.toCharArray()) {
			if ((c >= '0' && c <='9') || (c >='A' && c <= 'F') || (c >='a' && c <= 'f'))
				buffer.append(c);
		}
		super.insertString(offs, buffer.toString(), a);
	}

}
