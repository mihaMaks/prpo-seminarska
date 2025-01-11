FROM openjdk:17-jdk-slim

RUN mkdir /app

WORKDIR /app

ADD ../api/target/member-api-1.0.0-SNAPSHOT.jar /app

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "member-api-1.0.0-SNAPSHOT.jar"]