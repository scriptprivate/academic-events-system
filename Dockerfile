FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy source files
COPY src/main/java /app/src/main/java
COPY src/main/resources /app/src/main/resources
COPY src/lib /app/src/lib

# Create build directory
RUN mkdir /app/build

# Compile Java files
RUN javac -cp "src/lib/postgresql-42.7.1.jar" \
     src/main/java/*.java -d build/

# Set classpath and run the main class
CMD ["java", "-cp", "build:src/lib/postgresql-42.7.1.jar:src/main/resources", "AcademicEventsApp"]