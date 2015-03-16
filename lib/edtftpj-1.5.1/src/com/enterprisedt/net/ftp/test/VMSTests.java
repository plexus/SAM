/**
 *
 *  edtFTPj
 *
 *  Copyright (C) 2000  Enterprise Distributed Technologies Ltd
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
 *        $Log: VMSTests.java,v $
 *        Revision 1.1  2005/08/28 13:40:11  plexus
 *        *** empty log message ***
 *
 *        Revision 1.1  2005/06/03 11:26:49  bruceb
 *        new tests
 *
 *
 */

package com.enterprisedt.net.ftp.test;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FTPFile;
import com.enterprisedt.net.ftp.FTPTransferType;

/**
 *  Tests against an external (public) VMS FTP server - so we
 *  can't do put's.
 *
 *  @author     Bruce Blackshaw
 *  @version    $Revision: 1.1 $
 */
public class VMSTests extends FTPTestCase {

    /**
     *  Revision control id
     */
    public static String cvsId = "@(#)$Id: VMSTests.java,v 1.1 2005/08/28 13:40:11 plexus Exp $";

    /**
     *  Get name of log file
     *
     *  @return name of file to log to
     */
    protected String getLogName() {
        return "TestVMS.log";
    }

    /**
     *  Test directory listings
     */ 
    public void testDir() throws Exception {

        connect();
        login();

        // move to test directory
        ftp.chdir(testdir);

        // list current dir
        String[] list = ftp.dir();
        print(list);

        // non-existent file
		String randomName = generateRandomFilename();
        try {        
            list = ftp.dir(randomName);
            print(list);
		}
		catch (FTPException ex) {
            if (ex.getReplyCode() != 550 && ex.getReplyCode() != 552)
                fail("dir(" + randomName + ") should throw 550/552 for non-existent dir");
		}            
        
        ftp.quit();
    }

    /**
     *  Test full directory listings
     */ 
    public void testDirFull() throws Exception {

        connect();
        login();

        // move to test directory
        ftp.chdir(testdir);

        // list current dir by name
        String[] list = ftp.dir("", true);
        print(list);
        
        log.debug("******* dirDetails *******");
        FTPFile[] files = ftp.dirDetails("");
        print(files);
        log.debug("******* end dirDetails *******");

        // non-existent file. Some FTP servers don't send
        // a 450/450, but IIS does - so we catch it and
        // confirm it is a 550
        String randomName = generateRandomFilename();
        try {        
        	list = ftp.dir(randomName, true);
        	print(list);
        }
        catch (FTPException ex) {
        	if (ex.getReplyCode() != 550 && ex.getReplyCode() != 552)
				fail("dir(" + randomName + ") should throw 550/552 for non-existent dir");
        }
        
        ftp.quit();
    }

    /**
     *  Test transfering a text file
     */
    public void testTransferText() throws Exception {

        log.debug("testTransferText()");

        connect();
        login();
        
        // move to test directory
        ftp.chdir(testdir);
        ftp.setType(FTPTransferType.ASCII);

        // random filename
        String filename = generateRandomFilename();

        // get it        
        ftp.get(filename, remoteTextFile);

        // and delete local file
        File local = new File(filename);
        local.delete();

        ftp.quit();
    }
    
    
    /**
     *  Test transfering a binary file
     */
    public void testTransferBinary() throws Exception {

        log.debug("testTransferBinary()");

        connect();
        login();
        
        // move to test directory
        ftp.chdir(testdir);
        ftp.setType(FTPTransferType.BINARY);
        
        // random filename
        String filename = generateRandomFilename();

        // get     
        ftp.get(filename, remoteBinaryFile);

        // and delete local file
        File local = new File(filename);
        local.delete();

        ftp.quit();
    }

    
    /**
     *  Automatic test suite construction
     *
     *  @return  suite of tests for this class
     */
    public static Test suite() {
        return new TestSuite(VMSTests.class);
    } 

    /**
     *  Enable our class to be run, doing the
     *  tests
     */
    public static void main(String[] args) {       
        junit.textui.TestRunner.run(suite());
    }
}

