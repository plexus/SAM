set JAVA_HOME=C:\j2sdk1.4.2_03
rem
set CLASSPATH=..\lib\junit.jar;..\lib\edtftpj.jar

%JAVA_HOME%\bin\java -Dftptest.connectmode=ACTIVE com.enterprisedt.net.ftp.test.TestTransfer
