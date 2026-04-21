# Start from a clean Java 21 image
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copy all build files
COPY gradlew .
COPY gradle gradle
COPY *.kts .
COPY *.properties .
COPY web web

# Build only the web module
RUN tr -d '\r' < gradlew > gradlew_unix && mv gradlew_unix gradlew
RUN chmod +x gradlew
RUN ./gradlew :web:bootJar --no-daemon

# Expose port (Render overrides this with $PORT)
EXPOSE 10000

# Use the environment variable $PORT for the web server
CMD ["sh", "-c", "java -jar web/build/libs/web-0.0.1-SNAPSHOT.jar --server.port=${PORT:-10000}"]
