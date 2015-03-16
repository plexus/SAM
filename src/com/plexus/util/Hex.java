/*
 * Created on 12-okt-2005
 *
 */
package com.plexus.util;

/**
 * Contains utility methods for converting from and to hexadecimal values
 * @author plexus
 *
 */
public class Hex {
	
	/**
	 * Convert a String containing hexadecimal didgits (0-9 or A-F or a-f) to a string where every
	 * two hex didgits are converted to one byte which is cast to a char and appended. (So only th extended
	 * ASCII chars are used)
	 * 
	 * @param s
	 * @return the decoded string
	 */
	public static String hexToString(String s) {
		String result = "";
		char b;
		for (int i = 0; i < s.length() ; i++) {
			if (s.length() - i > 1) {
				b = (char)(nibbleTohalfByte(s.charAt(i)) << 4);
				b += nibbleTohalfByte(s.charAt(++i));
				result += b;
				
			}
		}
		return result;
	}
	
	
	/**
	 * Do the inverse of {@link #hexToString(String)}, convert a string to a series of hexadecimal didgets.
	 * It is assumed that only char values under 256 are used.
	 *  
	 * @param s
	 * @return the encoded string
	 */
	public static String stringToHex(String s) {
		StringBuffer result = new StringBuffer(s.length()*2);
		for (char c : s.toCharArray()) {
			result.append(byteToHex(c));
		}
		return result.toString();
	}
	
	/**
	 * Convert a byte to a string containing two hexadecimal didgits
	 *  
	 * @param b byte to be converted
	 * @return a string of length two with characters 0-9 or A-F
	 */
	public static String byteToHex(char b) {
		b <<= 8;
		b >>= 8;
		byte upper = (byte)(b >>> 4);
		byte lower = (byte)(b - (upper << 4));
		return ""+halfbyteToNibble(upper)+halfbyteToNibble(lower);
	}
	
	/**
	 * Convert a char containing a hexadecimal didgit (0-9, A-F, a-f) to a byte with a value
	 * from 0 to 15.
	 * 
	 * @param s hexadecimal didgit
	 * @return value from 0 to 15
	 */
	public static byte nibbleTohalfByte(char s) {
		if (s >= '0' && s <='9')
			return (byte)(s - '0');
		if (s >= 'A' && s <= 'F')
			return (byte)(s - 'A' + 10);
		if (s >= 'a' && s <= 'f')
			return (byte)(s - 'a' + 10);
		return 0;
	}
	
	/**
	 * Convert a value between 0 and 15 inclusive to a char containing a hexadecimal didgit
	 * 
	 * @param b
	 * @return a hexidecimal didgit 0-9 or A-F
	 */
	public static char halfbyteToNibble(byte b) {
		if (b > 15 || b < 0)
			return '0';
		if (b <= 9)
			return (char)('0'+b);
		if (b > 9 && b < 16) {
			return (char)('A' + (b-10));
		}
		return ('0');
	}
}
