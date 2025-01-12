
```bash
docker build -t event-service-image .
```
```bash
docker run -d -p 8081:8081 --network=mynetwork event-service-image

docker run --name event-service --network mynetwork -p 8081:8081 event-service-image

```
```bash
docker ps
```
export JAVA_HOME=$(/usr/libexec/java_home -v 18)
TO DO:
poskusi se enkrat zagnat memberservice na dockerju in eventservice potem da vidis ce se res pogovarjata


docker-compose build

