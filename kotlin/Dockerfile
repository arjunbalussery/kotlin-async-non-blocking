FROM eclipse-temurin:19

# Set the working directory to /app
WORKDIR /app

COPY build/libs/kotlin-*-SNAPSHOT.jar /service.jar

# Expose port 8080
EXPOSE 8080

# Command to run your application
CMD ["java", "-jar", "/service.jar"]

