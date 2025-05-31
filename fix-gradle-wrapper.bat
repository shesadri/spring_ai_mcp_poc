@echo off
setlocal enabledelayedexpansion

REM Script to fix missing gradle-wrapper.jar on Windows
REM This script downloads the official gradle-wrapper.jar for the configured Gradle version

echo 🔧 Fixing missing gradle-wrapper.jar...

REM Check if gradle-wrapper.properties exists
if not exist "gradle\wrapper\gradle-wrapper.properties" (
    echo ❌ Error: gradle\wrapper\gradle-wrapper.properties not found!
    exit /b 1
)

REM Extract Gradle version from gradle-wrapper.properties
for /f "tokens=2 delims=-" %%a in ('findstr "distributionUrl" gradle\wrapper\gradle-wrapper.properties') do (
    for /f "tokens=1 delims=-" %%b in ("%%a") do (
        set GRADLE_VERSION=%%b
    )
)

if "%GRADLE_VERSION%"=="" (
    echo ❌ Error: Could not determine Gradle version from gradle-wrapper.properties
    exit /b 1
)

echo 📋 Detected Gradle version: %GRADLE_VERSION%

REM Create wrapper directory if it doesn't exist
if not exist "gradle\wrapper" mkdir "gradle\wrapper"

REM Download the gradle-wrapper.jar
set WRAPPER_JAR_URL=https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-wrapper.jar
echo 📥 Downloading gradle-wrapper.jar from: %WRAPPER_JAR_URL%

REM Try to download using PowerShell (available on Windows 7+ with PowerShell 2.0+)
powershell -Command "try { Invoke-WebRequest -Uri '%WRAPPER_JAR_URL%' -OutFile 'gradle\wrapper\gradle-wrapper.jar' -UseBasicParsing; Write-Host '✅ Successfully downloaded gradle-wrapper.jar' } catch { Write-Host '❌ Error downloading gradle-wrapper.jar:' $_.Exception.Message; exit 1 }"

if errorlevel 1 (
    echo ❌ Failed to download gradle-wrapper.jar
    exit /b 1
)

REM Verify the file was downloaded
if exist "gradle\wrapper\gradle-wrapper.jar" (
    echo ✅ Successfully downloaded gradle-wrapper.jar
    for %%A in ("gradle\wrapper\gradle-wrapper.jar") do echo 📏 File size: %%~zA bytes
    
    REM Test the wrapper
    echo 🧪 Testing Gradle wrapper...
    gradlew.bat --version
    
    if errorlevel 1 (
        echo ❌ Gradle wrapper test failed
        exit /b 1
    )
    
    echo 🎉 Gradle wrapper is now working correctly!
) else (
    echo ❌ Error: Failed to download gradle-wrapper.jar
    exit /b 1
)

echo.
echo ✨ Next steps:
echo    1. Commit the gradle-wrapper.jar file to your repository
echo    2. The Gradle wrapper should now work correctly
echo    3. You can delete this script and README_GRADLE_WRAPPER_FIX.md after fixing

pause
