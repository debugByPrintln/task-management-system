FROM openjdk:21-jdk-slim

RUN apt-get update && apt-get install -y maven

COPY target/task-management-system-0.0.1-SNAPSHOT.jar app.jar

COPY src src
COPY pom.xml pom.xml

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]