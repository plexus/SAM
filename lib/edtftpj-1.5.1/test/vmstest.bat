set JAVA_HOME=C:\j2sdk1.4.2_03
set CLASSPATH=..\lib\edtftpj.jar

set JAVA_HOME=C:\j2sdk1.4.2_03
rem
set CLASSPATH=..\lib\junit.jar;..\lib\edtftpj.jar

%JAVA_HOME%\bin\java -Dftptest.properties.filename=test.vms.properties -Dftptest.connectmode=ACTIVE com.enterprisedt.net.ftp.test.VMSTests  

%JAVA_HOME%\bin\java -Dftptest.properties.filename=test.vms.properties -Dftptest.connectmode=PASV com.enterprisedt.net.ftp.test.VMSTests  
