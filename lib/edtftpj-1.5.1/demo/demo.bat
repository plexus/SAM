set JAVA_HOME=C:\j2sdk1.4.2_03
set CP=.;..\lib\edtftpj.jar
%JAVA_HOME%\bin\javac -classpath %CP% Demo.java 
%JAVA_HOME%\bin\java -classpath %CP% Demo %1 %2 %3

