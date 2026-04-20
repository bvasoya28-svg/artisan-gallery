# Use an official JDK runtime as a parent image
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the gradle wrapper and configuration files
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Copy the web module files
COPY web web

# Build the application
RUN ./gradlew :web:bootJar --no-daemon

# Expose the port the app runs on
EXPOSE 8085

# Run the jar file
ENTRYPOINT ["java", "-jar", "web/build/libs/web-0.0.1-SNAPSHOT.jar"]
