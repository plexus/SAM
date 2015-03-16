/**
 *
 *  edtFTPj
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
 *  Bug fixes, suggestions and comments should be should posted on 
 *  http://www.enterprisedt.com/forums/index.php
 *
 *  Change Log:
 *
 *    $Log: FTPFileFactory.java,v $
 *    Revision 1.1  2005/08/28 13:40:11  plexus
 *    *** empty log message ***
 *
 *    Revision 1.10  2005/06/10 15:43:41  bruceb
 *    more VMS tweaks
 *
 *    Revision 1.9  2005/06/03 11:26:05  bruceb
 *    VMS stuff
 *
 *    Revision 1.8  2005/04/01 13:57:35  bruceb
 *    added some useful debug
 *
 *    Revision 1.7  2004/10/19 16:15:16  bruceb
 *    swap to unix if seems like unix listing
 *
 *    Revision 1.6  2004/10/18 15:57:16  bruceb
 *    set locale
 *
 *    Revision 1.5  2004/08/31 10:45:50  bruceb
 *    removed unused import
 *
 *    Revision 1.4  2004/07/23 08:31:52  bruceb
 *    parser rotation
 *
 *    Revision 1.3  2004/05/01 11:44:21  bruceb
 *    modified for server returning "total 3943" as first line
 *
 *    Revision 1.2  2004/04/17 23:42:07  bruceb
 *    file parsing part II
 *
 *    Revision 1.1  2004/04/17 18:37:23  bruceb
 *    new parse functionality
 *
 */

package com.enterprisedt.net.ftp;

import java.text.ParseException;
import java.util.Locale;

import com.enterprisedt.util.debug.Logger;

/**
 *  Factory for creating FTPFile objects
 *
 *  @author      Bruce Blackshaw
 *  @version     $Revision: 1.1 $
 */
public class FTPFileFactory {
    
    /**
     *  Revision control id
     */
    public static String cvsId = "@(#)$Id: FTPFileFactory.java,v 1.1 2005/08/28 13:40:11 plexus Exp $";
    
    /**
     * Logging object
     */
    private Logger log = Logger.getLogger(FTPFileFactory.class);

    /**
     * Windows server comparison string
     */
    final static String WINDOWS_STR = "WINDOWS";
                  
    /**
     * UNIX server comparison string
     */
    final static String UNIX_STR = "UNIX";
    
    /**
     * VMS server comparison string
     */
    final static String VMS_STR = "VMS";
        
    /**
     * SYST string
     */
    private String system;
    
    /**
     * Cached windows parser
     */
    private FTPFileParser windows = new WindowsFileParser();
    
    /**
     * Cached unix parser
     */
    private FTPFileParser unix = new UnixFileParser();
    
    /**
     * Cached vms parser
     */
    private FTPFileParser vms = new VMSFileParser();
    
    /**
     * Does the parsing work
     */
    private FTPFileParser parser = null;
    
    /**
     * True if using VMS parser
     */
    private boolean usingVMS = false;
    
    /**
     * Rotate parsers when a ParseException is thrown?
     */
    private boolean rotateParsers = true;
     
    /**
     * Constructor
     * 
     * @param system    SYST string
     */
    public FTPFileFactory(String system) throws FTPException {
        setParser(system);
    }
    
    /**
     * Constructor. User supplied parser. Note that parser
     * rotation (in case of a ParseException) is disabled if
     * a parser is explicitly supplied
     * 
     * @param parser   the parser to use
     */
    public FTPFileFactory(FTPFileParser parser) {
        this.parser = parser;
        rotateParsers = false;
    }   
    
    /**
     * Set the locale for date parsing of listings
     * 
     * @param locale    locale to set
     */
    public void setLocale(Locale locale) {
        windows.setLocale(locale);
        unix.setLocale(locale);
        vms.setLocale(locale);
        parser.setLocale(locale); // might be user supplied
    }
    
    /**
     * Set the remote server type
     * 
     * @param system    SYST string
     */
    private void setParser(String system) {
        this.system = system;
        if (system.toUpperCase().startsWith(WINDOWS_STR))
            parser = windows;
        else if (system.toUpperCase().startsWith(UNIX_STR))
            parser = unix;
        else if (system.toUpperCase().startsWith(VMS_STR)) {
            parser = vms;
            usingVMS = true;
        }
        else {
            parser = unix;
            log.warn("Unknown SYST '" + system + "' - defaulting to Unix parsing");
        }
    }
    
    
    /**
     * Parse an array of raw file information returned from the
     * FTP server
     * 
     * @param files     array of strings
     * @return array of FTPFile objects
     */
    public FTPFile[] parse(String[] files) throws ParseException {
               
        FTPFile[] temp = new FTPFile[files.length];
        
        // quick check if no files returned
        if (files.length == 0)
            return temp;
                
        int count = 0;
        boolean checkedUnix = false;
        for (int i = 0; i < files.length; i++) {
            try {
                if (files[i] == null || files[i].trim().length() == 0)
                    continue;
                
                // swap to Unix if looks like Unix listing
                if (!checkedUnix && parser != unix && UnixFileParser.isUnix(files[i])) {
                    parser = unix;
                    checkedUnix = true;
                    log.info("Swapping Windows parser to Unix");
                }
                
                FTPFile file = null;
                if(usingVMS) {
                    // vms uses 2 lines for some file listings.  send 2 just in case
                    if (files[i].indexOf(';') > 0) {
                       if (i+1 < files.length && files[i+1].indexOf(';') < 0) {
                           file = parser.parse(files[i] + "  " + files[i+1]);
                           i++; // skip over second part for next iteration
                       }
                       else
                           file = parser.parse(files[i]);
                    }
                }
                else {
                    file = parser.parse(files[i]);
                }
                // we skip null returns - these are duff lines we know about and don't
                // really want to throw an exception
                if (file != null) {
                    temp[count++] = file;
                }
            }
            catch (ParseException ex) {
                log.info("Failed to parse listing '" + files[i] + "': " + ex.getMessage());
                if (rotateParsers) { // first error, let's try swapping parsers
                    rotateParsers = false; // only do once
                    rotateParsers();
                    FTPFile file = parser.parse(files[i]);
                    if (file != null)
                        temp[count++] = file;
                }
                else // rethrow
                    throw ex;
            }
        }
        FTPFile[] result = new FTPFile[count];
        System.arraycopy(temp, 0, result, 0, count);
        return result;
    }
    
    /**
     * Swap from one parser to the other. We can just check
     * object references
     */
    private void rotateParsers() {
        usingVMS = false;
        if (parser == unix) {
            parser = windows;
            log.info("Rotated parser to Windows");
        }
        else if (parser == windows){
            parser = unix;
            log.info("Rotated parser to Unix");
        }
    }

    /**
     * Get the SYST string
     * 
     * @return the system string.
     */
    public String getSystem() {
        return system;
    }


}
