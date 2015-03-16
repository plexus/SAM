set JAVA_HOME=C:\j2sdk1.4.2_03
rem
rem  note that we rename edtftpj-1.x.x to edtftpj.jar just for testing
rem  so we don't have to keep changing this file
rem
set CLASSPATH=..\lib\junit.jar;..\lib\edtftpj.jar

%JAVA_HOME%\bin\java -Dftptest.connectmode=ACTIVE com.enterprisedt.net.ftp.test.TestBigTransfer

%JAVA_HOME%\bin\java -Dftptest.connectmode=PASV com.enterprisedt.net.ftp.test.TestBigTransfer
