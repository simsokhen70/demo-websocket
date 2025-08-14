#!/bin/bash

echo "========================================"
echo "Demo WebSocket Application Startup"
echo "========================================"
echo

echo "Prerequisites Check:"
echo "1. Ensure PostgreSQL is running on localhost:5432"
echo "2. Ensure Kafka is running on localhost:9092"
echo "3. Ensure Zookeeper is running on localhost:2181"
echo

echo "Starting Demo WebSocket Application..."
echo

# Build the application
echo "Building application..."
./gradlew clean build -x test

if [ $? -ne 0 ]; then
    echo "Build failed! Please check the errors above."
    exit 1
fi

echo
echo "Build successful! Starting application..."
echo

# Run the application
./gradlew bootRun
