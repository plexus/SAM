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
 *
 */
package com.enterprisedt.net.ftp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *  Represents a remote OpenVMS file parser. Thanks to Jason Schultz for contributing
 *  significantly to this class 
 *
 *  @author      Bruce Blackshaw
 *  @version     $Revision: 1.1 $
 */
public class VMSFileParser extends FTPFileParser {

    /**
     *  Revision control id
     */
    public static String cvsId = "@(#)$Id: VMSFileParser.java,v 1.1 2005/08/28 13:40:11 plexus Exp $";
             
    /**
     * Directory field
     */
    private final static String DIR = ".DIR";
    
    /**
     * Directory line
     */
    private final static String HDR = "Directory";
    
    /**
     * Total line
     */
    private final static String TOTAL = "Total";
    
    /**
     * Blocksize for calculating file sizes
     */
    private final int BLOCKSIZE = 512*1024;
    
    /**
     * Number of expected fields
     */
    private final static int MIN_EXPECTED_FIELD_COUNT = 4;
    
    /**
     * Date formatter
     */
    private SimpleDateFormat formatter1;
    
    /**
     * Date formatter
     */
    private SimpleDateFormat formatter2;
    
    /**
     * Constructor
     */
    public VMSFileParser() {
         setLocale(Locale.getDefault());
    }

    /**
     * Parse server supplied string
     *
     * OUTPUT: <begin>
     * 
     * Directory <dir>
     *  
     * <filename>
     *      used/allocated  dd-MMM-yyyy HH:mm:ss [unknown]      (PERMS)
     * <filename>
     *      used/allocated  dd-MMM-yyyy HH:mm:ss [unknown]      (PERMS)
     * ...
     * 
     * Total of <> files, <>/<> blocks
     *
     * @param raw   raw string to parse
     */
    public FTPFile parse(String raw) throws ParseException {
        String[] fields = split(raw);
        
        // skip blank lines
        if(fields.length <= 0)
        	return null;
        // skip line which lists Directory
        if (fields.length >= 2 && fields[0].compareTo(HDR) == 0)
        	return null;
        // skip line which lists Total
        if (fields.length > 0 && fields[0].compareTo(TOTAL) == 0)
        	return null;
        // probably the remainder of a listing on 2nd line
        if (fields.length < MIN_EXPECTED_FIELD_COUNT) 
            return null; 
        
        // first field is name
        String name = fields[0];
        
        // make sure it is the name (ends with ';<INT>')
        int semiPos = name.lastIndexOf(';');
        // check for ;
        if(semiPos <= 0) {
            throw new ParseException("File version number not found in name '" + name + "'", 0);
        }
        name = name.substring(0, semiPos);
        
        // check for version after ;
        String afterSemi = fields[0].substring(semiPos + 1);
		try{
			Integer.parseInt(afterSemi);
			// didn't throw exception yet, must be number
			// we don't use it currently but we might in future
		}
		catch(NumberFormatException ex) {
		    // don't worry about version number
		}
        
        // test is dir
        boolean isDir = false;
        if (semiPos < 0) {
        	semiPos = fields[0].length();
        }
        if( semiPos <= 4) {
        	// string to small to have a .DIR
        }
        else {
        	// look for .DIR
        	String tstExtnsn = fields[0].substring(semiPos-4, semiPos);
        	if(tstExtnsn.compareTo(DIR) == 0) {
        		isDir = true;
                name = name.substring(0, semiPos-4);
            }
        }
        
        // 2nd field is size USED/ALLOCATED format
        int slashPos = fields[1].indexOf('/');
        String sizeUsed = fields[1];
        if (slashPos > 0)
            sizeUsed = fields[1].substring(0, slashPos);
        long size = Long.parseLong(sizeUsed) * BLOCKSIZE;
        
        // 3 & 4 fields are date time
        Date lastModified = null;
        try {
            lastModified = formatter1.parse(fields[2] + " " + fields[3]);
        }
        catch (ParseException ex) {
            lastModified = formatter2.parse(fields[2] + " " + fields[3]);
        }
        
        // 5th field is [group,owner]
        String group = null;
        String owner = null;
        if (fields.length >= 5) {     
            if (fields[4].charAt(0) == '[' && fields[4].charAt(fields[4].length()-1) == ']') {
                int commaPos = fields[4].indexOf(',');
                if (commaPos < 0) {
                    throw new ParseException("Unable to parse [group,owner] field '" + fields[4] + "'", 0);
                }
                group = fields[4].substring(1, commaPos);
                owner = fields[4].substring(commaPos+1, fields[4].length()-1);
            }
        }
        
        // 6th field is permissions e.g. (RWED,RWED,RE,)
        String permissions = null;
        if (fields.length >= 6) {     
            if (fields[5].charAt(0) == '(' && fields[5].charAt(fields[5].length()-1) == ')') {
                permissions = fields[5].substring(1, fields[5].length()-2);
            }
        }
        
        FTPFile file = new FTPFile(FTPFile.VMS, raw, name, size, isDir, lastModified); 
        file.setGroup(group);
        file.setOwner(owner);
        file.setPermissions(permissions);
        return file;        
    }

    /* (non-Javadoc)
     * @see com.enterprisedt.net.ftp.FTPFileParser#setLocale(java.util.Locale)
     */
    public void setLocale(Locale locale) {
        formatter1 = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", locale);
        formatter2 = new SimpleDateFormat("dd-MMM-yyyy HH:mm", locale);
    }
  
}
