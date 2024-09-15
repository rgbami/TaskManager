#!/bin/bash

# Navigate to the directory containing the Java source file
cd "$(dirname "$0")/../Resources"

# Compile the Java file
javac TaskManager.java

# Run the compiled Java program
java TaskManager
