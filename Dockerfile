# Use Java 17 (Matching your build.gradle)
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy gradle files
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradle.properties .

# Copy ONLY the web module (ignore the android app)
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
