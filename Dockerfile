FROM gradle:8.5-jdk17 AS build

WORKDIR /app
COPY . /app/
RUN gradle build --no-daemon

FROM openjdk:17-slim

# Add labels for better documentation
LABEL maintainer="Developer <joleksiysurovtsev@gmail.com>"
LABEL version="0.0.1"
LABEL description="TrainmateAI Data Provider Service"

# Install curl for health check
RUN apt-get update && apt-get install -y curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Create a non-root user
RUN groupadd -r appuser && useradd -r -g appuser appuser

WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/application.jar

# Environment variables for database connection
ENV POSTGRES_SERVER=localhost
ENV POSTGRES_PORT=5432
ENV POSTGRES_DB_NAME=train_mate_ai_data
ENV POSTGRES_USERNAME=postgres
ENV POSTGRES_PASSWORD=postgres

# Create directory for persistent data with proper permissions
RUN mkdir -p /app/data && chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose the port the app runs on
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8080/ || exit 1

# JVM options for containerized environment
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# Command to run the application
CMD java $JAVA_OPTS -jar /app/application.jar
