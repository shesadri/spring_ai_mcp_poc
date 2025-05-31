#!/bin/bash

# Script to fix missing gradle-wrapper.jar
# This script downloads the official gradle-wrapper.jar for the configured Gradle version

set -e

echo "🔧 Fixing missing gradle-wrapper.jar..."

# Read the Gradle version from gradle-wrapper.properties
if [ ! -f "gradle/wrapper/gradle-wrapper.properties" ]; then
    echo "❌ Error: gradle/wrapper/gradle-wrapper.properties not found!"
    exit 1
fi

GRADLE_VERSION=$(grep "distributionUrl" gradle/wrapper/gradle-wrapper.properties | sed 's/.*gradle-\(.*\)-bin\.zip.*/\1/')

if [ -z "$GRADLE_VERSION" ]; then
    echo "❌ Error: Could not determine Gradle version from gradle-wrapper.properties"
    exit 1
fi

echo "📋 Detected Gradle version: $GRADLE_VERSION"

# Create wrapper directory if it doesn't exist
mkdir -p gradle/wrapper

# Download the gradle-wrapper.jar
WRAPPER_JAR_URL="https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-wrapper.jar"
echo "📥 Downloading gradle-wrapper.jar from: $WRAPPER_JAR_URL"

if command -v curl >/dev/null 2>&1; then
    curl -L -o gradle/wrapper/gradle-wrapper.jar "$WRAPPER_JAR_URL"
elif command -v wget >/dev/null 2>&1; then
    wget -O gradle/wrapper/gradle-wrapper.jar "$WRAPPER_JAR_URL"
else
    echo "❌ Error: Neither curl nor wget is available. Please install one of them."
    exit 1
fi

# Verify the file was downloaded
if [ -f "gradle/wrapper/gradle-wrapper.jar" ]; then
    echo "✅ Successfully downloaded gradle-wrapper.jar"
    echo "📏 File size: $(ls -lh gradle/wrapper/gradle-wrapper.jar | awk '{print $5}')"
    
    # Test the wrapper
    echo "🧪 Testing Gradle wrapper..."
    chmod +x gradlew
    ./gradlew --version
    
    echo "🎉 Gradle wrapper is now working correctly!"
else
    echo "❌ Error: Failed to download gradle-wrapper.jar"
    exit 1
fi

echo ""
echo "✨ Next steps:"
echo "   1. Commit the gradle-wrapper.jar file to your repository"
echo "   2. The Gradle wrapper should now work correctly"
echo "   3. You can delete this script and README_GRADLE_WRAPPER_FIX.md after fixing"
