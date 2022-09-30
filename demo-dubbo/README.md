# spring-boot-demo-dubbo

> This demo mainly demonstrates how Spring Boot integrates with Dubbo, demo is divided into 3 modules, namely the common module 'spring-boot-demo-dubbo-common', the service provider 'spring-boot-demo-dubbo-provider', the service caller 'spring-boot-demo-dubbo-consumer'

## Note

The registry in this example uses zookeeper, and the author runs zookeeper in docker mode when writing this demo

1. Download the image: 'docker pull wurstmeister/zookeeper'

2. Run the container: 'docker run -d -p 2181:2181 -p 2888:2888 -p 2222:22 -p 3888:3888 --name zk wurstmeister/zookeeper'

3. Stop container: 'docker stop zk'

4. Start the container: 'docker start zk'

## Run the steps

1. Go to the service provider's 'spring-boot-demo-dubbo-provider' directory and run 'SpringBootDemoDubboProviderApplication.java'
2. Go to the service caller 'spring-boot-demo-dubbo-consumer' directory and run 'SpringBootDemoDubboConsumerApplication.java'
3. Open the browser input http://localhost:8080/demo/sayHello and observe the browser output and the console output logs of the service provider and the service caller

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>spring-boot-demo-dubbo</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <modules>
        <module>spring-boot-demo-dubbo-common</module>
        <module>spring-boot-demo-dubbo-provider</module>
        <module>spring-boot-demo-dubbo-consumer</module>
    </modules>
    <packaging>pom</packaging>

    <name>spring-boot-demo-dubbo</name>
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
        <dubbo.starter.version>2.0.0</dubbo.starter.version>
        <zkclient.version>0.10</zkclient.version>
    </properties>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

## Reference

1. dubbo official website:http://dubbo.apache.org/zh-cn/
2. [Super detailed, novice can understand!] Build a simple distributed service with SpringBoot+Dubbo] (https://segmentfault.com/a/1190000017178722#articleHeader20)

