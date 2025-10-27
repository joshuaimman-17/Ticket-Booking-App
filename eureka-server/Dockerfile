# Use official OpenJDK 21 image as base
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the jar into the container
COPY target/eureka-server-0.0.1-SNAPSHOT.jar app.jar

# Expose Eureka server port
EXPOSE 8761

# Run the jar
ENTRYPOINT ["java","-jar","app.jar"]
