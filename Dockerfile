
# syntax=docker/dockerfile:1

# --- Build stage ---
FROM openjdk:21-jdk AS build

WORKDIR /app

# Copy the rest of the source code
COPY . .

# # Build the application (skip tests for faster build)
RUN chmod +x mvnw && ./mvnw clean install

# --- Runtime stage ---
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar /app/app.jar

# Expose the port the app runs on
EXPOSE 1010

# Use exec form for proper signal handling
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
