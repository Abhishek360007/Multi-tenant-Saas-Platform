# TenantHub server: builds from source, so hosts like Render can deploy straight from GitHub.
#   docker build -t tenanthub-server ./server

# ---- build ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /src
COPY . .
RUN mvn -B -ntp -q -DskipTests package && cp tenanthub-service/target/tenanthub-service-*.jar /app.jar

# ---- run ----
FROM eclipse-temurin:21-jre-jammy
RUN useradd --system --create-home tenanthub
USER tenanthub
WORKDIR /home/tenanthub
COPY --from=build /app.jar app.jar
# Fits a 512 MB container (e.g. Render free): small heap, serial GC, C1-only JIT for a lighter footprint
ENV SPRING_PROFILES_ACTIVE=prod \
    JWT_TOKEN_KEYS_DIR=/home/tenanthub/keys \
    STORAGE_LOCAL_PATH=/home/tenanthub/uploads/ \
    JAVA_OPTS="-XX:MaxRAMPercentage=60 -XX:+UseSerialGC -XX:TieredStopAtLevel=1 -XX:MaxMetaspaceSize=192m -XX:ReservedCodeCacheSize=48m -Xss512k -Dspring.jmx.enabled=false"
EXPOSE 18891
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -Dfile.encoding=UTF-8 -Djava.security.egd=file:/dev/./urandom -jar app.jar"]
