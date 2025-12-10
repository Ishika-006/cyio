# Step 1: Use official OpenJDK 17 image
FROM openjdk:17-jdk-slim

# Step 2: Set working directory inside container
WORKDIR /app

# Step 3: Copy the Maven built jar into container
COPY target/web-ser-0.0.1-SNAPSHOT.jar app.jar

# Step 4: Expose port (Render assigns dynamic PORT env variable)
EXPOSE 8080

# Step 5: Set default command to run the jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
