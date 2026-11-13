# TenantHub server

Spring Boot 3.5 + MyBatis-Plus + Liquibase backend. Each tenant gets its own MySQL database.

## Build

```bash
mvn -DskipTests package
# -> tenanthub-service/target/tenanthub-service-1.0.1.jar
```

Requires JDK 21 or newer.

## Run with Docker

Use the root `docker-compose.yml` (MySQL, Redis, Nginx and the app). A standalone app container looks like this:

```yaml
services:
  tenanthub:
    image: eclipse-temurin:21-jre-jammy
    container_name: tenanthub
    ports:
      - "18891:18891"
    environment:
      - SPRING_PROFILES_ACTIVE=pro
      - JWT_TOKEN_KEYS_DIR=/keys
      - JVM_OPTS=-server -Xms2g -Xmx2g -XX:+DisableExplicitGC -XX:+HeapDumpOnOutOfMemoryError
    volumes:
      - /opt/tenanthub-server/keys:/keys
      - /opt/tenanthub-server/tenanthub-service-1.0.1.jar:/app.jar
      - /opt/tenanthub-server/application-pro.yml:/config/application-pro.yml
    command: java ${JVM_OPTS} -Dfile.encoding=UTF-8 -Duser.timezone=UTC -jar /app.jar
    restart: always
```

Keep the `keys` volume persistent and private: it holds the generated JWT secret and RSA key pair.

## Run with the shell script

```bash
./tenanthub.sh start     # start
./tenanthub.sh stop      # stop
./tenanthub.sh restart   # restart
./tenanthub.sh status    # status
```

## Run as a systemd service

```bash
sudo cp tenanthub.service /usr/lib/systemd/system/
sudo systemctl daemon-reload
sudo systemctl enable tenanthub    # start at boot
sudo systemctl start tenanthub
sudo systemctl status tenanthub
# also: stop, restart, disable
```

## Sample requests

`REST API/*.http` contains requests for the IntelliJ HTTP client
(environments in `REST API/http-client.env.json`).

## Bump the version

```bash
mvn versions:set -DnewVersion=1.0.2 -DgenerateBackUpPoms=false
```
