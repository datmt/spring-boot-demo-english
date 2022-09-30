# spring-boot-demo-session

> This demo mainly demonstrates how Spring Boot can achieve Session sharing through Spring Session and restart program Session without invalidation.

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>spring-boot-demo-session</artifactId>
    <version>1.0.0-SNAPSHOT</version>

    <name>spring-boot-demo-session</name>
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
            <groupId>org.springframework.session</groupId>
            <artifactId>spring-session-data-redis</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>

        <!-- object pool, --> must be introduced when using redis
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-pool2</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
        </dependency>
    </dependencies>

    <build>
        <finalName>spring-boot-demo-session</finalName>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

## application.yml

```yaml
server:
  port: 8080
  servlet:
    context-path: /demo
spring:
  session:
    store-type: redis
    redis:
      flush-mode: immediate
      namespace: "spring:session"
  redis:
    host: localhost
    port: 6379
    # Connection timeout (remember to add units, Duration)
    timeout: 10000ms
    # Redis has 16 shards by default, and here you configure the specific shards used
    # database: 0
    lettuce:
      pool:
        # The maximum number of connections in the connection pool (using a negative value to indicate no limit) defaults to 8
        max-active: 8
        # Connection pool maximum blocking wait time (using a negative value to indicate no limit) Default -1
        max-wait: -1ms
        # Maximum idle connections in connection pool Default 8
        max-idle: 8
        # Minimum idle connections in connection pool Default 0
        min-idle: 0
```

## Test

> test the scenario where the program is restarted, Session does not fail

1. Open a browser and visit Home: http://localhost:8080/demo/page/index
2. You are not logged in at first, so you will be redirected to the login page: http://localhost:8080/demo/page/login?redirect=true and click the Sign In button
3. After logging in, jump back to the homepage, and you can see that the homepage displays the token information.
4. Restart the program. Without closing the browser, you can refresh the homepage directly, and do not jump to the login page. Test successful!

## Reference

- Spring Session Official Documentation: https://docs.spring.io/spring-session/docs/current/reference/html5/guides/boot-redis.html#updating-dependencies
