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
 *        $Log: FTPClient.java,v $
 *        Revision 1.1  2005/08/28 13:40:11  plexus
 *        *** empty log message ***
 *
 *        Revision 1.41  2005/06/10 15:44:38  bruceb
 *        added noOperation() and connected()
 *
 *        Revision 1.40  2005/06/03 11:25:17  bruceb
 *        ascii fixes, setActivePortRange
 *
 *        Revision 1.41  2005/05/24 11:32:28  bruceb
 *        version + timestamp info in static block
 *
 *        Revision 1.40  2005/05/15 19:46:28  bruceb
 *        changes for testing setActivePortRange + STOR accepting 350 nonstrict
 *
 *        Revision 1.39  2005/04/01 13:58:15  bruceb
 *        restructured dir() exception handling + quote() change
 *
 *        Revision 1.38  2005/03/18 11:04:32  bruceb
 *        deprecated constructors
 *
 *        Revision 1.37  2005/03/11 14:40:11  bruceb
 *        added cdup() and changed buffer defaults
 *
 *        Revision 1.36  2005/03/03 21:07:14  bruceb
 *        implement interface & augment login doco
 *
 *        Revision 1.35  2005/02/04 12:40:35  bruceb
 *        tidied javadoc
 *
 *        Revision 1.34  2005/02/04 12:28:51  bruceb
 *        when getting, if file exists and is readonly, exception is thrown
 *
 *        Revision 1.33  2005/01/28 13:55:39  bruceb
 *        added ACCT handling
 *
 *        Revision 1.32  2005/01/14 20:27:02  bruceb
 *        exception restructuring + ABOR
 *
 *        Revision 1.31  2004/11/19 08:28:10  bruceb
 *        added setPORTIP()
 *
 *        Revision 1.30  2004/10/18 15:54:48  bruceb
 *        clearSOCKS added, set encoding for control sock, locale for parser
 *
 *        Revision 1.29  2004/09/21 21:28:28  bruceb
 *        fixed javadoc comment
 *
 *        Revision 1.28  2004/09/18 14:27:57  bruceb
 *        features() throw exception if not supported
 *
 *        Revision 1.27  2004/09/18 09:33:47  bruceb
 *        1.1.8 tweaks
 *
 *        Revision 1.26  2004/09/17 14:12:38  bruceb
 *        fixed javadoc re filemasks
 *
 *        Revision 1.25  2004/09/14 06:24:03  bruceb
 *        fixed javadoc comment
 *
 *        Revision 1.24  2004/08/31 13:48:29  bruceb
 *        resume,features,restructure
 *
 *        Revision 1.23  2004/07/23 08:34:32  bruceb
 *        strict replies or not, better tfr monitor reporting
 *
 *        Revision 1.22  2004/06/25 11:47:46  bruceb
 *        made 1.1.x compatible
 *
 *        Revision 1.21  2004/06/11 10:20:35  bruceb
 *        permit 200 to be returned from various cmds
 *
 *        Revision 1.20  2004/05/22 16:52:57  bruceb
 *        message listener
 *
 *        Revision 1.19  2004/05/15 22:37:22  bruceb
 *        put debugResponses back in
 *
 *        Revision 1.18  2004/05/13 23:00:34  hans
 *        changed comment
 *
 *        Revision 1.17  2004/05/08 21:14:41  bruceb
 *        checkConnection stuff
 *
 *        Revision 1.14  2004/04/19 21:54:06  bruceb
 *        final tweaks to dirDetails() re caching
 *
 *        Revision 1.13  2004/04/18 11:16:44  bruceb
 *        made validateTransfer() public
 *
 *        Revision 1.12  2004/04/17 18:37:38  bruceb
 *        new parse functionality
 *
 *        Revision 1.11  2004/03/23 20:26:49  bruceb
 *        tweak to size(), catch exceptions on puts()
 *
 *        Revision 1.10  2003/11/15 11:23:55  bruceb
 *        changes required for ssl subclasses
 *
 *        Revision 1.6  2003/05/31 14:53:44  bruceb
 *        1.2.2 changes
 *
 *        Revision 1.5  2003/01/29 22:46:08  bruceb
 *        minor changes
 *
 *        Revision 1.4  2002/11/19 22:01:25  bruceb
 *        changes for 1.2
 *
 *        Revision 1.3  2001/10/09 20:53:46  bruceb
 *        Active mode changes
 *
 *        Revision 1.1  2001/10/05 14:42:03  bruceb
 *        moved from old project
 *
 */

package com.enterprisedt.net.ftp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;
import java.util.Vector;

import com.enterprisedt.util.debug.Level;
import com.enterprisedt.util.debug.Logger;

/**
 *  Supports client-side FTP. Most common
 *  FTP operations are present in this class.
 *
 *  @author      Bruce Blackshaw
 *  @version     $Revision: 1.1 $
 */
public class FTPClient implements FTPClientInterface {

    /**
     *  Revision control id
     */
    public static String cvsId = "@(#)$Id: FTPClient.java,v 1.1 2005/08/28 13:40:11 plexus Exp $";
    
    /**
     * Default byte interval for transfer monitor
     */
    final private static int DEFAULT_MONITOR_INTERVAL = 65535;
    
    /**
     * Default transfer buffer size
     */
    final private static int DEFAULT_BUFFER_SIZE = 16384;
    
    /**
     * Maximum port number
     */
    final private static int MAX_PORT = 65535;
    
    /**
     * Default encoding used for control data
     */
    final public static String DEFAULT_ENCODING = "US-ASCII";
    
    /**
     * SOCKS port property name
     */
    final private static String SOCKS_PORT = "socksProxyPort";

    /**
     * SOCKS host property name
     */
    final private static String SOCKS_HOST = "socksProxyHost";
    
    /**
     * Line separator
     */
    final private static byte[] LINE_SEPARATOR = System.getProperty("line.separator").getBytes();
    
    /**
     * Used for ASCII translation
     */
    final private static byte CARRIAGE_RETURN = 13;
    
    /**
     * Used for ASCII translation
     */
    final private static byte LINE_FEED = 10;
    
    /**
     * Major version (substituted by ant)
     */
    private static String majorVersion = "@major_ver@";
    
    /**
     * Middle version (substituted by ant)
     */
    private static String middleVersion = "@middle_ver@";
    
    /**
     * Middle version (substituted by ant)
     */
    private static String minorVersion = "@minor_ver@";
    
    /**
     * Full version
     */
    private static int[] version;
    
    /**
     * Timestamp of build
     */
    private static String buildTimestamp = "@date_time@";
    
    /**
     * Logging object
     */
    private static Logger log = Logger.getLogger(FTPClient.class);
    
    /**
     * Work out the version array
     */
    static {
        try {
            version = new int[3];
            version[0] = Integer.parseInt(majorVersion);
            version[1] = Integer.parseInt(middleVersion);
            version[2] = Integer.parseInt(minorVersion);
            log.info("edtFTPj version: " + version[0] + "." + version[1] + "." + version[2]);
            log.info("edtFTPj build timestamp: " + getBuildTimestamp());
        }
        catch (NumberFormatException ex) {
            System.err.println("Failed to calculate version: " + ex.getMessage());
        }
    }
    
    /**
     *  Format to interpret MTDM timestamp
     */
    private SimpleDateFormat tsFormat =
        new SimpleDateFormat("yyyyMMddHHmmss");
    
    /**
     *  Socket responsible for controlling
     *  the connection
     */
	protected FTPControlSocket control = null;

    /**
     *  Socket responsible for transferring
     *  the data
     */
    protected FTPDataSocket data = null;

    /**
     *  Socket timeout for both data and control. In
     *  milliseconds
     */
    private int timeout = 0;
    
    /**
     * Address of the remote server.
     */
    private InetAddress remoteAddr;
    
    /**
     * Name/IP of remote host
     */
    private String remoteHost;

    /**
     * Control port number.
     */
    private int controlPort = FTPControlSocket.CONTROL_PORT;
    
    /**
     * Encoding used on control socket
     */
    private String controlEncoding = DEFAULT_ENCODING;
      
    /**
     * Use strict return codes if true
     */
    private boolean strictReturnCodes = true;
    
    /**
     *  Can be used to cancel a transfer
     */
    private boolean cancelTransfer = false;
    
    /**
     * If true, a file transfer is being resumed
     */
    private boolean resume = false;
    
    /**
     * Resume byte marker point
     */
    private long resumeMarker = 0;
    
    /**
     * Bytes transferred in between monitor callbacks
     */
    private long monitorInterval = DEFAULT_MONITOR_INTERVAL;
    
    /**
     * Size of transfer buffers
     */
    private int transferBufferSize = DEFAULT_BUFFER_SIZE;
    
    /**
     * Parses LIST output
     */
    private FTPFileFactory fileFactory = null;
    
    /**
     * Locale for date parsing
     */
    private Locale listingLocale = Locale.getDefault();
    
    /**
     *  Progress monitor
     */
    private FTPProgressMonitor monitor = null;  
    
    /**
     * Message listener
     */
    protected FTPMessageListener messageListener = null;

    /**
     *  Record of the transfer type - make the default ASCII
     */
    private FTPTransferType transferType = FTPTransferType.ASCII;

    /**
     *  Record of the connect mode - make the default PASV (as this was
     *  the original mode supported)
     */
    private FTPConnectMode connectMode = FTPConnectMode.PASV;

    /**
     *  Holds the last valid reply from the server on the control socket
     */
	protected FTPReply lastValidReply;    
    
    /**
     *  Instance initializer. Sets formatter to GMT.
     */
    {
        tsFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }  
    
    
    /**
     * Get the version of edtFTPj
     * 
     * @return int array of {major,middle,minor} version numbers 
     */
    public static int[] getVersion() {
        return version;
    }
    
    /**
     * Get the build timestamp
     * 
     * @return d-MMM-yyyy HH:mm:ss z build timestamp 
     */
    public static String getBuildTimestamp() {
        return buildTimestamp;
    }

    /**
     *  Constructor. Creates the control
     *  socket
     *
     *  @param   remoteHost  the remote hostname
     *  @deprecated  use setter methods to set properties
     */
    public FTPClient(String remoteHost)
        throws IOException, FTPException {

		this(remoteHost, FTPControlSocket.CONTROL_PORT, 0);
    }

    /**
     *  Constructor. Creates the control
     *  socket
     *
     *  @param   remoteHost  the remote hostname
     *  @param   controlPort  port for control stream (-1 for default port)
     *  @deprecated  use setter methods to set properties
     */
    public FTPClient(String remoteHost, int controlPort)
        throws IOException, FTPException {

		this(remoteHost, controlPort, 0);
    }
    
    
    /**
     *  Constructor. Creates the control
     *  socket
     *
     *  @param   remoteHost  the remote hostname
     *  @param   controlPort  port for control stream (use -1 for the default port)
     *  @param  timeout       the length of the timeout, in milliseconds
     *                        (pass in 0 for no timeout)
     *  @deprecated  use setter methods to set properties
     */
    public FTPClient(String remoteHost, int controlPort, int timeout)
    throws IOException, FTPException {

        this(InetAddress.getByName(remoteHost), controlPort, timeout);
    }

    /**
     *  Constructor. Creates the control
     *  socket
     *
     *  @param   remoteHost  the remote hostname
     *  @param   controlPort  port for control stream (use -1 for the default port)
     *  @param  timeout       the length of the timeout, in milliseconds
     *                        (pass in 0 for no timeout)
     *  @param   encoding         character encoding used for data
     *  @deprecated  use setter methods to set properties
     */
    public FTPClient(String remoteHost, int controlPort, int timeout, String encoding)
        throws IOException, FTPException {

        this(InetAddress.getByName(remoteHost), controlPort, timeout, encoding);
    }

    /**
     *  Constructor. Creates the control
     *  socket
     *
     *  @param   remoteAddr  the address of the
     *                       remote host
     *  @deprecated  use setter methods to set properties
     */
    public FTPClient(InetAddress remoteAddr)
        throws IOException, FTPException {

		this(remoteAddr, FTPControlSocket.CONTROL_PORT, 0);
    }
    

    /**
     *  Constructor. Creates the control
     *  socket. Allows setting of control port (normally
     *  set by default to 21).
     *
     *  @param   remoteAddr  the address of the
     *                       remote host
     *  @param   controlPort  port for control stream
     *  @deprecated  use setter methods to set properties
     */
    public FTPClient(InetAddress remoteAddr, int controlPort)
        throws IOException, FTPException {

		this(remoteAddr, controlPort, 0);
    }

    /**
     *  Constructor. Creates the control
     *  socket. Allows setting of control port (normally
     *  set by default to 21).
     *
     *  @param   remoteAddr    the address of the
     *                          remote host
     *  @param   controlPort   port for control stream (-1 for default port)
     *  @param  timeout        the length of the timeout, in milliseconds 
     *                         (pass in 0 for no timeout)
     *  @deprecated  use setter methods to set properties
     */
    public FTPClient(InetAddress remoteAddr, int controlPort, int timeout)
        throws IOException, FTPException {
        if (controlPort < 0)
            controlPort = FTPControlSocket.CONTROL_PORT;
		initialize(new FTPControlSocket(remoteAddr, controlPort, timeout, DEFAULT_ENCODING, null));
    }
    
    /**
     *  Constructor. Creates the control
     *  socket. Allows setting of control port (normally
     *  set by default to 21).
     *
     *  @param   remoteAddr    the address of the
     *                          remote host
     *  @param   controlPort   port for control stream (-1 for default port)
     *  @param   timeout        the length of the timeout, in milliseconds 
     *                         (pass in 0 for no timeout)
     *  @param   encoding         character encoding used for data
     *  @deprecated  use setter methods to set properties
     */
    public FTPClient(InetAddress remoteAddr, int controlPort, int timeout, String encoding)
        throws IOException, FTPException {
        if (controlPort < 0)
            controlPort = FTPControlSocket.CONTROL_PORT;
        initialize(new FTPControlSocket(remoteAddr, controlPort, timeout, encoding, null));
    }
    
    /**
     *  Default constructor should now always be used together with setter methods
     *  in preference to other constructors (now deprecated). The {@link #connect()}
     *  method is used to perform the actual connection to the remote host - but only
     *  for this constructor. Deprecated constructors connect in the constructor and
     *  connect() is not required (and cannot be called).
     */
    public FTPClient() {
    }
    
    /**
     * Connects to the server at the address and port number defined
     * in the constructor. Must be performed <b>before</b> login() or user() is
     * called.
     * 
     * @throws IOException Thrown if there is a TCP/IP-related error.
     * @throws FTPException Thrown if there is an error related to the FTP protocol. 
     */
    public void connect() throws IOException, FTPException {

        checkConnection(false);
       
        log.debug("Connecting to " + remoteAddr + ":" + controlPort);
        
        control = new FTPControlSocket(remoteAddr, controlPort, timeout, 
                                       controlEncoding, messageListener);
    }
    
    /**
     * Is this client connected?
     * 
     * @return true if connected, false otherwise
     */
    public boolean connected() {
        return control != null;
    }
    
    /**
     * Checks if the client has connected to the server and throws an exception if it hasn't.
     * This is only intended to be used by subclasses
     * 
     * @throws FTPException Thrown if the client has not connected to the server.
     */
    protected void checkConnection(boolean shouldBeConnected) throws FTPException {
    	if (shouldBeConnected && !connected())
    		throw new FTPException("The FTP client has not yet connected to the server.  "
    				+ "The requested action cannot be performed until after a connection has been established.");
    	else if (!shouldBeConnected && connected())
    		throw new FTPException("The FTP client has already been connected to the server.  "
    				+"The requested action must be performed before a connection is established.");
    }
    	
    /**
     * Set the control socket explicitly
     * 
     * @param control   control socket reference
     */
	protected void initialize(FTPControlSocket control) throws IOException {
		this.control = control;
        control.setMessageListener(messageListener);
        control.setStrictReturnCodes(strictReturnCodes);
        control.setTimeout(timeout);
	}
    
    /**
     *  Switch debug of responses on or off
     *
     *  @param  on  true if you wish to have responses to
     *              the log stream, false otherwise
     *  @deprecated  use the Logger class to switch debugging on and off
     */
    public void debugResponses(boolean on) {
        if (on)
            Logger.setLevel(Level.DEBUG);
        else
            Logger.setLevel(Level.OFF);
    }    
    
    /**
     * Set strict checking of FTP return codes. If strict 
     * checking is on (the default) code must exactly match the expected 
     * code. If strict checking is off, only the first digit must match.
     * 
     * @param strict    true for strict checking, false for loose checking
     */
    public void setStrictReturnCodes(boolean strict) {
        this.strictReturnCodes = strict;
        if (control != null)
            control.setStrictReturnCodes(strict);
    }
    
    /**
     * Determine if strict checking of return codes is switched on. If it is 
     * (the default), all return codes must exactly match the expected code.  
     * If strict checking is off, only the first digit must match.
     * 
     * @return  true if strict return code checking, false if non-strict.
     */
    public boolean isStrictReturnCodes() {
        return strictReturnCodes;
    }

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
    public void setTimeout(int millis)
        throws IOException {

        this.timeout = millis;
        if (control != null)
            control.setTimeout(millis);
    }
    
 
    /**
     *  Get the TCP timeout 
     *  
     *  @return timeout that is used, in milliseconds
     */
    public int getTimeout() {
        return timeout;
    }
    
    /**
     * @return Returns the controlPort.
     */
    public int getControlPort() {
        return controlPort;
    }
    
    /** 
     * Set the control socket's port. Can only do this if
     * not connected
     * 
     * @param controlPort The controlPort to set. 
     * @throws FTPException
     */
    public void setControlPort(int controlPort) throws FTPException {
        checkConnection(false);
        this.controlPort = controlPort;
    }
    
    /**
     * @return Returns the remoteAddr.
     */
    public InetAddress getRemoteAddr() {
        return remoteAddr;
    }
    
    /**
     * Set the remote address
     * 
     * @param remoteAddr The remoteAddr to set.
     * @throws FTPException
     */
    public void setRemoteAddr(InetAddress remoteAddr) throws FTPException {
        checkConnection(false);
        this.remoteAddr = remoteAddr;
        this.remoteHost = remoteAddr.getHostName();
    }
    
    /**
     * @return Returns the remoteHost.
     */
    public String getRemoteHost() {
        return remoteHost;
    }
    
    /**
     * Set the remote host
     * 
     * @param remoteHost The remoteHost to set.
     * @throws FTPException
     */
    public void setRemoteHost(String remoteHost) throws IOException, FTPException {
        checkConnection(false);
        this.remoteHost = remoteHost;
        this.remoteAddr = InetAddress.getByName(remoteHost);
    }
    
    
    /**
     * Get the encoding used for the control connection
     * 
     * @return Returns the current controlEncoding.
     */
    public String getControlEncoding() {
        return controlEncoding;
    }
    
    /**
     * Set the control socket's encoding. Can only do this if
     * not connected
     * 
     * @param controlEncoding The controlEncoding to set.
     * @throws FTPException
     */
    public void setControlEncoding(String controlEncoding) throws FTPException {
        checkConnection(false);
        this.controlEncoding = controlEncoding;
    }
    /**
     * @return Returns the messageListener.
     */
    public FTPMessageListener getMessageListener() {
        return messageListener;
    }
    
    /**
     * Set a listener that handles all FTP messages
     * 
     * @param listener  message listener
     */
    public void setMessageListener(FTPMessageListener listener) {
        this.messageListener = listener;
        if (control != null)
           control.setMessageListener(listener);
    }
    
    /**
     *  Set the connect mode
     *
     *  @param  mode  ACTIVE or PASV mode
     */
    public void setConnectMode(FTPConnectMode mode) {
        connectMode = mode;
    }
    
    /**
     * @return Returns the connectMode.
     */
    public FTPConnectMode getConnectMode() {
        return connectMode;
    }
    
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
    public void setProgressMonitor(FTPProgressMonitor monitor, long interval) {
        this.monitor = monitor;
        this.monitorInterval = interval;
    }    
    
    /**
     *  Set a progress monitor for callbacks. Uses default callback
     *  interval
     *
     *  @param  monitor   the monitor object
     */
    public void setProgressMonitor(FTPProgressMonitor monitor) {
        this.monitor = monitor;
    }    
        
    /**
     *  Get the bytes transferred between each callback on the
     *  progress monitor
     * 
     * @return long     bytes to be transferred before a callback
     */
    public long getMonitorInterval() {
        return monitorInterval;
    }
    
    /**
     * Set the size of the buffers used in writing to and reading from
     * the data sockets
     * 
     * @param size  new size of buffer in bytes
     */
    public void setTransferBufferSize(int size) {
        transferBufferSize = size;
    }
    
    /**
     * Get the size of the buffers used in writing to and reading from
     * the data sockets
     * 
     * @return  transfer buffer size
     */
    public int getTransferBufferSize() {
        return transferBufferSize;
    }
    
    /**
     *  Cancels the current transfer. Generally called from a separate
     *  thread. Note that this may leave partially written files on the
     *  server or on local disk, and should not be used unless absolutely
     *  necessary. The server is not notified
     */
    public void cancelTransfer() {
        cancelTransfer = true;
    } 
    
    /**
     * We can force PORT to send a fixed IP address, which can be useful with certain
     * NAT configurations. Must be connected to the remote host to call this method.
     * 
     * @param IPAddress     IP address to force, in 192.168.1.0 form
     * @deprecated
     */
    public void setPORTIP(String IPAddress) 
        throws FTPException {
        setActiveIPAddress(IPAddress);
    }
    
    /**
     * We can force PORT to send a fixed IP address, which can be useful with certain
     * NAT configurations. Must be connected to the remote host to call this method.
     * 
     * @param IPAddress     IP address to force, in 192.168.1.0 form
     */
    public void setActiveIPAddress(String IPAddress) 
        throws FTPException {
        
        checkConnection(true);
        
        byte PORT_IP[] = new byte[4];
        int len = IPAddress.length();
        int partCount = 0;
        StringBuffer buf = new StringBuffer();

        // loop thru and examine each char
        for (int i = 0; i < len && partCount <= 4; i++) {

            char ch = IPAddress.charAt(i);
            if (Character.isDigit(ch))
                buf.append(ch);
            else if (ch != '.') {
                throw new FTPException("Incorrectly formatted IP address: " + IPAddress);
            }

            // get the part
            if (ch == '.' || i+1 == len) { // at end or at separator
                try {
                    PORT_IP[partCount++] = (byte)Integer.parseInt(buf.toString());
                    buf.setLength(0);
                }
                catch (NumberFormatException ex) {
                    throw new FTPException("Incorrectly formatted IP address: " + IPAddress);
                }
            }
        }
        
        control.setActivePortIPAddress(PORT_IP);
    }
    
    
    /**
     * We can force PORT to send a fixed IP address, which can be useful with certain
     * NAT configurations. Must be connected to the remote host to call this method.
     * 
     * @param IPAddress     IP address to force, in 192.168.1.0 form
     */
    public void setActivePortRange(int lowest, int highest) 
        throws FTPException {
        
        checkConnection(true);
        
        if (lowest < 0 || lowest > highest || highest > MAX_PORT)
            throw new FTPException("Invalid port range specified");
        
        control.setActivePortRange(lowest, highest);
    }
    
       
    /**
     *  Login into an account on the FTP server. This
     *  call completes the entire login process. Note that
     *  connect() must be called first.
     *
     *  @param   user       user name
     *  @param   password   user's password
     */
    public void login(String user, String password)
        throws IOException, FTPException {
    	
    	checkConnection(true);
        
        user(user);

        if (lastValidReply.getReplyCode().equals("230"))
            return;
        else {
            password(password);
        }
    }
    
    /**
     *  Login into an account on the FTP server. This call completes the 
     *  entire login process. This method permits additional account information 
     *  to be supplied. FTP servers can use combinations of these parameters in 
     *  many different ways, e.g. to pass in proxy details via this method, some 
     *  servers use the "user" as 'ftpUser + "@" + ftpHost + " " + ftpProxyUser', 
     *  the "password" as the FTP user's password, and the accountInfo as the proxy 
     *  password. Note that connect() must be called first.
     *
     *  @param   user           user name
     *  @param   password       user's password
     *  @param   accountInfo    account info string
     */
    public void login(String user, String password, String accountInfo)
        throws IOException, FTPException {
        
        checkConnection(true);
        
        user(user);

        if (lastValidReply.getReplyCode().equals("230")) // no pwd
            return;
        else {
            password(password);
            if (lastValidReply.getReplyCode().equals("332")) // requires acct info
                account(accountInfo);
        }
    }    
    
    /**
     *  Supply the user name to log into an account
     *  on the FTP server. Must be followed by the
     *  password() method - but we allow for no password.
     *  Note that connect() must be called first.
     *
     *  @param   user       user name
     */
    public void user(String user)
        throws IOException, FTPException {
    	
    	checkConnection(true);
    	
        FTPReply reply = control.sendCommand("USER " + user);

        // we allow for a site with no password - 230 response
        String[] validCodes = {"230", "331"};
        lastValidReply = control.validateReply(reply, validCodes);
    }


    /**
     *  Supplies the password for a previously supplied
     *  username to log into the FTP server. Must be
     *  preceeded by the user() method
     *
     *  @param   password       The password.
     */
    public void password(String password)
        throws IOException, FTPException {
    	
    	checkConnection(true);
    	
        FTPReply reply = control.sendCommand("PASS " + password);

        // we allow for a site with no passwords (202) or requiring
        // ACCT info (332)
        String[] validCodes = {"230", "202", "332"};
        lastValidReply = control.validateReply(reply, validCodes);
    }
    
    
    /**
     *  Supply account information string to the server. This can be
     *  used for a variety of purposes - for example, the server could
     *  indicate that a password has expired (by sending 332 in reply to
     *  PASS) and a new password automatically supplied via ACCT. It
     *  is up to the server how it uses this string.
     *
     *  @param   accountInfo    account information string
     */
    public void account(String accountInfo)
        throws IOException, FTPException {
        
        checkConnection(true);
        
        FTPReply reply = control.sendCommand("ACCT " + accountInfo);

        // ok or not implemented
        String[] validCodes = {"230", "202"};
        lastValidReply = control.validateReply(reply, validCodes);
    }


    /**
     *  Set up SOCKS v4/v5 proxy settings. This can be used if there
     *  is a SOCKS proxy server in place that must be connected thru.
     *  Note that setting these properties directs <b>all</b> TCP
     *  sockets in this JVM to the SOCKS proxy
     *
     *  @param  port  SOCKS proxy port
     *  @param  host  SOCKS proxy hostname
     */
    public static void initSOCKS(String port, String host) {
        Properties props = System.getProperties();
        props.put(SOCKS_PORT, port);
        props.put(SOCKS_HOST, host);
        System.setProperties(props);
    }

    /**
     *  Set up SOCKS username and password for SOCKS username/password
     *  authentication. Often, no authentication will be required
     *  but the SOCKS server may be configured to request these.
     *
     *  @param  username   the SOCKS username
     *  @param  password   the SOCKS password
     */
    public static void initSOCKSAuthentication(String username,
                                               String password) {
        Properties props = System.getProperties();
        props.put("java.net.socks.username", username);
        props.put("java.net.socks.password", password);
        System.setProperties(props);
    }
    
    /**
     * Clear SOCKS settings. Note that setting these properties affects 
     * <b>all</b> TCP sockets in this JVM
     */
    public static void clearSOCKS() {
        
        Properties prop = System.getProperties(); 
        prop.remove(SOCKS_HOST); 
        prop.remove(SOCKS_PORT); 
        System.setProperties(prop); 
    }

    /**
     *  Get the name of the remote host
     *
     *  @return  remote host name
     */
    String getRemoteHostName() {
        return control.getRemoteHostName();
    }

    /**
     *  Issue arbitrary ftp commands to the FTP server.
     *
     *  @param command     ftp command to be sent to server
     *  @param validCodes  valid return codes for this command. If null
     *                      is supplied no validation is performed
     * 
     *  @return  the text returned by the FTP server
     */
    public String quote(String command, String[] validCodes)
        throws IOException, FTPException {
    	
    	checkConnection(true);
    	
        FTPReply reply = control.sendCommand(command);

        // allow for no validation to be supplied
        if (validCodes != null) {
            lastValidReply = control.validateReply(reply, validCodes);
        }
        else { // no validation
            lastValidReply = reply; // assume valid
        }
        return lastValidReply.getReplyText();       
    }


    /**
     *  Get the size of a remote file. This is not a standard FTP command, it
     *  is defined in "Extensions to FTP", a draft RFC 
     *  (draft-ietf-ftpext-mlst-16.txt)
     *
     *  @param  remoteFile  name or path of remote file in current directory
     *  @return size of file in bytes      
     */      
     public long size(String remoteFile)
         throws IOException, FTPException {
     	
     	 checkConnection(true);
     	
         FTPReply reply = control.sendCommand("SIZE " + remoteFile);
         lastValidReply = control.validateReply(reply, "213");

         // parse the reply string .
         String replyText = lastValidReply.getReplyText();
         
         // trim off any trailing characters after a space, e.g. webstar
         // responds to SIZE with 213 55564 bytes
         int spacePos = replyText.indexOf(' ');
         if (spacePos >= 0)
             replyText = replyText.substring(0, spacePos);
         
         // parse the reply
         try {
             return Long.parseLong(replyText);
         }
         catch (NumberFormatException ex) {
             throw new FTPException("Failed to parse reply: " + replyText);
         }         
     }
     
     /**
      * Make the next file transfer (put or get) resume. For puts(), the
      * bytes already transferred are skipped over, while for gets(), if 
      * writing to a file, it is opened in append mode, and only the bytes
      * required are transferred.
      * 
      * Currently resume is only supported for BINARY transfers (which is
      * generally what it is most useful for).
      * 
      * @throws FTPException
      */
     public void resume() throws FTPException {
         if (transferType.equals(FTPTransferType.ASCII))
             throw new FTPException("Resume only supported for BINARY transfers");
         resume = true;
     }
     
     /**
      * Cancel the resume. Use this method if something goes wrong
      * and the server is left in an inconsistent state
      * 
      * @throws IOException
      * @throws FTPException
      */
     public void cancelResume() 
         throws IOException, FTPException {
         restart(0);
         resume = false;
     }
     
     /**
      * Issue the RESTart command to the remote server 
      * 
      * @param size     the REST param, the mark at which the restart is 
      *                  performed on the remote file. For STOR, this is retrieved
      *                  by SIZE
      * @throws IOException
      * @throws FTPException
      */
     private void restart(long size) 
         throws IOException, FTPException {
         FTPReply reply = control.sendCommand("REST " + size);
         lastValidReply = control.validateReply(reply, "350");
     }


    /**
     *  Put a local file onto the FTP server. It
     *  is placed in the current directory.
     *
     *  @param  localPath   path of the local file
     *  @param  remoteFile  name of remote file in
     *                      current directory
     */
    public void put(String localPath, String remoteFile)
        throws IOException, FTPException {

        put(localPath, remoteFile, false);
    }

    /**
     *  Put a stream of data onto the FTP server. It
     *  is placed in the current directory.
     *
     *  @param  srcStream   input stream of data to put
     *  @param  remoteFile  name of remote file in
     *                      current directory
     */
    public void put(InputStream srcStream, String remoteFile)
        throws IOException, FTPException {

        put(srcStream, remoteFile, false);
    }


    /**
     *  Put a local file onto the FTP server. It
     *  is placed in the current directory. Allows appending
     *  if current file exists
     *
     *  @param  localPath   path of the local file
     *  @param  remoteFile  name of remote file in
     *                      current directory
     *  @param  append      true if appending, false otherwise
     */
    public void put(String localPath, String remoteFile,
                    boolean append)
        throws IOException, FTPException {
    	    	
        putData(localPath, remoteFile, append);
        validateTransfer();
     }

    /**
     *  Put a stream of data onto the FTP server. It
     *  is placed in the current directory. Allows appending
     *  if current file exists
     *
     *  @param  srcStream   input stream of data to put
     *  @param  remoteFile  name of remote file in
     *                      current directory
     *  @param  append      true if appending, false otherwise
     */
    public void put(InputStream srcStream, String remoteFile,
                    boolean append)
        throws IOException, FTPException {

        putData(srcStream, remoteFile, append);
        validateTransfer();
    }

    /**
     * Validate that the put() or get() was successful.  This method is not
     * for general use.
     */
    public void validateTransfer()
        throws IOException, FTPException {
    	
    	checkConnection(true);
    	
        // check the control response
        String[] validCodes = {"225", "226", "250", "426", "450"};
        FTPReply reply = control.readReply();
        
        // permit 426/450 error if we cancelled the transfer, otherwise
        // throw an exception
        String code = reply.getReplyCode();
        if ( (code.equals("426")||code.equals("450")) && !cancelTransfer )
            throw new FTPException(reply);
        
        lastValidReply = control.validateReply(reply, validCodes);
    }
    
    /**
     * Close the data socket
     */
    private void closeDataSocket() {
        if (data != null) {
            try {
                data.close();
                data = null;
            }
            catch (IOException ex) {
                log.warn("Caught exception closing data socket", ex);
            }
        }
    }
    
       
    /**
     * Close stream for data socket
     * 
     * @param stream    stream reference
     */
    private void closeDataSocket(InputStream stream) {
        if (stream != null) {
            try {
                stream.close();
            } 
            catch (IOException ex) {
                log.warn("Caught exception closing data socket", ex);
            }
        }

        closeDataSocket();
    }
    
    
    /**
     * Close stream for data socket
     * 
     * @param stream    stream reference
     */
    private void closeDataSocket(OutputStream stream) {
        if (stream != null) {
            try {
                stream.close();
            } 
            catch (IOException ex) {
                log.warn("Caught exception closing data socket", ex);
            }
        }

        closeDataSocket();
    }

    /**
     * Request the server to set up the put
     * 
     * @param remoteFile
     *            name of remote file in current directory
     * @param append
     *            true if appending, false otherwise
     */
    private void initPut(String remoteFile, boolean append)
        throws IOException, FTPException {
    	
    	checkConnection(true);
    	
        // reset the cancel flag
        cancelTransfer = false;

        boolean close = false;
        data = null;
        try {
            // set up data channel
            data = control.createDataSocket(connectMode);
            data.setTimeout(timeout);
            
            // if resume is requested, we must obtain the size of the
            // remote file and issue REST
            if (resume) {
                if (transferType.equals(FTPTransferType.ASCII))
                    throw new FTPException("Resume only supported for BINARY transfers");
                resumeMarker = size(remoteFile);
                restart(resumeMarker);
            }
    
            // send the command to store
            String cmd = append ? "APPE " : "STOR ";
            FTPReply reply = control.sendCommand(cmd + remoteFile);
    
            // Can get a 125 or a 150
            String[] validCodes1 = {"125", "150"};
            
            // for non-strict return codes, also allow 350 (for Global eXchange Services server)
            String[] validCodes2 = {"125", "150", "350"};
            
            String[] validCodes = strictReturnCodes ? validCodes1 : validCodes2;                        
            lastValidReply = control.validateReply(reply, validCodes);
        }
        catch (IOException ex) {
            close = true;
            throw ex;
        }
        catch (FTPException ex) {
            close = true;
            throw ex;
        }
        finally {
            if (close) {
                resume = false;
                closeDataSocket();
            }
        }
    }


    /**
     *  Put as binary, i.e. read and write raw bytes
     *
     *  @param localPath   full path of local file to read from
     *  @param remoteFile  name of remote file we are writing to
     *  @param  append      true if appending, false otherwise
     */
    private void putData(String localPath, String remoteFile,
                           boolean append)
        throws IOException, FTPException {

        // open input stream to read source file ... do this
        // BEFORE opening output stream to server, so if file not
        // found, an exception is thrown
        InputStream srcStream = new FileInputStream(localPath);
        putData(srcStream, remoteFile, append);
    }

    /**
     *  Put as binary, i.e. read and write raw bytes
     *
     *  @param  srcStream   input stream of data to put
     *  @param  remoteFile  name of remote file we are writing to
     *  @param  append      true if appending, false otherwise
     */
    private void putData(InputStream srcStream, String remoteFile,
                           boolean append)
        throws IOException, FTPException {

        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        long size = 0;
        try {
            in = new BufferedInputStream(srcStream);
    
            initPut(remoteFile, append);
    
            // get an output stream
            out = new BufferedOutputStream(
                    new DataOutputStream(data.getOutputStream()));
            
            // if resuming, we skip over the unwanted bytes
            if (resume) {
                in.skip(resumeMarker);
            }
    
            byte[] buf = new byte[transferBufferSize];
            byte[] prevBuf = null;
    
            // read a chunk at a time and write to the data socket            
            long monitorCount = 0;
            int count = 0;
            boolean isASCII = getType() == FTPTransferType.ASCII;
            int separatorPos = 0;
            
            while ((count = in.read(buf)) > 0 && !cancelTransfer) {
                if (isASCII) {
                    for (int i = 0; i < count; i++) {
                        // at each byte pos, check if there's a match along the line sep array  
                        boolean found = true;
                        int skip = 0;
                        for (; separatorPos < LINE_SEPARATOR.length && i+separatorPos < count; skip++,separatorPos++) {
                            if (buf[i+separatorPos] != LINE_SEPARATOR[separatorPos]) {
                                found = false;
                                break;
                            }
                        }
                        if (found) { // either found match or run out of buffer
                            if (separatorPos == LINE_SEPARATOR.length) { // found line separator
                                out.write(CARRIAGE_RETURN);
                                out.write(LINE_FEED);
                                size += 2;
                                monitorCount += 2;
                                separatorPos = 0;
                                // now must skip over bytes that match
                                i += (skip-1);  // we are already skipping current byte
                                prevBuf = null;
                            }
                            else { // reached end of buffer && are matching so far
                                   // Do nothing, we'll pick it up next time
                                // we need to save the bytes matched so far
                                prevBuf = new byte[skip];
                                for (int k = 0; k < skip; k++) {
                                    prevBuf[k] = buf[i+k];
                                }
                            }
                        }
                        else { // match failed, write out this byte && any prev ones from last buf
                            if (prevBuf != null) {
                                out.write(prevBuf);
                                size += prevBuf.length;
                                monitorCount += prevBuf.length;
                                prevBuf = null;
                            }
                            out.write(buf[i]);
                            size++;
                            monitorCount++;
                            separatorPos = 0;
                        }
                    }
                }
                else {
                    out.write(buf, 0, count);
                    size += count;
                    monitorCount += count;
                }
                
                // write out saved chunk if it exists
                if (prevBuf != null) {
                    out.write(prevBuf);
                    size += prevBuf.length;
                    monitorCount += prevBuf.length;
                }
                    
                if (monitor != null && monitorCount > monitorInterval) {
                    monitor.bytesTransferred(size); 
                    monitorCount = 0;  
                }
            }
        }
        finally {
            resume = false;
            try {
                if (in != null)
                    in.close();
            }
            catch (IOException ex) {
                log.warn("Caught exception closing input stream", ex);
            }
                
            closeDataSocket(out);
            
            // notify the final transfer size
            if (monitor != null)
                monitor.bytesTransferred(size);  
            // log bytes transferred
            log.debug("Transferred " + size + " bytes to remote host");           
        }
    }


    /**
     *  Put data onto the FTP server. It
     *  is placed in the current directory.
     *
     *  @param  bytes        array of bytes
     *  @param  remoteFile  name of remote file in
     *                      current directory
     */
    public void put(byte[] bytes, String remoteFile)
        throws IOException, FTPException {

        put(bytes, remoteFile, false);
    }

    /**
     *  Put data onto the FTP server. It
     *  is placed in the current directory. Allows
     *  appending if current file exists
     *
     *  @param  bytes        array of bytes
     *  @param  remoteFile  name of remote file in
     *                      current directory
     *  @param  append      true if appending, false otherwise
     */
    public void put(byte[] bytes, String remoteFile, boolean append)
        throws IOException, FTPException {
        
        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        put(input, remoteFile, append);
    }


    /**
     *  Get data from the FTP server. Uses the currently
     *  set transfer mode.
     *
     *  @param  localPath   local file to put data in
     *  @param  remoteFile  name of remote file in
     *                      current directory
     */
    public void get(String localPath, String remoteFile)
        throws IOException, FTPException {

        getData(localPath, remoteFile);
        validateTransfer();
    }

    /**
     *  Get data from the FTP server. Uses the currently
     *  set transfer mode.
     *
     *  @param  destStream  data stream to write data to
     *  @param  remoteFile  name of remote file in
     *                      current directory
     */
    public void get(OutputStream destStream, String remoteFile)
        throws IOException, FTPException {

        getData(destStream, remoteFile);
        validateTransfer();
    }


    /**
     *  Request to the server that the get is set up
     *
     *  @param  remoteFile  name of remote file
     */
    private void initGet(String remoteFile)
        throws IOException, FTPException {
    	
    	checkConnection(true);
    	
        // reset the cancel flag
        cancelTransfer = false;            

        boolean close = false;
        data = null;
        try {
            // set up data channel
            data = control.createDataSocket(connectMode);
            data.setTimeout(timeout);
            
            // if resume is requested, we must issue REST
            if (resume) {
                if (transferType.equals(FTPTransferType.ASCII))
                    throw new FTPException("Resume only supported for BINARY transfers");
                restart(resumeMarker);
            }
    
            // send the retrieve command
            FTPReply reply = control.sendCommand("RETR " + remoteFile);
    
            // Can get a 125 or a 150
            String[] validCodes1 = {"125", "150"};
            lastValidReply = control.validateReply(reply, validCodes1);
        }
        catch (IOException ex) {
            close = true;
            throw ex;
        }
        catch (FTPException ex) {
            close = true;
            throw ex;
        }
        finally {
            if (close) {
                resume = false;
                closeDataSocket();
            }
        }
    }
    

    /**
     *  Get as binary file, i.e. straight transfer of data
     *
     *  @param localPath   full path of local file to write to
     *  @param remoteFile  name of remote file
     */
    private void getData(String localPath, String remoteFile)
        throws IOException, FTPException {

        // B. McKeown: Need to store the local file name so the file can be
        // deleted if necessary.
        File localFile = new File(localPath);
        
        // if resuming, we must find the marker
        if (localFile.exists()) {
            if (!localFile.canWrite())
                throw new FTPException(localPath + " is readonly - cannot write");
            if (resume)
                resumeMarker = localFile.length();
        }           

        // B.McKeown:
        // Call initGet() before creating the FileOutputStream.
        // This will prevent being left with an empty file if a FTPException
        // is thrown by initGet().
        initGet(remoteFile);

        // create the buffered output stream for writing the file
        FileOutputStream out =
                new FileOutputStream(localPath, resume);
        
        try {
            getDataAfterInitGet(out);
        }
        catch (IOException ex) {
            localFile.delete();
            throw ex;
        }        
    }

    /**
     *  Get as binary file, i.e. straight transfer of data
     *
     *  @param destStream  stream to write to
     *  @param remoteFile  name of remote file
     */
    private void getData(OutputStream destStream, String remoteFile)
        throws IOException, FTPException {

        initGet(remoteFile);
        
        getDataAfterInitGet(destStream);
    }
    
        
    /**
     *  Get as binary file, i.e. straight transfer of data
     *
     *  @param destStream  stream to write to
     */
    private void getDataAfterInitGet(OutputStream destStream)
        throws IOException, FTPException {

        // create the buffered output stream for writing the file
        BufferedOutputStream out =
            new BufferedOutputStream(destStream);
        
        BufferedInputStream in = null;
        long size = 0;
        IOException storedEx = null;
        try {
            // get an input stream to read data from ... AFTER we have
            // the ok to go ahead AND AFTER we've successfully opened a
            // stream for the local file
            in = new BufferedInputStream(
                    new DataInputStream(data.getInputStream()));
    
            // B. McKeown:
            // If we are in active mode we have to set the timeout of the passive
            // socket. We can achieve this by calling setTimeout() again.
            // If we are in passive mode then we are merely setting the value twice
            // which does no harm anyway. Doing this simplifies any logic changes.
            data.setTimeout(timeout);
    
            // do the retrieving
            long monitorCount = 0; 
            byte [] chunk = new byte[transferBufferSize];
            int count;
            boolean isASCII = getType() == FTPTransferType.ASCII;
            boolean crFound = false;

            // read from socket & write to file in chunks        
            while ((count = readChunk(in, chunk, transferBufferSize)) >= 0 && !cancelTransfer) {
                if (isASCII) {
                    // transform CRLF
                    boolean lfFound = false;
                    for (int i = 0; i < count; i++) {
                        lfFound = chunk[i] == LINE_FEED;
                        // if previous is a CR, write it out if current is LF, otherwise
                        // write out the previous CR
                        if (crFound) {
                            if (lfFound) {
                               out.write(LINE_SEPARATOR, 0, LINE_SEPARATOR.length);
                               size += LINE_SEPARATOR.length;
                               monitorCount += LINE_SEPARATOR.length;
                            }
                            else {
                                // not CR LF so write out previous CR
                                out.write(CARRIAGE_RETURN); 
                                size++;
                                monitorCount++;
                            }                           
                        }
                        
                        // now check if current is CR
                        crFound = chunk[i] == CARRIAGE_RETURN;
                       
                        // if we didn't find a LF this time, write current byte out
                        // unless it is a CR - in that case save it
                        if (!lfFound && !crFound) {
                            out.write(chunk[i]);
                            size++;
                            monitorCount++;
                        }
                    }
                }
                else { // binary
                    out.write(chunk, 0, count);
                    size += count;
                    monitorCount += count;
                }
                
                if (monitor != null && monitorCount > monitorInterval) {
                    monitor.bytesTransferred(size); 
                    monitorCount = 0;  
                }                                                                        
            }
            // if asked to transfer, abort
            if (cancelTransfer)
                abort(); 
            
            // account for last byte if necessary
            if (isASCII && crFound) { 
                out.write(CARRIAGE_RETURN); 
                size++;
                monitorCount++;
            }
        }
        catch (IOException ex) {
            storedEx = ex;
        }
        finally {
            try {
                if (out != null)
                    out.close();
            }
            catch (IOException ex) {
                log.warn("Caught exception closing output stream", ex);
            }

            resume = false;
    
            // close streams
            closeDataSocket(in);
    
            // if we failed to write the file, rethrow the exception
            if (storedEx != null)
                throw storedEx;
            else if (monitor != null)
                monitor.bytesTransferred(size);  
    
            // log bytes transferred
            log.debug("Transferred " + size + " bytes from remote host");
        }
    }

    /**
     *  Get data from the FTP server. Transfers in
     *  whatever mode we are in. Retrieve as a byte array. Note
     *  that we may experience memory limitations as the
     *  entire file must be held in memory at one time.
     *
     *  @param  remoteFile  name of remote file in
     *                      current directory
     */
    public byte[] get(String remoteFile)
        throws IOException, FTPException {
        
        ByteArrayOutputStream result = new ByteArrayOutputStream(transferBufferSize);
        getData(result, remoteFile);
        validateTransfer();
        return result == null ? null : result.toByteArray();
    }


    /**
     *  Run a site-specific command on the
     *  server. Support for commands is dependent
     *  on the server
     *
     *  @param  command   the site command to run
     *  @return true if command ok, false if
     *          command not implemented
     */
    public boolean site(String command)
        throws IOException, FTPException {
    	
    	checkConnection(true);
    	
        // send the retrieve command
        FTPReply reply = control.sendCommand("SITE " + command);

        // Can get a 200 (ok) or 202 (not impl). Some
        // FTP servers return 502 (not impl)
        String[] validCodes = {"200", "202", "502"};
        lastValidReply = control.validateReply(reply, validCodes);

        // return true or false? 200 is ok, 202/502 not
        // implemented
        if (reply.getReplyCode().equals("200"))
            return true;
        else
            return false;
    }


    /**
     *  List a directory's contents
     *
     *  @param  dirname  the name of the directory (<b>not</b> a file mask)
     *  @return a string containing the line separated
     *          directory listing
     *  @deprecated  As of FTP 1.1, replaced by {@link #dir(String)}
     */
    public String list(String dirname)
        throws IOException, FTPException {

        return list(dirname, false);
    }


    /**
     *  List a directory's contents as one string. A detailed
     *  listing is available, otherwise just filenames are provided.
     *  The detailed listing varies in details depending on OS and
     *  FTP server.
     *
     *  @param  dirname  the name of the directory(<b>not</b> a file mask)
     *  @param  full     true if detailed listing required
     *                   false otherwise
     *  @return a string containing the line separated
     *          directory listing
     *  @deprecated  As of FTP 1.1, replaced by {@link #dir(String,boolean)}
     */
    public String list(String dirname, boolean full)
        throws IOException, FTPException {

        String[] list = dir(dirname, full);

        StringBuffer result = new StringBuffer();
        String sep = System.getProperty("line.separator");

        // loop thru results and make into one string
        for (int i = 0; i < list.length; i++) {
            result.append(list[i]);
            result.append(sep);
        }

        return result.toString();
    }
    
    
    /**
     * Override the chosen file factory with a user created one - meaning
     * that a specific parser has been selected
     * 
     * @param fileFactory
     */
    public void setFTPFileFactory(FTPFileFactory fileFactory) {
        this.fileFactory = fileFactory;
    }
    
    /**
     * Set the locale for date parsing of dir listings
     * 
     * @param locale    new locale to use
     */
    public void setParserLocale(Locale locale) {
        listingLocale = locale;
    }
    
    /**
     *  List a directory's contents as an array of FTPFile objects.
     *  Should work for Windows and most Unix FTP servers - let us know
     *  about unusual formats (http://www.enterprisedt.com/forums/index.php)
     *
     *  @param   dirname  name of directory OR filemask
     *  @return  an array of FTPFile objects
     */
    public FTPFile[] dirDetails(String dirname) 
        throws IOException, FTPException, ParseException {
        
        // create the factory
        if (fileFactory == null)
            fileFactory = new FTPFileFactory(system());
        fileFactory.setLocale(listingLocale);
                
        // get the details and parse
        return fileFactory.parse(dir(dirname, true));
    }

    /**
     *  List current directory's contents as an array of strings of
     *  filenames.
     *
     *  @return  an array of current directory listing strings
     */
    public String[] dir()
        throws IOException, FTPException {

        return dir(null, false);
    }

    /**
     *  List a directory's contents as an array of strings of filenames.
     *
     *  @param   dirname  name of directory OR filemask
     *  @return  an array of directory listing strings
     */
    public String[] dir(String dirname)
        throws IOException, FTPException {

        return dir(dirname, false);
    }


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
        throws IOException, FTPException {
    	
    	checkConnection(true);
        
        try {
            // set up data channel
            data = control.createDataSocket(connectMode);
            data.setTimeout(timeout);
    
            // send the retrieve command
            String command = full ? "LIST ":"NLST ";
            if (dirname != null)
                command += dirname;
    
            // some FTP servers bomb out if NLST has whitespace appended
            command = command.trim();
            FTPReply reply = control.sendCommand(command);
    
            // check the control response. wu-ftp returns 550 if the
            // directory is empty, so we handle 550 appropriately. Similarly
            // proFTPD returns 450
            String[] validCodes1 = {"125", "150", "450", "550"};
            lastValidReply = control.validateReply(reply, validCodes1);  
    
            // an empty array of files for 450/550
            String[] result = new String[0];
            
            // a normal reply ... extract the file list
            String replyCode = lastValidReply.getReplyCode();
            if (!replyCode.equals("450") && !replyCode.equals("550")) {
    			// get a character input stream to read data from .
    			LineNumberReader in = null;
                Vector lines = new Vector();    
                try {
    				in = new LineNumberReader(
    					    new InputStreamReader(data.getInputStream()));
    
        			// read a line at a time
        			String line = null;
        			while ((line = readLine(in)) != null) {
                        lines.addElement(line);
        			}
                }
                finally {
                    try {
                        if (in != null)
                            in.close();
                    }
                    catch (IOException ex) {
                        log.error("Failed to close socket in dir()", ex);
                    }
                    closeDataSocket();
                }
                    
                // check the control response
                String[] validCodes2 = {"226", "250"};
                reply = control.readReply();
                lastValidReply = control.validateReply(reply, validCodes2);
    
                // empty array is default
                if (!lines.isEmpty()) {
                    result = new String[lines.size()];
                    lines.copyInto(result);
                }
            }
            return result;
        }
        finally {
            closeDataSocket();
        }        
    }

    /**
     * Attempts to read a specified number of bytes from the given 
     * <code>InputStream</code> and place it in the given byte-array.
     * The purpose of this method is to permit subclasses to execute
     * any additional code necessary when performing this operation. 
     * @param in The <code>InputStream</code> to read from.
     * @param chunk The byte-array to place read bytes in.
     * @param chunksize Number of bytes to read.
     * @return Number of bytes actually read.
     * @throws IOException Thrown if there was an error while reading.
     */
    protected int readChunk(BufferedInputStream in, byte[] chunk, int chunksize) 
    	throws IOException {
    		
    	return in.read(chunk, 0, chunksize);
    }
    
    /**
     * Attempts to read a single character from the given <code>InputStream</code>. 
     * The purpose of this method is to permit subclasses to execute
     * any additional code necessary when performing this operation. 
     * @param in The <code>LineNumberReader</code> to read from.
     * @return The character read.
     * @throws IOException Thrown if there was an error while reading.
     */
    protected int readChar(LineNumberReader in) 
    	throws IOException {
    		
    	return in.read();
    }
    
    /**
     * Attempts to read a single line from the given <code>InputStream</code>. 
     * The purpose of this method is to permit subclasses to execute
     * any additional code necessary when performing this operation. 
     * @param in The <code>LineNumberReader</code> to read from.
     * @return The string read.
     * @throws IOException Thrown if there was an error while reading.
     */
    protected String readLine(LineNumberReader in) 
    	throws IOException {
    		
    	return in.readLine();
    }

    /**
     *  Gets the latest valid reply from the server
     *
     *  @return  reply object encapsulating last valid server response
     */
    public FTPReply getLastValidReply() {
        return lastValidReply;
    }


    /**
     *  Get the current transfer type
     *
     *  @return  the current type of the transfer,
     *           i.e. BINARY or ASCII
     */
    public FTPTransferType getType() {
        return transferType;
    }

    /**
     *  Set the transfer type
     *
     *  @param  type  the transfer type to
     *                set the server to
     */
    public void setType(FTPTransferType type)
        throws IOException, FTPException {
    	
    	checkConnection(true);
    	
        // determine the character to send
        String typeStr = FTPTransferType.ASCII_CHAR;
        if (type.equals(FTPTransferType.BINARY))
            typeStr = FTPTransferType.BINARY_CHAR;

        // send the command
        FTPReply reply = control.sendCommand("TYPE " + typeStr);
        lastValidReply = control.validateReply(reply, "200");

        // record the type
        transferType = type;
    }


    /**
     *  Delete the specified remote file
     *
     *  @param  remoteFile  name of remote file to
     *                      delete
     */
    public void delete(String remoteFile)
        throws IOException, FTPException {
    	
    	checkConnection(true);
        String[] validCodes = {"200", "250"};
        FTPReply reply = control.sendCommand("DELE " + remoteFile);
        lastValidReply = control.validateReply(reply, validCodes);
    }


    /**
     *  Rename a file or directory
     *
     * @param from  name of file or directory to rename
     * @param to    intended name
     */
    public void rename(String from, String to)
        throws IOException, FTPException {
    	
    	checkConnection(true);
    	
        FTPReply reply = control.sendCommand("RNFR " + from);
        lastValidReply = control.validateReply(reply, "350");

        reply = control.sendCommand("RNTO " + to);
        lastValidReply = control.validateReply(reply, "250");
    }


    /**
     *  Delete the specified remote working directory
     *
     *  @param  dir  name of remote directory to
     *               delete
     */
    public void rmdir(String dir)
        throws IOException, FTPException {
    	
    	checkConnection(true);
    	
        FTPReply reply = control.sendCommand("RMD " + dir);

        // some servers return 200,257, technically incorrect but
        // we cater for it ...
        String[] validCodes = {"200", "250", "257"};
        lastValidReply = control.validateReply(reply, validCodes);
    }


    /**
     *  Create the specified remote working directory
     *
     *  @param  dir  name of remote directory to
     *               create
     */
    public void mkdir(String dir)
        throws IOException, FTPException {
    	
    	checkConnection(true);
    	
        FTPReply reply = control.sendCommand("MKD " + dir);
        
        // some servers return 200,257, technically incorrect but
        // we cater for it ...
        String[] validCodes = {"200", "250", "257"};
        lastValidReply = control.validateReply(reply, validCodes);
    }


    /**
     *  Change the remote working directory to
     *  that supplied
     *
     *  @param  dir  name of remote directory to
     *               change to
     */
    public void chdir(String dir)
        throws IOException, FTPException {
    	
    	checkConnection(true);
    	
        FTPReply reply = control.sendCommand("CWD " + dir);
        lastValidReply = control.validateReply(reply, "250");
    }
    
    /**
     *  Change the remote working directory to
     *  the parent directory
     */
    public void cdup()
        throws IOException, FTPException {
        
        checkConnection(true);
        
        FTPReply reply = control.sendCommand("CDUP");
        String[] validCodes = {"200", "250"};
        lastValidReply = control.validateReply(reply, validCodes);
    }

    /**
     *  Get modification time for a remote file
     *
     *  @param    remoteFile   name of remote file
     *  @return   modification time of file as a date (in local time)
     */
    public Date modtime(String remoteFile)
        throws IOException, FTPException {
    	
    	checkConnection(true);
    	
        FTPReply reply = control.sendCommand("MDTM " + remoteFile);
        lastValidReply = control.validateReply(reply, "213");

        // parse the reply string ...
        Date ts = tsFormat.parse(lastValidReply.getReplyText(),
                                 new ParsePosition(0));
        return ts;
    }

    /**
     *  Get the current remote working directory
     *
     *  @return   the current working directory
     */
    public String pwd()
        throws IOException, FTPException {
    	
    	checkConnection(true);
    	
        FTPReply reply = control.sendCommand("PWD");
        lastValidReply = control.validateReply(reply, "257");

        // get the reply text and extract the dir
        // listed in quotes, if we can find it. Otherwise
        // just return the whole reply string
        String text = lastValidReply.getReplyText();
        int start = text.indexOf('"');
        int end = text.lastIndexOf('"');
        if (start >= 0 && end > start)
            return text.substring(start+1, end);
        else
            return text;
    }
    
    
    /**
     *  Get the server supplied features
     *
     *  @return   string containing server features, or null if no features or not
     *             supported
     */
    public String[] features()
        throws IOException, FTPException {
        
        checkConnection(true);
        
        FTPReply reply = control.sendCommand("FEAT");
        String[] validCodes = {"211", "500", "502"};
        lastValidReply = control.validateReply(reply, validCodes);
        if (lastValidReply.getReplyCode().equals("211"))
            return lastValidReply.getReplyData();
        else
            throw new FTPException(reply);
    }
    
    /**
     *  Get the type of the OS at the server
     *
     *  @return   the type of server OS
     */
    public String system()
        throws IOException, FTPException {
    	
    	checkConnection(true);
    	
        FTPReply reply = control.sendCommand("SYST");
        String[] validCodes = {"200", "215"};
        lastValidReply = control.validateReply(reply, validCodes);
        return lastValidReply.getReplyText();
    }
    
    
    /**
     *  Send a "no operation" message that does nothing. Can be
     *  called periodically to prevent the connection timing out
     */
    public void noOperation()
        throws IOException, FTPException {
        
        checkConnection(true);
        
        FTPReply reply = control.sendCommand("NOOP");
        lastValidReply = control.validateReply(reply, "200");
    }

    /**
     *  Get the help text for the specified command
     *
     *  @param  command  name of the command to get help on
     *  @return help text from the server for the supplied command
     */
    public String help(String command)
        throws IOException, FTPException {
    	
    	checkConnection(true);
    	
        FTPReply reply = control.sendCommand("HELP " + command);
        String[] validCodes = {"211", "214"};
        lastValidReply = control.validateReply(reply, validCodes);
        return lastValidReply.getReplyText();
    }
    
     /**
     *  Abort the current action
     */
    protected void abort()
        throws IOException, FTPException {
        
        checkConnection(true);
        
        FTPReply reply = control.sendCommand("ABOR");
        String[] validCodes = {"426", "226"};
        lastValidReply = control.validateReply(reply, validCodes);
    }

    /**
     *  Quit the FTP session
     *
     */
    public void quit()
        throws IOException, FTPException {
    	
    	checkConnection(true);
    	
        fileFactory = null;
        try {
            FTPReply reply = control.sendCommand("QUIT");
            String[] validCodes = {"221", "226"};
            lastValidReply = control.validateReply(reply, validCodes);
        }
        finally { // ensure we clean up the connection
            control.logout();
            control = null;
        }
    }
}


