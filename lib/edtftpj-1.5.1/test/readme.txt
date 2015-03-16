Running the edtFTPj tests.

1. Open a console.

2. Change to the test directory (i.e. the directory where this readme file is).

3. Edit junittests.bat, edit test.properties and set JAVA_HOME appropriately

	Note that the test account on the FTP server must have a directory structure
	as below for junit tests:
	
		empty // an empty directory
		remote
			test
				

4. Run the test by:
     junittests

5. Examine the log files for test output.

   