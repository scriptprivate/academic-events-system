#!/bin/bash
echo "Compiling Academic Events System..."
javac -cp "src/lib/postgresql-42.7.1.jar" src/main/java/*.java -d build/
echo "Compilation complete!"