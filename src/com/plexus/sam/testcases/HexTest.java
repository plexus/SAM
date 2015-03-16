/*
 * Created on 12-okt-2005
 *
 */
package com.plexus.sam.testcases;

import com.plexus.util.Hex;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;



public class HexTest extends TestCase {
	char[] hexchars1;
	char[] hexchars2;
	
	public HexTest(String s) {
		super (s);
	}
	
	protected void setUp() throws Exception {
		hexchars1 = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
		hexchars2 = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	}

	public void testhalfbyteToNibble() {
		for (byte i = 0; i < 10; i++)
			assertEquals("halfbyte to nibble is wrong at "+i, Hex.halfbyteToNibble(i) - '0', (char)i );
		
		for (byte i = 10; i < 16; i++)
			assertEquals("halfbyte to nibble is wrong at "+i, Hex.halfbyteToNibble(i) - 'A', (char)i - 10 );
	}
	
	public void testnibbleTohalfByte() {
		int i=0;
		for (char c:hexchars1) {
			assertEquals("nibbleTohalfByte fails at '"+c+"'", Hex.nibbleTohalfByte(c), i++);
		}
		
		i=0;
		for (char c:hexchars2) {
			assertEquals("nibbleTohalfByte fails at '"+c+"'", Hex.nibbleTohalfByte(c), i++);
		}
	}
	
	
	public void testbyteToHex() {
		char c1 = '0';
		char c2 = '0';
		for (char b = 0; b < 256; b++) {
			assertEquals("byteToHex fails at bytevalue "+b+" "+c1+c2+" != "+Hex.byteToHex(b), ""+c1+c2, Hex.byteToHex(b));
			c2++;
			if (c2=='9'+1)
				c2='A';
			if (c2=='F'+1) {
				c2 = '0';
				c1 ++;
			}
			
			if (c1=='9'+1)
				c1='A';
			if (c1=='F'+1) {
				c1 = '0';
			}
		}
	}
	
	public void testStringToHex() {
		String[] teststring = {"jolijtig", "#m[]mlk", "PKskeµ$"};
		String t1 = ""+(char)16+(char)255+(char)(13+64);
		assertEquals (Hex.stringToHex(t1), "10FF4D");
	}
	
	public void testHexToString() {
		assertEquals(Hex.hexToString("10FF4D"), ""+(char)16+(char)255+(char)(13+64));
	}
	
	public static Test suite() { 
	    TestSuite suite= new TestSuite(); 
	    suite.addTest(new HexTest("testhalfbyteToNibble"));
	    suite.addTest(new HexTest("testnibbleTohalfByte"));
	    suite.addTest(new HexTest("testbyteToHex"));
	    suite.addTest(new HexTest("testStringToHex"));
	    suite.addTest(new HexTest("testHexToString"));
	    return suite;
	}
}
