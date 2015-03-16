/*
 * Created on 28-aug-2005
 *
 */
package com.plexus.sam.net;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.FTPConnectMode;
import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FTPFile;
import com.enterprisedt.net.ftp.FTPFileFactory;
import com.enterprisedt.net.ftp.FTPFileParser;
import com.enterprisedt.net.ftp.FTPTransferType;
import com.enterprisedt.net.ftp.UnixFileParser;
import com.enterprisedt.net.ftp.WindowsFileParser;

import com.plexus.sam.SAM;
import com.plexus.sam.config.ConfigGroup;
import com.plexus.sam.config.Configuration;

/**
 * Implementation of the FileTransfer facade interface for the FTP protocol.
 * All information is gathered from the ftp configuration group,
 * including a remote base path from which relative file/directory requests
 * can be made.
 * <br /><br />
 * Following settings are used:<br />
 * <ul>
 * 		<li>remote_path : location on the server</li>
 * 		<li>connect_mode : ACTIVE or PASSIVE</li>
 * 		<li>control_port : should be a valid integer</li>
 *		<li>hostname : i.e. ftp.someserver.com</li>
 *		<li>user_id : username of the ftp account</li>
 *		<li>password : password of the ftp account</li> 
 *		<li>transfer_type : BINARY or ASCII</li>
 *		<li>server_type : WIN or UNIX</li>
 *		<li>server_locale : i.e. en_US</li>
 * </ul>
 * @author plexus
 *
 */
public class FTPTransferModule implements FileTransferModule {
	private ResourceBundle i18n_error = ResourceBundle.getBundle("com.plexus.sam.i18n.error");
	private ConfigGroup config = Configuration.getConfigGroup("ftp");
	
	private FTPClient ftpClient = new FTPClient();
	
	private boolean DEBUG = false;
	/**
	 * Make a connection to the FTP server. All settings come from the ftp configgroup.
	 * 
	 * If any of the calls fail a FTPException is thrown
	 * @see com.plexus.sam.net.FileTransferModule#connect()
	 * @throws FTPException when connection fails.
	 */
	public void connect() throws FTPException {
		debug("ftp connect()");
		//if we're not allready connected
		if (! ftpClient.connected() ) {
			//set ACTIVE or PASSIVE connection mode
			FTPConnectMode connectMode = (config.get("connect_mode").equals("ACTIVE")) 
											? FTPConnectMode.ACTIVE 
										    : FTPConnectMode.PASV;
			ftpClient.setConnectMode( connectMode );
			debug("ftp setConnectMode "+connectMode);
			
			//set the control port (default 21)
			try {
				ftpClient.setControlPort( Integer.parseInt( config.get( "control_port" ) ) );
				debug("ftp controlport "+Integer.parseInt( config.get( "control_port" ) ));
			} catch (NumberFormatException e) {
				SAM.error(i18n_error.getString("ftp_controlport_wrongint"), e );
				ftpClient.setControlPort( 21 );
				debug("ftp controlport 21 (NumberFormatException : defaulting)");
			} 
			
			//set the hostname
			try {
				ftpClient.setRemoteHost( config.get( "hostname" ) );
				debug( "ftp hostname"+config.get( "hostname" ) );
			} catch ( IOException e ) {
				SAM.error(i18n_error.getString("ftp_unknown_hostname"), e );
				debug("ftp hostname : IOException");
			}
			
			//set the parser (WIN or UNIX style server)
			FTPFileParser parser;
			if ( config.get("server_type").equals("UNIX") )
				parser = new UnixFileParser();
			else
				parser = new WindowsFileParser();
			
			debug( "ftp servertype "+config.get("server_type") );
			ftpClient.setFTPFileFactory( new FTPFileFactory( parser ) );
			
			//set the server locale
			ftpClient.setParserLocale( new Locale( config.get( "server_locale" ) ) );
			debug( "ftp server locale "+config.get("server_locale") );
			
			//connect and login
			try {					
					ftpClient.connect();
					debug ( "ftp connected" );
					
					ftpClient.login( config.get( "user_id" ) , config.get( "password" ) );
					debug( "ftp logged in" );
					
					//set the transfer type (ASCII or BINARY)
					ftpClient.setType( config.get("transfer_type").equals("ASCII") 
							? FTPTransferType.ASCII
							: FTPTransferType.BINARY);
					debug ( "ftp transfer type "+config.get("transfer_type") );
			} catch (IOException e) {
				SAM.error("ftp_unable_to_write", e );
				debug("ftp IOException connect/login/settransfertype");
			}
			if (! ftpClient.connected() ) {
				debug( "ftp failed to connect");
			}
		}
	}

	private void debug(String string) {
		if (DEBUG)
			System.out.println(string);
	}

	/**
	 * Copy a file from the server to the local filesystem.
	 * 
	 * @see com.plexus.sam.net.FileTransferModule#getFile(java.lang.String, java.lang.String)
	 */
	public void getFile(String remote, String local) {
		if (isConnected()) {
			debug ("ftp getFile "+remote+" => "+local);
			try {
				ftpClient.get( local, remotePath( remote ) );
			}
			catch (IOException e) {
				SAM.error("ftp_unable_to_get" , e);
			}
			catch (FTPException e) {
				SAM.error( "ftp_unable_to_get" , e);
			}
		} else {
			debug ( "    not connected!" );
		}
	}

	/**
	 * Replace in the filename all instances of '\' with '/' 
	 * (UNIX style pathnames).
	 * 
	 * @param file
	 * @return the fully qualyfied remote location of the file
	 */
	private String remotePath(String file) {
		if (config.get("server_type").equals("UNIX")) {
			file = file.replace( '\\', '/' );
		}
		else {
			file = file.replace("/", "\\");
		}
		return file;
	}

	/**
	 * Get a directory listing as a List of RemoteFile objects.
	 * 
	 * @see com.plexus.sam.net.FileTransferModule#getDirectory(java.lang.String)
	 */
	public List getDirectory(String dir) throws FTPException {
		debug ("ftp getDirectory "+remotePath(dir));
		List<RemoteFile> returnList = new ArrayList<RemoteFile>();
		if ( isConnected() ) {		
			try {				
				FTPFile[] files = ftpClient.dirDetails( remotePath( dir ) );
				RemoteFile rFile;
				for ( int i=0 ; i < files.length ; i++ ) {
					rFile = new RemoteFile();
					rFile.setDir( files[i].isDir() );
					rFile.setName( files[i].getName() );
					returnList.add( rFile );
					if (rFile.isDir())
						debug("    remoteDirectory : "+rFile.getName());
					else
						debug("    remote file     : "+rFile.getName());
				}
			} 
			catch ( IOException e ) {
				SAM.error("ftp_unable_to_read", e );
				debug ("    unable to get directory info : IOException" );
			}
			catch ( ParseException e ) {
				SAM.error("ftp_parse_error", e );
				debug("     unable to get directory info : ParseException");
			}
		}
		return returnList;
	}

	/**
	 * Disconnect from the FTP server (send the quit command).
	 * 
	 * @see com.plexus.sam.net.FileTransferModule#disConnect()
	 */
	public void disConnect() {
		debug( "ftp disconnect" );
		if ( isConnected() ) {
			try {
				ftpClient.quit();
			} catch ( IOException e ) {
				SAM.error("ftp_unable_to_write", e );
			} catch ( FTPException e ) {
				if ( isConnected() )
					SAM.error("ftp_unable_to_disconnect", e);
			}
		}
		if (isConnected())
			debug ( "    failed to disconnect!" );
	}

	/**
	 * Check wheter the FTP connection is open.
	 */
	public boolean isConnected() {
		return ftpClient.connected();
	}
}
