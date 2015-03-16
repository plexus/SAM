/**
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
 *        $Log: FTPActiveDataSocket.java,v $
 *        Revision 1.1  2005/08/28 13:40:11  plexus
 *        *** empty log message ***
 *
 *        Revision 1.5  2005/06/03 11:26:25  bruceb
 *        comment change
 *
 *        Revision 1.4  2004/07/23 08:27:03  bruceb
 *        made public cvsId
 *
 *        Revision 1.3  2003/11/15 11:23:55  bruceb
 *        changes required for ssl subclasses
 *
 *        Revision 1.1  2003/11/02 21:49:52  bruceb
 *        implement FTPDataSocket interface
 *
 *
 */
package com.enterprisedt.net.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *  Active data socket handling class
 *
 *  @author      Bruce Blackshaw
 *  @version     $Revision: 1.1 $
 */
public class FTPActiveDataSocket implements FTPDataSocket {

    /**
     *  Revision control id
     */
    public static String cvsId = "@(#)$Id: FTPActiveDataSocket.java,v 1.1 2005/08/28 13:40:11 plexus Exp $";
    
    /**
     *  The underlying socket for Active connection.
     */
    protected ServerSocket sock = null;
    
    /**
     *  The socket accepted from server.
     */
    protected Socket acceptedSock = null;

    /**
     *  Constructor
     *  
     *  @param sock    the server socket to use
     */
    protected FTPActiveDataSocket(ServerSocket sock) {
         this.sock = sock;
    }

    /**
     *   Set the TCP timeout on the underlying data socket(s).
     *
     *   If a timeout is set, then any operation which
     *   takes longer than the timeout value will be
     *   killed with a java.io.InterruptedException.
     *
     *   @param millis The length of the timeout, in milliseconds
     */
    public void setTimeout(int millis) throws IOException {
        sock.setSoTimeout(millis);
        if (acceptedSock != null)
            acceptedSock.setSoTimeout(millis);
    }
    
    /**
     * Returns the local port to which this socket is bound. 
     * 
     * @return the local port number to which this socket is bound
     */
    public int getLocalPort() {
        return sock.getLocalPort();
    }
    
    /**
     * Waits for a connection from the server and then sets the timeout
     * when the connection is made.
     * @throws IOException There was an error while waiting for or accepting
     *                     a connection from the server.
     */
    protected void acceptConnection() throws IOException {
		if (acceptedSock==null) {
			acceptedSock = sock.accept();
			acceptedSock.setSoTimeout(sock.getSoTimeout());
		}
    }

    /**
     *  If active mode, accepts the FTP server's connection - in PASV,
     *  we are already connected. Then gets the output stream of
     *  the connection
     *
     *  @return  output stream for underlying socket.
     */
    public OutputStream getOutputStream() throws IOException {
        // accept socket from server
        acceptConnection();
        return acceptedSock.getOutputStream ();
     }

    /**
     *  If active mode, accepts the FTP server's connection - in PASV,
     *  we are already connected. Then gets the input stream of
     *  the connection
     *
     *  @return  input stream for underlying socket.
     */
    public InputStream getInputStream() throws IOException {
        // accept socket from server
		acceptConnection();
        return acceptedSock.getInputStream();
    }

     /**
      *  Closes underlying sockets
      */
    public void close() throws IOException {
        if (acceptedSock != null) {
            acceptedSock.close();
			acceptedSock = null;
		}
        sock.close();
    }
}
