# PRPO smeinarska - baza-ksk (backend)
## Kratek opis:
Applikacija v obliki mikro storitev za predmet princimi razvoja programske opreme.
Vsaka mikrostoritev je v svojem dokumentu, ki vključuje tudi datoteko docker.
Izpostavja 4 končne točke:

    http://localhost:8080/members  
    http://localhost:8080/events  
    http://localhost:8080/entry-form  
    http://localhost:8080/email


## Zagon aplikacije

### Zgradite mikro storitve:

```
cd <ime-mikro-sotritve>
mvn clean package
```


### Najprej zaženemo bazo z ukazom:

```bash
docker run -d -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=ksk-baza -p 5432:5432 postgres:13

```

## Zaženemo docker network
```bash
docker network create mynetwork
```
### Slike zaženemo:
```bash
cd <ime-mikro-storitve>
```

#### Member service:
```bash
docker run -d --network mynetwork -p 8080:8080 <image-name>
```
#### Event service:
```bash
docker run --name event-service-1 --network mynetwork -p 8081:8081 <image-name>
```

#### Entry-form service:
```bash
docker run -d --network mynetwork -p 9090:9090 <image-name>
```

#### Email service:
```bash
docker run -d -p 9091:9091 --network=mynetwork <image-name>
```

## Frontend 
Git: https://github.com/mihaMaks/baza-ksk-frontend.git

## Dodatno
### Informacije o projektu

Verzija jave:

        java version "21.0.5" 2024-10-15 LTS
        Java(TM) SE Runtime Environment (build 21.0.5+9-LTS-239)
        Java HotSpot(TM) 64-Bit Server VM (build 21.0.5+9-LTS-239, mixed mode, sharing)

Verzija maven:
    
    OS name: "mac os x", version: "14.6.1", arch: "aarch64", family: "mac"






