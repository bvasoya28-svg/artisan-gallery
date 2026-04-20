# Use an official JDK runtime as a parent image
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy all root files (gradlew, gradle, build scripts)
COPY gradlew .
COPY gradle gradle
COPY *.kts .
COPY *.properties .

# Copy the modules
COPY web web
COPY app app

# Build only the web application
RUN chmod +x gradlew
RUN ./gradlew :web:bootJar --no-daemon

# Expose the port
EXPOSE 8085

# Run the jar file
ENTRYPOINT ["java", "-jar", "web/build/libs/web-0.0.1-SNAPSHOT.jar"]
