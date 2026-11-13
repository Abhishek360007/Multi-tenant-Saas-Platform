FROM eclipse-temurin:21-jre-jammy
LABEL org.opencontainers.image.title="tenanthub" \
      org.opencontainers.image.source="https://github.com/Abhishek360007/Multi-tenant-Saas-Platform" \
      org.opencontainers.image.licenses="Apache-2.0"
ENV VERSION=1.1
# Mounted container directories
# tmp: Temp directory used by embedded Tomcat
# logs: spring-boot Log output directory
# config: spring-boot External configuration directory
VOLUME ["/tmp","/logs","/config"]
# Copy the built jar and rename it to app.jar
COPY target/tenanthub-service-1.0.1.jar app.jar
# Run in UTC; the web UIs convert times to the viewer's time zone
RUN ln -sf /usr/share/zoneinfo/UTC /etc/localtime
RUN echo "UTC" >/etc/timezone
# Encoding
ENV LANG=C.UTF-8
# JVM Parameters
ENV JVM_OPTS="-server -Xms2g -Xmx2g -XX:+DisableExplicitGC -XX:+HeapDumpOnOutOfMemoryError"
# Server port
ENV SERVER_PORT=18891
# Exposed port
EXPOSE ${SERVER_PORT}
# Spring Boot start command
ENTRYPOINT ["sh", "-c", "exec java ${JVM_OPTS} -Dfile.encoding=UTF-8 -Duser.timezone=UTC -Djava.security.egd=file:/dev/./urandom -jar /app.jar"]
