# Use Java 21 (Matching your project's requirement)
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy gradle files
COPY gradlew .
COPY gradle gradle
COPY *.kts .
COPY *.properties .

# Copy ONLY the web module
COPY web web

# Fix line endings and permissions
RUN tr -d '\r' < gradlew > gradlew_unix && mv gradlew_unix gradlew
RUN chmod +x gradlew

# Build ONLY the web module jar
RUN ./gradlew :web:bootJar --no-daemon

# Expose port 8085
EXPOSE 8085

# Run the jar file
ENTRYPOINT ["java", "-jar", "web/build/libs/web-0.0.1-SNAPSHOT.jar"]
