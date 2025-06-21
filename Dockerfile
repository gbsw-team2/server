FROM openjdk:21-jdk-slim

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} app.jar

COPY gcloud-key.json gcloud-key.json

ENV GOOGLE_APPLICATION_CREDENTIALS=/app/gcloud-key.json

ENTRYPOINT ["java", "-jar", "app.jar"]
