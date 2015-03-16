/**
 * 
 *  Copyright (C) 2000-2004 Enterprise Distributed Technologies Ltd
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
 *  Bug fixes, suggestions and comments should be sent to bruce@enterprisedt.com
 *
 *  Change Log:
 *
 *    $Log: StandardOutputAppender.java,v $
 *    Revision 1.1  2005/08/28 13:40:11  plexus
 *    *** empty log message ***
 *
 *    Revision 1.2  2004/08/16 21:08:08  bruceb
 *    made cvsids public
 *
 *    Revision 1.1  2004/05/15 22:35:44  bruceb
 *    stdout appender
 *
 *    Revision 1.1  2004/05/01 16:55:42  bruceb
 *    first cut
 *
 *
 */
package com.enterprisedt.util.debug;

import java.io.PrintWriter;

/**
 *  Appends log statements to standard output
 *
 *  @author      Bruce Blackshaw
 *  @version     $Revision: 1.1 $
 */
public class StandardOutputAppender implements Appender {
    
    /**
     *  Revision control id
     */
    public static String cvsId = "@(#)$Id: StandardOutputAppender.java,v 1.1 2005/08/28 13:40:11 plexus Exp $";

    /**
     * Destination
     */
    private PrintWriter log = new PrintWriter(System.out);
    
    /**
     * Constructor
     */
    public StandardOutputAppender() {}
    
    /**
     * Log a message
     * 
     * @param msg  message to log
     */
    public void log(String msg) {
        log.println(msg);
    }
    
    /* (non-Javadoc)
     * @see com.enterprisedt.util.debug.Appender#log(java.lang.Throwable)
     */
    public void log(Throwable t) {
        t.printStackTrace(log);
        
    }

    /* (non-Javadoc)
     * @see com.enterprisedt.util.debug.Appender#close()
     */
    public void close() {
        log.flush();
    }
}
