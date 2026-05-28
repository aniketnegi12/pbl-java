#!/bin/bash

# AI-Based Pricing System Launcher
# This script compiles and runs the Integrated Pricing System

echo "🤖 Compiling AI Pricing System..."
javac -cp build/main/java:lib/h2.jar:src/main/resources -d build/main/java \
    src/main/java/IntegratedPricingApp.java \
    src/main/java/pricing/PricePredictor.java \
    src/main/java/db/*.java 2>&1

if [ $? -eq 0 ]; then
    echo "✓ Compilation successful!"
    echo ""
    echo "🚀 Starting AI Pricing System..."
    echo ""
    java -cp build/main/java:lib/h2.jar:src/main/resources main.java.IntegratedPricingApp
else
    echo "❌ Compilation failed!"
    exit 1
fi
