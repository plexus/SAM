/*
 * Created on 11-sep-2005
 *
 */
package com.plexus.sam.testcases;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author plexus
 *
 */
public class SamTest extends TestCase {

	/**
	 * 
	 * @return A testsuite containing all the suites written so far
	 */
	public static Test suite() { 
	    TestSuite suite= new TestSuite(); 
	    suite.addTest(PlayListTest.suite());
	    suite.addTest(SimplePlaylistTest.suite());
	    suite.addTest(PlayerTest.suite());
	    suite.addTest(AutoplayTest.suite()); //this one takes more than four minutes, be prepared!
	    suite.addTest(HexTest.suite());
	    suite.addTest(TriggersTest.suite()); // Also an extensive one
	    return suite;
	}
}
