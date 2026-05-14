# Step 1: Lightweight Java 17 Runtime environment ka use karna [cite: 12]
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Step 2: Local 'target' folder se compiled JAR file ko container mein copy karna 
# Isse Docker build ke waqt internet dependencies ki zaroorat nahi padti
COPY target/*.jar app.jar

# Step 3: Backend API port define karna [cite: 12]
EXPOSE 8081

# Step 4: Container start hone par Spring Boot application run karne ki command
ENTRYPOINT ["java", "-jar", "app.jar"]