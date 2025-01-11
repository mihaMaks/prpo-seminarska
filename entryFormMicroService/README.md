# KŠK BAZA

Skeletni projekt za predmet PRPO.

## Predpogoji

Preden začnete, se prepričajte, da imate nameščena naslednja orodja:

1. Java JDK 17
2. Apache Maven
3. Docker

## Zagon baze

Za delovanje aplikacije potrebujete podatkovno bazo, ki jo lahko enostavno zaženete s tehnologijo Docker na naslednji način:

```bash
docker run -d -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=ksk-baza -p 5432:5432 postgres:13
```
export JAVA_HOME=$(/usr/libexec/java_home -v 18)

## Gradnja in zagon projekta

Najprej zgradite projekt:

```
mvn clean package
```

Nato ga lahko zaženete z naslednjim ukazom:

```
java -jar api/target/api-1.0.0-SNAPSHOT.jar
```

Za preverjanje delovanja aplikacije lahko v brskalniku odprete naslednji URL: http://localhost:8080/servlet

FROM openjdk:17-jdk-slim


RUN mkdir /app

WORKDIR /app

ADD ./api/target/api-1.0.0-SNAPSHOT.jar /app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "api-1.0.0-SNAPSHOT.jar"]
```bash
docker build -t form-service-image .
```
```bash
docker run -d -p 9090:9090 --network=mynetwork form-service-image
docker run -d -p 9090:9090 --network=mynetwork form-service-image

docker run --name form-service --network mynetwork -p 9090:9090 form-service-image

```
```bash
docker ps
```
docker-compose build
