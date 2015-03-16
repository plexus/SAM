/*
 * Created on 28-aug-2005
 *
 */
package com.plexus.sam.net;

import java.util.List;

/**
 * Interface to allow different protocols to be used, each having
 * its own implementation of this interface.
 * Every implementation should use a ConfigGroup of its own to
 * get the values not provided by these methods (such as uid/pwd).
 * 
 * @author plexus
 *
 */
public interface FileTransferModule {
	/**
	 * Open a connection to receive file/directory information.
	 *
	 */
	public void connect() throws Exception;
	
	/**
	 * Get a directory as a list of RemoteFile objects
	 * 
	 * @param dir which directory to get
	 * @return list of RemoteFile objects
	 */
	public List getDirectory(String dir) throws Exception;
	
	/**
	 * Copy a file from a remote to a local destination.
	 * 
	 * @param remote
	 * @param local
	 */
	public void getFile(String remote, String local) throws Exception;
	
	/**
	 * Close the connection, we're done for now.
	 *
	 */
	public void disConnect();
	
	/**
	 * Verify wheter connecting succeeded.
	 * 
	 * @return connected, yes or no
	 */
	public boolean isConnected();
}
