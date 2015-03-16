set JAVA_HOME=C:\j2sdk1.4.2_03
rem
set CLASSPATH=..\lib\junit.jar;..\lib\edtftpj.jar

%JAVA_HOME%\bin\java -Dftptest.connectmode=ACTIVE com.enterprisedt.net.ftp.test.TestLogin

%JAVA_HOME%\bin\java -Dftptest.connectmode=ACTIVE com.enterprisedt.net.ftp.test.TestGeneral

%JAVA_HOME%\bin\java -Dftptest.connectmode=ACTIVE com.enterprisedt.net.ftp.test.TestListings

%JAVA_HOME%\bin\java -Dftptest.connectmode=ACTIVE com.enterprisedt.net.ftp.test.TestFileOperations

%JAVA_HOME%\bin\java -Dftptest.connectmode=ACTIVE com.enterprisedt.net.ftp.test.TestDirOperations

%JAVA_HOME%\bin\java -Dftptest.connectmode=ACTIVE com.enterprisedt.net.ftp.test.TestTransfer

%JAVA_HOME%\bin\java -Dftptest.connectmode=ACTIVE com.enterprisedt.net.ftp.test.TestFeatures

%JAVA_HOME%\bin\java -Dftptest.connectmode=ACTIVE com.enterprisedt.net.ftp.test.TestResume

%JAVA_HOME%\bin\java -Dftptest.connectmode=ACTIVE com.enterprisedt.net.ftp.test.TestBulkTransfer

@echo off
rem
rem 120 second pause to allow earlier TIME_WAITs to expire
rem
ping -n 120 localhost>NUL
@echo on

%JAVA_HOME%\bin\java -Dftptest.connectmode=ACTIVE com.enterprisedt.net.ftp.test.TestPortRange

@echo off
rem
rem 120 second pause to allow earlier TIME_WAITs to expire
rem
ping -n 120 localhost>NUL
@echo on

%JAVA_HOME%\bin\java -Dftptest.connectmode=PASV com.enterprisedt.net.ftp.test.TestLogin

%JAVA_HOME%\bin\java -Dftptest.connectmode=PASV com.enterprisedt.net.ftp.test.TestGeneral

%JAVA_HOME%\bin\java -Dftptest.connectmode=PASV com.enterprisedt.net.ftp.test.TestListings

%JAVA_HOME%\bin\java -Dftptest.connectmode=PASV com.enterprisedt.net.ftp.test.TestFileOperations

%JAVA_HOME%\bin\java -Dftptest.connectmode=PASV com.enterprisedt.net.ftp.test.TestDirOperations

%JAVA_HOME%\bin\java -Dftptest.connectmode=PASV com.enterprisedt.net.ftp.test.TestTransfer

%JAVA_HOME%\bin\java -Dftptest.connectmode=PASV com.enterprisedt.net.ftp.test.TestFeatures

%JAVA_HOME%\bin\java -Dftptest.connectmode=PASV com.enterprisedt.net.ftp.test.TestResume

%JAVA_HOME%\bin\java -Dftptest.connectmode=PASV com.enterprisedt.net.ftp.test.TestBulkTransfer

vmstest.bat
