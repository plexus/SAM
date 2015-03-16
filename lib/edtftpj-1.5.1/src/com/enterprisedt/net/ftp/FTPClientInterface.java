/**
*
*  edtFTPj
*
*  Copyright (C) 2000-2003  Enterprise Distributed Technologies Ltd
*
*  www.enterprisedt.com
*
*  This library is free software; you can redistribute it and/or
*  modify it under the terms of the GNU Lesser General Public
*  License as published by the Free Software Foundation; either
*  version 2.1 of the License, or (at your option) any later version.
*
*  This library is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
*  Lesser General Public License for more details.
*
*  You should have received a copy of the GNU Lesser General Public
*  License along with this library; if not, write to the Free Software
*  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*
 *  Bug fixes, suggestions and comments should be should posted on 
 *  http://www.enterprisedt.com/forums/index.php
*
*  Change Log:
*
*        $Log: FTPClientInterface.java,v $
*        Revision 1.1  2005/08/28 13:40:11  plexus
*        *** empty log message ***
*
*        Revision 1.2  2005/06/03 11:26:25  bruceb
*        comment change
*
* 
*/

package com.enterprisedt.net.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.Date;

/**
 * Defines operations in common with a number of FTP implementations
 * 
 * @author     hans
 * @version    $Revision: 1.1 $
 */
public interface FTPClientInterface {
	/**
	 *   Set the TCP timeout on the underlying socket.
	 *
	 *   If a timeout is set, then any operation which
	 *   takes longer than the timeout value will be
	 *   killed with a java.io.InterruptedException. We
	 *   set both the control and data connections
	 *
	 *   @param millis The length of the timeout, in milliseconds
	 */
	public void setTimeout(int millis) throws IOException, FTPException;

	/**
	 *  Set a progress monitor for callbacks. The bytes transferred in
	 *  between callbacks is only indicative. In many cases, the data is
	 *  read in chunks, and if the interval is set to be smaller than the
	 *  chunk size, the callback will occur after after chunk transfer rather
	 *  than the interval.
	 *
	 *  @param  monitor   the monitor object
	 *  @param  interval  bytes transferred in between callbacks
	 */
	public void setProgressMonitor(FTPProgressMonitor monitor,
			long interval);

	/**
	 *  Set a progress monitor for callbacks. Uses default callback
	 *  interval
	 *
	 *  @param  monitor   the monitor object
	 */
	public void setProgressMonitor(FTPProgressMonitor monitor);

	/**
	 *  Get the bytes transferred between each callback on the
	 *  progress monitor
	 * 
	 * @return long     bytes to be transferred before a callback
	 */
	public long getMonitorInterval();

	/**
	 *  Get the size of a remote file. This is not a standard FTP command, it
	 *  is defined in "Extensions to FTP", a draft RFC 
	 *  (draft-ietf-ftpext-mlst-16.txt)
	 *
	 *  @param  remoteFile  name or path of remote file in current directory
	 *  @return size of file in bytes      
	 */
	public long size(String remoteFile) throws IOException,
			FTPException;

	/**
	 *  Put a local file onto the FTP server. It
	 *  is placed in the current directory.
	 *
	 *  @param  localPath   path of the local file
	 *  @param  remoteFile  name of remote file in
	 *                      current directory
	 */
	public void put(String localPath, String remoteFile)
			throws IOException, FTPException;

	/**
	 *  Put a stream of data onto the FTP server. It
	 *  is placed in the current directory.
	 *
	 *  @param  srcStream   input stream of data to put
	 *  @param  remoteFile  name of remote file in
	 *                      current directory
	 */
	public void put(InputStream srcStream, String remoteFile)
			throws IOException, FTPException;

	/**
	 *  Put data onto the FTP server. It
	 *  is placed in the current directory.
	 *
	 *  @param  bytes        array of bytes
	 *  @param  remoteFile  name of remote file in
	 *                      current directory
	 */
	public void put(byte[] bytes, String remoteFile)
			throws IOException, FTPException;

	/**
	 *  Get data from the FTP server. Uses the currently
	 *  set transfer mode.
	 *
	 *  @param  localPath   local file to put data in
	 *  @param  remoteFile  name of remote file in
	 *                      current directory
	 */
	public void get(String localPath, String remoteFile)
			throws IOException, FTPException;

	/**
	 *  Get data from the FTP server. Uses the currently
	 *  set transfer mode.
	 *
	 *  @param  destStream  data stream to write data to
	 *  @param  remoteFile  name of remote file in
	 *                      current directory
	 */
	public void get(OutputStream destStream, String remoteFile)
			throws IOException, FTPException;

	/**
	 *  Get data from the FTP server. Transfers in
	 *  whatever mode we are in. Retrieve as a byte array. Note
	 *  that we may experience memory limitations as the
	 *  entire file must be held in memory at one time.
	 *
	 *  @param  remoteFile  name of remote file in
	 *                      current directory
	 */
	public byte[] get(String remoteFile) throws IOException,
			FTPException;

	/**
	 *  List a directory's contents as an array of FTPFile objects.
	 *  Should work for Windows and most Unix FTP servers - let us know
	 *  about unusual formats (http://www.enterprisedt.com/forums/index.php)
	 *
	 *  @param   dirname  name of directory OR filemask
	 *  @return  an array of FTPFile objects
	 */
	public FTPFile[] dirDetails(String dirname) throws IOException,
			FTPException, ParseException;

	/**
	 *  List current directory's contents as an array of strings of
	 *  filenames.
	 *
	 *  @return  an array of current directory listing strings
	 */
	public String[] dir() throws IOException, FTPException;

	/**
	 *  List a directory's contents as an array of strings of filenames.
	 *
	 *  @param   dirname  name of directory OR filemask
	 *  @return  an array of directory listing strings
	 */
	public String[] dir(String dirname) throws IOException,
			FTPException;

	/**
	 *  List a directory's contents as an array of strings. A detailed
	 *  listing is available, otherwise just filenames are provided.
	 *  The detailed listing varies in details depending on OS and
	 *  FTP server. Note that a full listing can be used on a file
	 *  name to obtain information about a file
	 *
	 *  @param  dirname  name of directory OR filemask
	 *  @param  full     true if detailed listing required
	 *                   false otherwise
	 *  @return  an array of directory listing strings
	 */
	public String[] dir(String dirname, boolean full)
			throws IOException, FTPException;

	/**
	 *  Delete the specified remote file
	 *
	 *  @param  remoteFile  name of remote file to
	 *                      delete
	 */
	public void delete(String remoteFile) throws IOException,
			FTPException;

	/**
	 *  Rename a file or directory
	 *
	 * @param from  name of file or directory to rename
	 * @param to    intended name
	 */
	public void rename(String from, String to) throws IOException,
			FTPException;

	/**
	 *  Delete the specified remote working directory
	 *
	 *  @param  dir  name of remote directory to
	 *               delete
	 */
	public void rmdir(String dir) throws IOException, FTPException;

	/**
	 *  Create the specified remote working directory
	 *
	 *  @param  dir  name of remote directory to
	 *               create
	 */
	public void mkdir(String dir) throws IOException, FTPException;

	/**
	 *  Change the remote working directory to
	 *  that supplied
	 *
	 *  @param  dir  name of remote directory to
	 *               change to
	 */
	public void chdir(String dir) throws IOException, FTPException;
    
    /**
     *  Change the remote working directory to
     *  the parent directory
     */
    public void cdup() throws IOException, FTPException;

	/**
	 *  Get modification time for a remote file
	 *
	 *  @param    remoteFile   name of remote file
	 *  @return   modification time of file as a date
	 */
	public Date modtime(String remoteFile) throws IOException,
			FTPException;

	/**
	 *  Get the current remote working directory
	 *
	 *  @return   the current working directory
	 */
	public String pwd() throws IOException, FTPException;

	/**
	 *  Quit the FTP session
	 *
	 */
	public void quit() throws IOException, FTPException;
}