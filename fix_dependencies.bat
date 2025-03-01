@echo off
echo ===== Fixing Maven Dependencies =====
echo.
echo This script will clean and reinstall all project dependencies.
echo.

REM Check if Maven is installed
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Maven is not installed or not in the PATH.
    echo Please install Maven and try again.
    echo You can download Maven from: https://maven.apache.org/download.cgi
    pause
    exit /b 1
)

echo Running Maven clean...
call mvn clean

echo Running Maven install...
call mvn install -DskipTests

echo.
echo ===== Dependency fix complete =====
echo.
echo If you still see errors in your IDE, try:
echo 1. Refreshing your Maven project
echo 2. Restarting your IDE
echo 3. Right-clicking on your project and selecting "Maven" -> "Update Project..."
echo.
pause 