/*
 * Created on 14-sep-2005
 *
 */
package com.plexus.debug;

/**
 * Debugging tool that contains a bunch of static 'assertion' messages.
 * This allows for programs to 'fail fast' when unexpected conditions arise,
 * in stead of carrying on and failing later on in unexpected ways.
 * 
 * Each method takes a message, this message should be written in a way that it
 * allows to locate the failure point quickly, since no exceptions are thrown,
 * no stacktrace is available.
 * 
 * @author plexus
 *
 */
public class Assert {
	/**
	 * Assert the boolean value is true, otherwise output a message
	 * @param msg message to output
	 * @param b must be true
	 */
	public static void isTrue(String msg, boolean b) {
		if (!b) {
			System.out.println("Assertion failed : "+msg);
			System.exit(-1);
		}	
	}
	
	/**
	 * Assert the boolean value is false, otherwise output a message
	 * @param msg message to output
	 * @param b must be false
	 */
	public static void isFalse(String msg, boolean b) {
		if (b) {
			System.out.println("Assertion failed : "+msg);
			System.exit(-1);
		}	
	}
	
	/**
	 * Assert the object contains a null value, otherwise output a message
	 * @param msg message to output
	 * @param o must be null
	 */
	public static void isNull(String msg, Object o) {
		if (o != null) {
			System.out.println("Assertion failed : "+o+" expected to be null.\n"+msg);
			System.exit(-1);
		}	
	}
	
	/**
	 * Assert the object doesn't contains a null value, otherwise output a message
	 * @param msg message to output
	 * @param o must not be null
	 */
	public static void notNull(String msg, Object o) {
		if (o == null) {
			System.out.println("Assertion failed : object expected to be not null.\n"+msg);
			System.exit(-1);
		}	
	}
	
	/**
	 * Assert the objects are equal (using the equals() method), otherwise output a message
	 * @param msg message to output
	 * @param o1 object to compare 
	 * @param o2 object to compare
	 */
	public static void equals(String msg, Object o1, Object o2) {
		if (! o1.equals(o2)) {
			System.out.println("Assertion failed : objects not equal.\n o1:"+o1+"\n o2:"+o2+"\n"+msg);
			System.exit(-1);
		}	
	}
	
	/**
	 * Assert the objects are the same (references point to the same object), otherwise output a message
	 * @param msg message to output
	 * @param o1 object to compare 
	 * @param o2 object to compare
	 */
	public static void same(String msg, Object o1, Object o2) {
		if (o1 != o2) {
			System.out.println("Assertion failed : objects not the same.\n o1:"+o1+"\n o2:"+o2+"\n"+msg);
			System.exit(-1);
		}	
	}
	
	/**
	 * Assert the objects are unequal (using the equals() method), otherwise output a message
	 * @param msg message to output
	 * @param o1 object to compare 
	 * @param o2 object to compare
	 */
	public static void notEquals(String msg, Object o1, Object o2) {
		if (o1.equals(o2)) {
			System.out.println("Assertion failed : objects are equal.\n o1:"+o1+"\n o2:"+o2+"\n"+msg);
			System.exit(-1);
		}	
	}
	
	/**
	 * Assert the objects are not the same (references point to different objects), otherwise output a message
	 * @param msg message to output
	 * @param o1 object to compare 
	 * @param o2 object to compare
	 */
	public static void notSame(String msg, Object o1, Object o2) {
		if (o1 == o2) {
			System.out.println("Assertion failed : objects are the same.\n o1:"+o1+"\n o2:"+o2+"\n"+msg);
			System.exit(-1);
		}	
	}
}
