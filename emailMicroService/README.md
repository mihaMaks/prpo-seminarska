export JAVA_HOME=$(/usr/libexec/java_home -v 18)

```bash
docker build -t email-service-image .
```
```bash
docker run -d -p 9091:9091 --network=mynetwork email-service-image
docker run -d -p 9091:9091 --network=mynetwork email-service-image

docker run --name email-service --network mynetwork -p 9091:9091 email-service-image

```
```bash
docker ps
```
docker-compose build
