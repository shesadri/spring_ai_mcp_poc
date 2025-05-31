#!/bin/bash
# Script to initialize Gradle wrapper

echo "Initializing Gradle wrapper..."

# Download gradle wrapper jar if it doesn't exist
if [ ! -f "gradle/wrapper/gradle-wrapper.jar" ]; then
    echo "Downloading Gradle wrapper..."
    mkdir -p gradle/wrapper
    curl -L -o gradle/wrapper/gradle-wrapper.jar \
        https://github.com/gradle/gradle/raw/v8.6.0/gradle/wrapper/gradle-wrapper.jar
fi

# Make gradlew executable
chmod +x gradlew

echo "Gradle wrapper initialized successfully!"
echo "You can now run: ./gradlew build"
