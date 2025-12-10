# ===============================
# Stage 1: Build Stage using Maven
# ===============================
FROM maven:3.9.3-eclipse-temurin-17 AS build

WORKDIR /app

# Copy parent pom
COPY pom.xml . 

# Copy modules
COPY jps-service ./jps-service
COPY web-ser ./web-ser

# Build web-ser module
RUN mvn clean package -DskipTests -f web-ser/pom.xml

# ===============================
# Stage 2: Run Stage using OpenJDK 17
# ===============================
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy built jar
COPY --from=build /app/web-ser/target/web-ser-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
