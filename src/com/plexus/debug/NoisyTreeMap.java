/*
 * Created on 2-sep-2005
 *
 */
package com.plexus.debug;

import java.util.TreeMap;

/**
 * A noisy TreeMap, {@link #put(Object, Object)}, {@link #remove(Object)} and {@link #size()}
 * are overriden to output debugging messages
 * @author plexus
 * @param <K> Key class
 * @param <V> Value class
 *
 */
public class NoisyTreeMap<K,V> extends TreeMap<K,V> {
	/**
	 * Delegate put method, @see TreeMap#put(K, V) 
	 * @param o1
	 * @param o2
	 * @return previous value associated with specified key, 
	 * or null if there was no mapping for key. A null return can also 
	 * indicate that the map previously associated null with 
	 * the specified key.
	 */
	@Override
	public V put(K o1, V o2) {
		System.out.println("Putting "+o1+ "->"+o2);
		System.out.println("Size = "+super.size());
		return super.put(o1, o2);
	}
	
	/**
	 * @see TreeMap#remove(java.lang.Object)
	 * @param key
	 * @return previous value associated with specified key, 
	 * or null if there was no mapping for key. A null return can 
	 * also indicate that the map previously associated null with 
	 * the specified key.
	 */
	@Override
	public V remove(Object key) {
		System.out.println("Removing "+key);
		System.out.println("Size = "+super.size());
		return super.remove(key);
	}
	
	/**
	 * @see TreeMap#size()
	 * @return the number of key-value mappings in this map.
	 */
	@Override
	public int size() {
		System.out.println("Size = "+super.size());
		return super.size();
	}
	
}
