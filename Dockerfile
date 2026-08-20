# syntax=docker/dockerfile:1

# ============================================================
# Stage 1: Build the Spring Boot application
# ============================================================
FROM eclipse-temurin:17-jdk-jammy AS build

WORKDIR /app

# Copy Gradle files first to take advantage of Docker layer caching.
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle

# Make Gradle wrapper executable.
RUN chmod +x gradlew

# Copy application source code.
COPY src ./src

# Build the executable Spring Boot JAR.
RUN ./gradlew --no-daemon clean bootJar


# ============================================================
# Stage 2: Run the application
# ============================================================
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Run the application as a non-root user.
RUN groupadd --system spring && \
    useradd --system --gid spring --create-home spring

USER spring:spring

# Copy the JAR from the build stage.
COPY --from=build /app/build/libs/*.jar app.jar

# Spring Boot listens on port 8080.
EXPOSE 8080

# JVM options can be overridden through the environment.
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0"

# Start the Spring Boot application.
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]