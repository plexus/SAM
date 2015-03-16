/*
 * Created on 28-aug-2005
 *
 */
package com.plexus.sam.net;

/**
 * Simple representation of a RemoteFile to hide different implementations.
 * 
 * @author plexus
 */
public class RemoteFile {
	private String name;
	private boolean isDir = false;
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setDir(boolean isDir) {
		this.isDir = isDir;
	}
	
	public boolean isDir() {
		return isDir;
	}
	
	public String toString() {
		return name;
	}
}
