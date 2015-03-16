rem This test is run separately as we simulate network failure
rem by pulling out a cable during the test
rem

set JAVA_HOME=C:\j2sdk1.4.2_03
set CLASSPATH=..\lib\junit.jar;..\lib\edtftpj.jar

%JAVA_HOME%\bin\java com.enterprisedt.net.ftp.test.TestTimeout

