FROM openjdk:21-jdk-slim

ARG JAR_FILE=build/libs/*.jar
ENV GOOGLE_APPLICATION_CREDENTIALS=/gcp_cloud.json

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
