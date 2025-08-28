FROM openjdk:17-slim
COPY target/demo-swagger-app.jar /app/demo-swagger-app.jar
WORKDIR /app
CMD ["java", "-jar", "demo-swagger-app.jar"]
EXPOSE 8080
# FROM maven:3.9-eclipse-temurin-17-alpine
#
# WORKDIR /app
#
# COPY . .
#
# EXPOSE 8080
#
# CMD ["sh", "-c", "mvn clean package -DskipTests && java -jar target/*.jar"]
