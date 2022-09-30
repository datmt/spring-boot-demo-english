# spring-boot-demo-actuator

> This demo mainly demonstrates how to check the running of a project with actuator in Spring Boot

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<artifactId>spring-boot-demo-actuator</artifactId>
	<version>1.0.0-SNAPSHOT</version>
	<packaging>jar</packaging>

	<name>spring-boot-demo-actuator</name>
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
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>

		<dependency>
			<groupId>org.springframework.security</groupId>
			<artifactId>spring-security-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>

	<build>
		<finalName>spring-boot-demo-actuator</finalName>
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
# To access endpoint information, you need to configure a user name and password
spring:
  security:
    user:
      name: xkcoding
      password: 123456
management:
  # The port used by the endpoint information interface in order to be separated from the port used by the main system interface
  server:
    port: 8090
    servlet:
      context-path: /sys
  # Endpoint health, default value "never", set to "always" to show hard disk usage and threading conditions
  endpoint:
    health:
      show-details: always
  # Set what the endpoint exposes by default, default ["health", "info"], set "*" to represent exposure to all accessible endpoints
  endpoints:
    web:
      exposure:
        include: '*'
```

## Endpoint exposure address

After the project is run, all accessible port information is viewed in the Console
1. Open a browser, visit: http://localhost:8090/sys/actuator/mappings, enter the username (xkcoding) password (123456) to see all the mapping information
2. Access: http://localhost:8090/sys/actuator/beans, enter your username (xkcoding) password (123456) to see all Spring-managed beans
3. For the remaining accessible paths, see the documentation

## Reference

- Actutator Documentation: https://docs.spring.io/spring-boot/docs/2.0.5.RELEASE/reference/htmlsingle/#production-ready
- For specific paths to access, see: https://docs.spring.io/spring-boot/docs/2.0.5.RELEASE/reference/htmlsingle/#production-ready-endpoints
