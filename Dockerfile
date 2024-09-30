# Use an official OpenJDK runtime as a parent image
FROM openjdk:21-jdk

# Set the working directory inside the container
WORKDIR /book-network-api

# Copy the jar file from the machine to the container
COPY book-network-api/target/book-network-api-0.0.1-SNAPSHOT.jar app.jar

# Expose the port that the application will run on
EXPOSE 8088

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]