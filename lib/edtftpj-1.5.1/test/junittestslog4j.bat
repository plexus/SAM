set JAVA_HOME=C:\j2sdk1.4.2_03
rem
rem  Note that we rename edtftpj-x.x.x to edtftpj.jar just for testing
rem  so we don't have to keep changing this filename in the CLASSPATH.
rem
rem  The log4j jar can be obtained from http://logging.apache.org/log4j/docs/download.html
rem
set CLASSPATH=..\lib\junit.jar;..\lib\edtftpj.jar;..\lib\log4j.jar;.

%JAVA_HOME%\bin\java -Dftptest.connectmode=ACTIVE -Dedtftp.log.log4j=true com.enterprisedt.net.ftp.test.TestLogin

%JAVA_HOME%\bin\java -Dftptest.connectmode=ACTIVE -Dedtftp.log.log4j=true com.enterprisedt.net.ftp.test.TestGeneral

%JAVA_HOME%\bin\java -Dftptest.connectmode=ACTIVE -Dedtftp.log.log4j=true com.enterprisedt.net.ftp.test.TestListings

%JAVA_HOME%\bin\java -Dftptest.connectmode=ACTIVE -Dedtftp.log.log4j=true com.enterprisedt.net.ftp.test.TestFileOperations

%JAVA_HOME%\bin\java -Dftptest.connectmode=ACTIVE -Dedtftp.log.log4j=true com.enterprisedt.net.ftp.test.TestDirOperations

%JAVA_HOME%\bin\java -Dftptest.connectmode=ACTIVE -Dedtftp.log.log4j=true com.enterprisedt.net.ftp.test.TestTransfer

%JAVA_HOME%\bin\java -Dftptest.connectmode=PASV -Dedtftp.log.log4j=true com.enterprisedt.net.ftp.test.TestLogin

%JAVA_HOME%\bin\java -Dftptest.connectmode=PASV -Dedtftp.log.log4j=true com.enterprisedt.net.ftp.test.TestGeneral

%JAVA_HOME%\bin\java -Dftptest.connectmode=PASV -Dedtftp.log.log4j=true com.enterprisedt.net.ftp.test.TestListings

%JAVA_HOME%\bin\java -Dftptest.connectmode=PASV -Dedtftp.log.log4j=true com.enterprisedt.net.ftp.test.TestFileOperations

%JAVA_HOME%\bin\java -Dftptest.connectmode=PASV -Dedtftp.log.log4j=true com.enterprisedt.net.ftp.test.TestDirOperations

%JAVA_HOME%\bin\java -Dftptest.connectmode=PASV -Dedtftp.log.log4j=true com.enterprisedt.net.ftp.test.TestTransfer

