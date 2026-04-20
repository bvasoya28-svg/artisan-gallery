# Use Java 21 (matching your project's requirement)
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy all root files
COPY gradlew .
COPY gradle gradle
COPY *.kts .
COPY *.properties .

# Copy the modules
COPY web web
COPY app app

# Build the web application
RUN chmod +x gradlew
RUN ./gradlew :web:bootJar --no-daemon

# Expose the port
EXPOSE 8085

# Run the jar file
ENTRYPOINT ["java", "-jar", "web/build/libs/web-0.0.1-SNAPSHOT.jar"]
