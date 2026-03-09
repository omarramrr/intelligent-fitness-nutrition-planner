@echo off
REM Compile the code
if not exist bin mkdir bin
javac -cp "lib/mssql-jdbc-13.2.1.jre11.jar;src" -d bin src/test/ConnectionTest.java src/config/DatabaseConnection.java

REM Copy properties file
copy /Y src\config\db.properties bin\config\db.properties >nul

REM Run the test
java -cp "lib/mssql-jdbc-13.2.1.jre11.jar;bin" test.ConnectionTest
pause
