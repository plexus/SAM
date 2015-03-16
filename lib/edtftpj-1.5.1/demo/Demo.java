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
 *  Bug fixes, suggestions and comments should be sent to support@enterprisedt.com
 *
 *  Change Log:
 *
 *    $Log: Demo.java,v $
 *    Revision 1.1  2005/08/28 13:40:11  plexus
 *    *** empty log message ***
 *
 *    Revision 1.6  2005/03/18 11:12:56  bruceb
 *    deprecated constructors
 *
 *    Revision 1.5  2005/02/04 12:30:26  bruceb
 *    print stack trace using logger
 *
 *    Revision 1.4  2004/06/25 12:34:55  bruceb
 *    logging added
 *
 *    Revision 1.3  2004/05/22 16:53:34  bruceb
 *    message listener added
 *
 *    Revision 1.2  2004/05/01 16:04:08  bruceb
 *    removed log stuff
 *
 *    Revision 1.1  2004/05/01 11:40:46  bruceb
 *    demo files
 *
 *
 */

import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.FTPMessageCollector;
import com.enterprisedt.net.ftp.FTPTransferType;
import com.enterprisedt.net.ftp.FTPConnectMode;
import com.enterprisedt.util.debug.Level;
import com.enterprisedt.util.debug.Logger;

/**
 *  Simple test class for FTPClient
 *
 *  @author      Hans Andersen
 *  @author      Bruce Blackshaw
 */
public class Demo {
    
    /**
     *  Revision control id
     */
    private static String cvsId = "@(#)$Id: Demo.java,v 1.1 2005/08/28 13:40:11 plexus Exp $";
    
    /**
     *  Log stream
     */
    private static Logger log = Logger.getLogger(Demo.class);

    /**
     * Standard main()
     * 
     * @param args  standard args
     */
    public static void main(String[] args) {
               
        // we want remote host, user name and password
        if (args.length < 3) {
            usage();
            System.exit(1);
        }

        // assign args to make it clear
        String host = args[0];
        String user = args[1];
        String password = args[2];
        
        Logger.setLevel(Level.ALL);

        FTPClient ftp = null;

        try {
            // set up client    
            ftp = new FTPClient();
            ftp.setRemoteHost(host);
            FTPMessageCollector listener = new FTPMessageCollector();
            ftp.setMessageListener(listener);
            
            // connect
            log.info("Connecting");
            ftp.connect();
            
             // login
            log.info("Logging in");
            ftp.login(user, password);

            // set up passive ASCII transfers
            log.debug("Setting up passive, ASCII transfers");
            ftp.setConnectMode(FTPConnectMode.PASV);
            ftp.setType(FTPTransferType.ASCII);

            // get directory and print it to console            
            log.debug("Directory before put:");
            String[] files = ftp.dir(".", true);
            for (int i = 0; i < files.length; i++)
                log.debug(files[i]);

            // copy file to server 
            log.info("Putting file");
            ftp.put("test.txt", "test.txt");

            // get directory and print it to console            
            log.debug("Directory after put");
            files = ftp.dir(".", true);
            for (int i = 0; i < files.length; i++)
                log.debug(files[i]);

            // copy file from server
            log.info("Getting file");
            ftp.get("test.txt" + ".copy", "test.txt");

            // delete file from server
            log.info("Deleting file");
            ftp.delete("test.txt");

            // get directory and print it to console            
            log.debug("Directory after delete");
            files = ftp.dir("", true);
            for (int i = 0; i < files.length; i++)
                log.debug(files[i]);

            // Shut down client                
            log.info("Quitting client");
            ftp.quit();
            
            String messages = listener.getLog();
            log.debug("Listener log:");
            log.debug(messages);

            log.info("Test complete");
        } catch (Exception e) {
            log.error("Demo failed", e);
        }
    }   
    
    /**
     *  Basic usage statement
     */
    public static void usage() {

        System.out.println("Usage: Demo remotehost user password");
    }

}
