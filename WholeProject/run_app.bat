@echo off
REM Create bin directory if it doesn't exist
if not exist bin mkdir bin

REM Define JavaFX Path (User provided)
set "JAVAFX_PATH=D:\Semester 3\Semester 3\Object Oriented Programming\javafx\javafx-sdk-25.0.1\lib"

REM Compile the code (this will compile all dependencies automatically)
echo Compiling...
javac -d bin --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.fxml -cp "lib/mssql-jdbc-13.2.1.jre11.jar" -sourcepath src -Xlint:-this-escape src/app/App.java src/app/Main.java src/app/UserSession.java src/app/UserManager.java
if %errorlevel% neq 0 (
    echo Compilation failed.
    pause
    exit /b %errorlevel%
)

REM Compile all view controllers
echo Compiling controllers...
javac -d bin --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.fxml -cp "lib/mssql-jdbc-13.2.1.jre11.jar;bin" -sourcepath src src\view\auth\*.java src\view\home\*.java src\view\nutrition\*.java src\view\profile\*.java src\view\tracking\*.java src\view\workout\*.java >nul 2>&1
if %errorlevel% neq 0 (
    echo Controller compilation failed.
    pause
    exit /b %errorlevel%
)

REM Copy resources (db.properties, FXML, CSS)
echo Copying resources...
if not exist bin\config mkdir bin\config
copy /Y src\config\db.properties bin\config\db.properties >nul 2>&1
xcopy /Y /S /I src\view\*.fxml bin\view >nul 2>&1
xcopy /Y /S /I src\view\*.css bin\view >nul 2>&1

REM Run the application
echo Running JavaFX Application...
java --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.fxml -cp "lib/mssql-jdbc-13.2.1.jre11.jar;bin" app.App

pause
