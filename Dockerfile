# Set OpenJDK 11 as base image
FROM openjdk:11-jre-slim

# Expose Tomcat port
EXPOSE 8080

# Copy jar package containing REST Server into the image
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Create user with no root privileges
RUN addgroup --system appgroup && adduser --system --ingroup appgroup app

# Use created user
USER app

# Run REST Server
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "/app.jar"]