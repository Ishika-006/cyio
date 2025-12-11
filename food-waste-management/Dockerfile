# Stage 1: Build with Maven + JDK
FROM maven:3.9.0-eclipse-temurin-17 AS build

WORKDIR /app

# Copy parent pom.xml and modules (adjust path)
COPY food-waste-management/pom.xml .
COPY food-waste-management/jps-service ./jps-service
COPY food-waste-management/web-ser ./web-ser

# Build the project (skip tests)
RUN mvn clean package -DskipTests


# Stage 2: Run with lightweight JDK
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/web-ser/target/web-ser-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Run Spring Boot app
ENTRYPOINT ["java","-jar","/app/app.jar"]

