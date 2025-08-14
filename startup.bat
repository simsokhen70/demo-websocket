@echo off
echo ========================================
echo Demo WebSocket Application Startup
echo ========================================
echo.

echo Prerequisites Check:
echo 1. Ensure PostgreSQL is running on localhost:5432
echo 2. Ensure Kafka is running on localhost:9092
echo 3. Ensure Zookeeper is running on localhost:2181
echo.

echo Starting Demo WebSocket Application...
echo.

REM Build the application
echo Building application...
call gradlew.bat clean build -x test

if %ERRORLEVEL% NEQ 0 (
    echo Build failed! Please check the errors above.
    pause
    exit /b 1
)

echo.
echo Build successful! Starting application...
echo.

REM Run the application
call gradlew.bat bootRun

pause
