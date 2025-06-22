# Step 1: Build fat jar using ktor plugin
FROM gradle:8.7-jdk17 AS builder

WORKDIR /app

COPY . .

RUN ./gradlew buildFatJar --no-daemon

# Step 2: Run jar
FROM openjdk:17-slim

RUN apt-get update && apt-get install -y curl && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=builder /app/build/libs/application.jar /app/application.jar

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

CMD java $JAVA_OPTS -jar /app/application.jar