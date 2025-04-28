# Use the Liberica JDK as the base image
FROM eclipse-temurin:23-jdk

# Set the working directory
WORKDIR /app

# Copy the Maven build files
COPY . .

# Build the application with Maven
RUN ./mvnw package -DskipTests

# Expose the necessary ports
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "target/bookstore-demo.jar"]