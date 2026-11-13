# Spring Boot 3.X + mybatis-plus + liquibase 多租户[多数据库] 解决方案

### 项目介绍

#### docker-compose.yml 部署

```shell
version: '3'

networks:
  app-net1:
    ipam:
      config:
        - subnet: 172.19.0.0/16
          gateway: 172.19.0.1

services:
  tenanthub-pro:
    image: eclipse-temurin:21-jre-jammy
    container_name: tenanthub-pro
    ports:
      - "18891:18891"
    environment:
      - SPRING_PROFILES_ACTIVE=pro
      - JVM_OPTS=-server -Xms2g -Xmx2g -XX:+DisableExplicitGC -XX:+HeapDumpOnOutOfMemoryError
    volumes:
      - /opt/tenanthub-server/tenanthub-service-1.0.1.jar:/app.jar
      - /opt/tenanthub-server/application-pro.yml:/config/application-pro.yml
      - /etc/localtime:/etc/localtime:ro
    command: java ${JVM_OPTS} -Dfile.encoding=UTF-8 -Duser.timezone=UTC -Djava.security.egd=file:/dev/./urandom -jar /app.jar
    privileged: true
    restart: always
    networks:
      app-net1:

```

#### Linux 脚本
```shell

# 启动
./tenanthub.sh start

# 停止
./tenanthub.sh stop

# 重启
./tenanthub.sh restart

# 状态
./tenanthub.sh status

```

#### Linux 服务
```shell

# 拷贝 tenanthub.service
sudo cp tenanthub.service /usr/lib/systemd/system/

# 设置 tenanthub 服务开机自启动
sudo systemctl enable tenanthub

# 停止 tenanthub 服务开机自启动
sudo systemctl disable tenanthub

# 重启
sudo systemctl daemon-reload

# 启动
sudo systemctl start tenanthub

# 停止
sudo systemctl stop tenanthub

# 重启
sudo systemctl restart tenanthub

# 重启
sudo systemctl status tenanthub

```


#### 全局更新 pom.xml
全局更新 pom.xml 版本 IDEA 按两下 Ctrl

```shell

mvn versions:set -DnewVersion=1.0.2 -DgenerateBackUpPoms=false

```


#### [eclipse temurin jdk下载地址](https://adoptium.net/zh-CN/temurin/releases/?package=jdk&arch=x64&os=any)

