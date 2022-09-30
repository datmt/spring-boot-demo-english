# spring-boot-demo-graylog

> This demo mainly demonstrates how the Spring Boot project can access GrayLog for log management.

## Note

When the author wrote this demo, 'graylog' was started with 'docker-compose', where 'mongodb' and 'elasticsearch' dependencies were launched synchronously, and the production environment recommended using external storage.

## 1. Environment preparation

**Write 'docker-compose' startup files for 'graylog'**

> If there are no images of 'mongo:3' and 'elasticsearch-oss:6.6.1' locally, it will be time-consuming

```yaml
version: '2'
services:
  # MongoDB: https://hub.docker.com/_/mongo/
  mongodb:
    image: mongo:3
  # Elasticsearch: https://www.elastic.co/guide/en/elasticsearch/reference/6.6/docker.html
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch-oss:6.6.1
    environment:
      - http.host=0.0.0.0
      - transport.host=localhost
      - network.host=0.0.0.0
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
    ulimits:
      memlock:
        soft: -1
        hard: -1
    mem_limit: 1g
  # Graylog: https://hub.docker.com/r/graylog/graylog/
  graylog:
    image: graylog/graylog:3.0
    environment:
      # Encryption salt value, do not set, graylog will start failure
      # This field requires a minimum of 16 characters
      - GRAYLOG_PASSWORD_SECRET=somepasswordpepper
      # Set the user name
      - GRAYLOG_ROOT_USERNAME=admin
      # Set the password, which is the string after the password has been encrypted by SHA256
      # Encryption mode, execute echo -n "Enter Password: " && head -1 </dev/stdin | tr -d '\n' | sha256sum | cut -d" " -f1
      - GRAYLOG_ROOT_PASSWORD_SHA2=8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918
      - GRAYLOG_HTTP_EXTERNAL_URI=http://127.0.0.1:9000/
      # Set the time zone
      - GRAYLOG_ROOT_TIMEZONE=Asia/Shanghai
    links:
      - mongodb:mongo
      - elasticsearch
    depends_on:
      - mongodb
      - elasticsearch
    ports:
      # Graylog web interface and REST API
      - 9000:9000
      # Syslog TCP
      - 1514:1514
      # Syslog UDP
      - 1514:1514/udp
      # GELF TCP
      - 12201:12201
      # GELF UDP
      - 12201:12201/udp
```

## 2. pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <artifactId>spring-boot-demo-graylog</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <packaging>jar</packaging>

  <name>spring-boot-demo-graylog</name>
  <description>Demo project for Spring Boot</description>

  <parent>
    <groupId>com.xkcoding</groupId>
    <artifactId>spring-boot-demo</artifactId>
    <version>1.0.0-SNAPSHOT</version>
  </parent>

  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    <java.version>1.8</java.version>
  </properties>

  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-test</artifactId>
      <scope>test</scope>
    </dependency>

    <!-- provides logback to transfer logs to graylog dependencies -->
    <dependency>
      <groupId>de.siegmar</groupId>
      <artifactId>logback-gelf</artifactId>
      <version>2.0.0</version>
    </dependency>
  </dependencies>

  <build>
    <finalName>spring-boot-demo-graylog</finalName>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
      </plugin>
    </plugins>
  </build>

</project>
```

## 3. application.yml

```yaml
spring:
  application:
    name: graylog
```

## 4. logback-spring.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright 2019 Yangkai.Shen
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
  ~
  ~    http://www.apache.org/licenses/LICENSE-2.0
  ~ Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or  implied. See the License for the specific language governing permissions and limitations under the License.
  -->
<configuration scan="true" scanPeriod="60 seconds">

  <!-- Rendering class on which color logs depend - >
  <conversionRule conversionWord="clr" converterClass="org.springframework.boot.logging.logback.ColorConverter"/>
  <conversionRule conversionWord="wex"
                  converterClass="org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter"/>
  <conversionRule conversionWord="wEx"
                  converterClass="org.springframework.boot.logging.logback.ExtendedWhitespaceThrowableProxyConverter"/>
  <!-- Color log format -->
  <property name="CONSOLE_LOG_PATTERN"
            value="${CONSOLE_LOG_PATTERN:-%clr(%d{yyyy-MM-dd HH:mm:ss. SSS}){faint} %clr(${LOG_LEVEL_PATTERN:-%5p}) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{50}){cyan} %clr(:){faint} %file:%line - %m%n${ LOG_EXCEPTION_CONVERSION_WORD:-%wEx}}"/>
  <!-- graylog full log format -->
  <property name="GRAY_LOG_FULL_PATTERN"
            value="%n%d{yyyy-MM-dd HH:mm:ss. SSS} [%thread] [%logger{50}] %file:%line%n%-5level: %msg%n"/>
  <!-- graylog simplifies the log format -->
  <property name="GRAY_LOG_SHORT_PATTERN"
            value="%m%nopex"/>

  <!-- Get Service Name -->
  <springProperty scope="context" name="APP_NAME" source="spring.application.name"/>

  <!-- Console Output -->
  <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
      <pattern>${CONSOLE_LOG_PATTERN}</pattern>
      <charset>utf8</charset>
    </encoder>
  </appender>

  <!-- graylog log collection -->
  <appender name="GELF" class="de.siegmar.logbackgelf.GelfUdpAppender">
    <graylogHost>localhost</graylogHost>
    <graylogPort>12201</graylogPort>
    <maxChunkSize>508</maxChunkSize>
    <useCompression>true</useCompression>
    <encoder class="de.siegmar.logbackgelf.GelfEncoder">
      <includeRawMessage>true</includeRawMessage>
      <includeMarker>true</includeMarker>
      <includeMdcData>true</includeMdcData>
      <includeCallerData>false</includeCallerData>
      <includeRootCauseData>false</includeRootCauseData>
      <includeLevelName>true</includeLevelName>
      <shortPatternLayout class="ch.qos.logback.classic.PatternLayout">
        <pattern>${GRAY_LOG_SHORT_PATTERN}</pattern>
      </shortPatternLayout>
      <fullPatternLayout class="ch.qos.logback.classic.PatternLayout">
        <pattern>${GRAY_LOG_FULL_PATTERN}</pattern>
      </fullPatternLayout>
      <staticField>app_name:${APP_NAME}</staticField>
      <staticField>os_arch:${os.arch}</staticField>
      <staticField>os_name:${os.name}</staticField>
      <staticField>os_version:${os.version}</staticField>
    </encoder>
  </appender>

  <!-- Log output level -->
  <root level="INFO">
    <appender-ref ref="STDOUT"/>
    <appender-ref ref="GELF" />
  </root>

  <logger name="net.sf.ehcache" level="INFO"/>
  <logger name="druid.sql" level="INFO"/>


  <!-- MyBatis log configure -->
  <logger name="com.apache.ibatis" level="INFO"/>
  <logger name="org.mybatis.spring" level="DEBUG"/>
  <logger name="java.sql.Connection" level="DEBUG"/>
  <logger name="java.sql.Statement" level="DEBUG"/>
  <logger name="java.sql.PreparedStatement" level="DEBUG"/>

  <!-- Reduce some debug logs -->
  <logger name="druid.sql" level="INFO"/>
  <logger name="org.apache.shiro" level="INFO"/>
  <logger name="org.mybatis.spring" level="INFO"/>
  <logger name="org.springframework" level="INFO"/>
  <logger name="org.springframework.context" level="WARN"/>
  <logger name="org.springframework.beans" level="WARN"/>
  <logger name="com.baomidou.mybatisplus" level="INFO"/>
  <logger name="org.apache.ibatis.io" level="INFO"/>
  <logger name="org.apache.velocity" level="INFO"/>
  <logger name="org.eclipse.jetty" level="INFO"/>
  <logger name="io.undertow" level="INFO"/>
  <logger name="org.xnio.nio" level="INFO"/>
  <logger name="org.thymeleaf" level="INFO"/>
  <logger name="springfox.documentation" level="INFO"/>
  <logger name="org.hibernate.validator" level="INFO"/>
  <logger name="com.netflix.loadbalancer" level="INFO"/>
  <logger name="com.netflix.hystrix" level="INFO"/>
  <logger name="com.netflix.zuul" level="INFO"/>
  <logger name="de.codecentric" level="INFO"/>
  <!-- cache INFO -->
  <logger name="net.sf.ehcache" level="INFO"/>
  <logger name="org.springframework.cache" level="INFO"/>
  <!-- cloud -->
  <logger name="org.apache.http" level="INFO"/>
  <logger name="com.netflix.discovery" level="INFO"/>
  <logger name="com.netflix.eureka" level="INFO"/>
  <!-- Business log -->
  <Logger name="com.xkcoding" level="DEBUG"/>

</configuration>
```

## 5. Configure the graylog console to receive log sources

1. Log in to 'graylog' and open a browser to access: http://localhost:9000

   Enter the 'username/password' information configured in 'docker-compose.yml'

   ! [login graylog] (http://static.xkcoding.com/spring-boot-demo/graylog/063124.jpg)

2. Set the source information

   ! [Set Inputs] (http://static.xkcoding.com/spring-boot-demo/graylog/063125.jpg)

   ! [image-20190423164748993] (http://static.xkcoding.com/spring-boot-demo/graylog/063121-1.jpg)

   ! [image-20190423164932488] (http://static.xkcoding.com/spring-boot-demo/graylog/063121.jpg)

   ! [image-20190423165120586] (http://static.xkcoding.com/spring-boot-demo/graylog/063122.jpg)

## 6. Start the Spring Boot project

After the startup is successful, return to the graylog page to view the log information

! [image-20190423165936711] (http://static.xkcoding.com/spring-boot-demo/graylog/063123.jpg)

## Reference

- graylog official download address: https://www.graylog.org/downloads#open-source

- graylog official docker image: https://hub.docker.com/r/graylog/graylog/

- Graylog image startup mode: http://docs.graylog.org/en/stable/pages/installation/docker.html

- graylog startup parameter configuration: http://docs.graylog.org/en/stable/pages/configuration/server.conf.html

  Note that the startup parameters need to be prefixed with 'GRAYLOG_'

- Log collection dependency: https://github.com/osiegmar/logback-gelf
