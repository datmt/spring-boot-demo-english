# spring-boot-demo-logback

> This demo mainly demonstrates how to use logback logs during the operation of the logback program, and how to configure logback, which can generate console logs and file log records at the same time, and file logs are split in date and size.

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<artifactId>spring-boot-demo-logback</artifactId>
	<version>1.0.0-SNAPSHOT</version>
	<packaging>jar</packaging>

	<name>spring-boot-demo-logback</name>
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

		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<optional>true</optional>
		</dependency>
	</dependencies>

	<build>
		<finalName>spring-boot-demo-logback</finalName>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>

</project>
```

## SpringBootDemoLogbackApplication.java

```java
/**
 * <p>
 * Startup class
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-09-30 23:16
 */
@SpringBootApplication
@Slf4j
public class SpringBootDemoLogbackApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(SpringBootDemoLogbackApplication.class, args);
		int length = context.getBeanDefinitionNames().length;
		log.trace("Spring boot started initialized {} beans", length);
		log.debug ("Spring boot started initialized {} beans", length);
		log.info ("Spring boot startup initialized {} beans", length);
		log.warn("Spring boot started initialized {} beans", length);
		log.error("Spring boot startup initialized {} beans", length);
		try {
			int i = 0;
			int j = 1 / i;
		} catch (Exception e) {
			log.error("[SpringBootDemoLogbackApplication] startup exception:", e);
		}
	}
}
```

## logback-spring.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
	<include resource="org/springframework/boot/logging/logback/defaults.xml"/>
	<appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
		<filter class="ch.qos.logback.classic.filter.LevelFilter">
			<level>INFO</level>
		</filter>
		<encoder>
			<pattern>%date [%thread] %-5level [%logger{50}] %file:%line - %msg%n</pattern>
			<charset>UTF-8</charset>
		</encoder>
	</appender>

	<appender name="FILE_INFO" class="ch.qos.logback.core.rolling.RollingFileAppender">
		<!-- if you just want the Info level log, just filter info or output the Error log, because the level of Error is high, so we use the following strategy to avoid the output of the Error log - >
		<filter class="ch.qos.logback.classic.filter.LevelFilter">
			<!-- filter Error-->
			<level>ERROR</level>
			<!-- matches to prohibit -->
			<onMatch>DENY</onMatch>
			<!-- no match is allowed - >
			<onMismatch>ACCEPT</onMismatch>
		</filter>
		<!-- log name, if there is no File property, then only the file path rule of FileNamePattern is used If there is a sum at the same time, <File><FileNamePattern>then the current day log is<File>, tomorrow will automatically rename today's log to today's date. That is, the<File> logs are all of the same day. -->
		<!--<File>logs/info.spring-boot-demo-logback.log</File>-->
		<!-- scrolling strategy, scroll by time TimeBasedRollingPolicy-->
		<rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
			<!-- file path defines how the log is split — archiving each day's log into a file to prevent the log from filling up the entire disk space – >
			<FileNamePattern>logs/spring-boot-demo-logback/info.created_on_%d{yyyy-MM-dd}.part_%i.log</FileNamePattern>
			<!-- only keep logs for the last 90 days - >
			<maxHistory>90</maxHistory>
			<!-- used to specify the upper limit size of the log file, then at this value, the old log - > is deleted
			<!--<totalSizeCap>1GB</totalSizeCap>-->
			<timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
				<!-- maxFileSize: This is the size of the active file, the default value is 10MB, this article is set to 1KB, just to demonstrate -->
				<maxFileSize>2MB</maxFileSize>
			</timeBasedFileNamingAndTriggeringPolicy>
		</rollingPolicy>
		<!--<triggeringPolicy class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">-->
		<!--<maxFileSize>1KB</maxFileSize>-->
		<!--</triggeringPolicy>-->
		<encoder>
			<pattern>%date [%thread] %-5level [%logger{50}] %file:%line - %msg%n</pattern>
			<charset>UTF-8</charset> <!-- Set the character set here -->
		</encoder>
	</appender>

	<appender name="FILE_ERROR" class="ch.qos.logback.core.rolling.RollingFileAppender">
		<!-- if you just want Error-level logs, you need to filter them, the default is info level, ThresholdFilter-->
		<filter class="ch.qos.logback.classic.filter.ThresholdFilter">
			<level>Error</level>
		</filter>
		<!-- log name, if there is no File property, then only the file path rule of FileNamePattern is used If there is a sum at the same time, <File><FileNamePattern>then the current day log is<File>, tomorrow will automatically rename today's log to today's date. That is, the<File> logs are all of the same day. -->
		<!--<File>logs/error.spring-boot-demo-logback.log</File>-->
		<!-- scrolling strategy, scroll by time TimeBasedRollingPolicy-->
		<rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
			<!-- file path defines how the log is split — archiving each day's log into a file to prevent the log from filling up the entire disk space – >
			<FileNamePattern>logs/spring-boot-demo-logback/error.created_on_%d{yyyy-MM-dd}.part_%i.log</FileNamePattern>
			<!-- only keep logs for the last 90 days - >
			<maxHistory>90</maxHistory>
			<timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
				<!-- maxFileSize: This is the size of the active file, the default value is 10MB, this article is set to 1KB, just to demonstrate -->
				<maxFileSize>2MB</maxFileSize>
			</timeBasedFileNamingAndTriggeringPolicy>
		</rollingPolicy>
		<encoder>
			<pattern>%date [%thread] %-5level [%logger{50}] %file:%line - %msg%n</pattern>
			<charset>UTF-8</charset> <!-- Set the character set here -->
		</encoder>
	</appender>

	<root level="info">
		<appender-ref ref="CONSOLE"/>
		<appender-ref ref="FILE_INFO"/>
		<appender-ref ref="FILE_ERROR"/>
	</root>
</configuration>
```

