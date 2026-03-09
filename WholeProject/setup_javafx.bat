@echo off
REM JavaFX Quick Setup Script
echo ================================================
echo JavaFX Auto-Download and Setup
echo ================================================
echo.

REM Download JavaFX SDK
echo [1/3] Downloading JavaFX SDK (this may take a minute)...
powershell -Command "& {$ProgressPreference = 'SilentlyContinue'; Invoke-WebRequest -Uri 'https://download2.gluonhq.com/openjfx/21.0.1/openjfx-21.0.1_windows-x64_bin-sdk.zip' -OutFile 'javafx-sdk.zip' -UseBasicParsing}"
if not exist javafx-sdk.zip (
    echo ERROR: Download failed!
    echo Please download manually from: https://gluonhq.com/products/javafx/
    pause
    exit /b 1
)
echo Download complete!

REM Extract ZIP
echo.
echo [2/3] Extracting JavaFX SDK...
powershell -Command "& {Expand-Archive -Path 'javafx-sdk.zip' -DestinationPath '.' -Force}"
echo Extraction complete!

REM Copy JARs to lib folder
echo.
echo [3/3] Copying JavaFX JARs to lib folder...
xcopy /Y "javafx-sdk-21.0.1\lib\*.jar" "lib\"
echo JARs copied successfully!

REM Update .classpath
echo.
echo [BONUS] Updating .classpath file...
(
echo ^<?xml version="1.0" encoding="UTF-8"?^>
echo ^<classpath^>
echo 	^<classpathentry kind="src" path="src"/^>
echo 	^<classpathentry kind="con" path="org.eclipse.jdt.launching.JRE_CONTAINER"^>
echo 		^<attributes^>
echo 			^<attribute name="module" value="true"/^>
echo 		^</attributes^>
echo 	^</classpathentry^>
echo 	^<classpathentry kind="lib" path="lib/javafx.base.jar"/^>
echo 	^<classpathentry kind="lib" path="lib/javafx.controls.jar"/^>
echo 	^<classpathentry kind="lib" path="lib/javafx.fxml.jar"/^>
echo 	^<classpathentry kind="lib" path="lib/javafx.graphics.jar"/^>
echo 	^<classpathentry kind="lib" path="lib/javafx.media.jar"/^>
echo 	^<classpathentry kind="lib" path="lib/javafx.swing.jar"/^>
echo 	^<classpathentry kind="lib" path="lib/javafx.web.jar"/^>
echo 	^<classpathentry kind="lib" path="lib/mssql-jdbc-13.2.1.jre11.jar"/^>
echo 	^<classpathentry kind="output" path="bin"/^>
echo ^</classpath^>
) > .classpath
echo .classpath updated!

REM Cleanup
echo.
echo [CLEANUP] Removing temporary files...
del /Q javafx-sdk.zip 2>nul
rmdir /S /Q javafx-sdk-21.0.1 2>nul
echo Cleanup complete!

echo.
echo ================================================
echo SUCCESS! JavaFX Configuration Complete!
echo ================================================
echo.
echo Next steps:
echo 1. Open your project in Eclipse
echo 2. Right-click project and select "Refresh" (F5)
echo 3. Clean and rebuild: Project -^> Clean...
echo 4. All JavaFX errors should now be resolved!
echo.
echo To run the app, add VM arguments in Run Configuration:
echo --module-path "lib" --add-modules javafx.controls,javafx.fxml
echo.
pause
