# JavaFX Auto-Setup Script for Windows
# This script will download JavaFX SDK and configure your project automatically

Write-Host "===============================================" -ForegroundColor Cyan
Write-Host "JavaFX Auto-Configuration Script" -ForegroundColor Cyan
Write-Host "===============================================`n" -ForegroundColor Cyan

$javafxVersion = "21.0.1"
$downloadUrl = "https://download2.gluonhq.com/openjfx/$javafxVersion/openjfx-${javafxVersion}_windows-x64_bin-sdk.zip"
$downloadPath = "javafx-sdk.zip"
$extractPath = "javafx-sdk-$javafxVersion"

Write-Host "[1/4] Downloading JavaFX SDK $javafxVersion..." -ForegroundColor Yellow
try {
    $ProgressPreference = 'SilentlyContinue'
    Invoke-WebRequest -Uri $downloadUrl -OutFile $downloadPath -UseBasicParsing
    Write-Host "✓ Download complete" -ForegroundColor Green
} catch {
    Write-Host "✗ Download failed: $_" -ForegroundColor Red
    Write-Host "`nPlease download manually from: https://gluonhq.com/products/javafx/" -ForegroundColor Yellow
    exit 1
}

Write-Host "`n[2/4] Extracting JavaFX SDK..." -ForegroundColor Yellow
try {
    Expand-Archive -Path $downloadPath -DestinationPath "." -Force
    Write-Host "✓ Extraction complete" -ForegroundColor Green
} catch {
    Write-Host "✗ Extraction failed: $_" -ForegroundColor Red
    exit 1
}

Write-Host "`n[3/4] Copying JavaFX JARs to lib folder..." -ForegroundColor Yellow
$javafxLibPath = Join-Path $extractPath "lib"
if (Test-Path $javafxLibPath) {
    Copy-Item "$javafxLibPath\*.jar" -Destination "lib\" -Force
    Write-Host "✓ JARs copied successfully" -ForegroundColor Green
} else {
    Write-Host "✗ JavaFX lib folder not found" -ForegroundColor Red
    exit 1
}

Write-Host "`n[4/4] Updating .classpath file..." -ForegroundColor Yellow
$classpathContent = @'
<?xml version="1.0" encoding="UTF-8"?>
<classpath>
	<classpathentry kind="src" path="src"/>
	<classpathentry kind="con" path="org.eclipse.jdt.launching.JRE_CONTAINER">
		<attributes>
			<attribute name="module" value="true"/>
		</attributes>
	</classpathentry>
	<classpathentry kind="lib" path="lib/javafx.base.jar"/>
	<classpathentry kind="lib" path="lib/javafx.controls.jar"/>
	<classpathentry kind="lib" path="lib/javafx.fxml.jar"/>
	<classpathentry kind="lib" path="lib/javafx.graphics.jar"/>
	<classpathentry kind="lib" path="lib/javafx.media.jar"/>
	<classpathentry kind="lib" path="lib/javafx.swing.jar"/>
	<classpathentry kind="lib" path="lib/javafx.web.jar"/>
	<classpathentry kind="lib" path="lib/mssql-jdbc-13.2.1.jre11.jar"/>
	<classpathentry kind="output" path="bin"/>
</classpath>
'@

$classpathContent | Out-File -FilePath ".classpath" -Encoding UTF8 -Force
Write-Host "✓ .classpath updated" -ForegroundColor Green

Write-Host "`n[Cleanup] Removing temporary files..." -ForegroundColor Yellow
Remove-Item $downloadPath -Force -ErrorAction SilentlyContinue
Remove-Item $extractPath -Recurse -Force -ErrorAction SilentlyContinue
Write-Host "✓ Cleanup complete" -ForegroundColor Green

Write-Host "`n===============================================" -ForegroundColor Cyan
Write-Host "✓ JavaFX Configuration Complete!" -ForegroundColor Green
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host "`nNext steps:" -ForegroundColor Yellow
Write-Host "1. Refresh your project in Eclipse (F5)" -ForegroundColor White
Write-Host "2. Clean and rebuild (Project → Clean...)" -ForegroundColor White
Write-Host "3. All JavaFX errors should now be resolved!" -ForegroundColor White
Write-Host "`nTo run the app, add VM arguments:" -ForegroundColor Yellow
Write-Host "--module-path `"lib`" --add-modules javafx.controls,javafx.fxml" -ForegroundColor Cyan
