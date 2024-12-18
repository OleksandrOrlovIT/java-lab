FROM openjdk:17-jdk-slim

VOLUME /tmp

COPY target/Gym_Trainer_Workload*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
