/**
 *
 *  Java FTP client library.
 *
 *  Copyright (C) 2000-2003 Enterprise Distributed Technologies Ltd
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
 *    $Log: FTPException.java,v $
 *    Revision 1.1  2005/08/28 13:40:11  plexus
 *    *** empty log message ***
 *
 *    Revision 1.6  2005/06/03 11:26:25  bruceb
 *    comment change
 *
 *    Revision 1.5  2004/07/23 08:27:43  bruceb
 *    new constructor
 *
 *    Revision 1.4  2002/11/19 22:01:25  bruceb
 *    changes for 1.2
 *
 *    Revision 1.3  2001/10/09 20:54:08  bruceb
 *    No change
 *
 *    Revision 1.1  2001/10/05 14:42:04  bruceb
 *    moved from old project
 *
 */

package com.enterprisedt.net.ftp;

/**
 *  FTP specific exceptions
 *
 *  @author     Bruce Blackshaw
 *  @version    $Revision: 1.1 $
 *
 */
 public class FTPException extends Exception {

    /**
     *  Revision control id
     */
    public static String cvsId = "@(#)$Id: FTPException.java,v 1.1 2005/08/28 13:40:11 plexus Exp $";


    /**
     *  Integer reply code
     */
    private int replyCode = -1;

    /**
     *   Constructor. Delegates to super.
     *
     *   @param   msg   Message that the user will be
     *                  able to retrieve
     */
    public FTPException(String msg) {
        super(msg);
    }

    /**
     *  Constructor. Permits setting of reply code
     *
     *   @param   msg        message that the user will be
     *                       able to retrieve
     *   @param   replyCode  string form of reply code
     */
    public FTPException(String msg, String replyCode) {

        super(msg);

        // extract reply code if possible
        try {
            this.replyCode = Integer.parseInt(replyCode);
        }
        catch (NumberFormatException ex) {
            this.replyCode = -1;
        }
    }
    
    /**
     *  Constructor. Permits setting of reply code
     *
     *   @param   reply     reply object
     */
    public FTPException(FTPReply reply) {

        super(reply.getReplyText());

        // extract reply code if possible
        try {
            this.replyCode = Integer.parseInt(reply.getReplyCode());
        }
        catch (NumberFormatException ex) {
            this.replyCode = -1;
        }
    }    
    


    /**
     *   Get the reply code if it exists
     *
     *   @return  reply if it exists, -1 otherwise
     */
    public int getReplyCode() {
        return replyCode;
    }

}
