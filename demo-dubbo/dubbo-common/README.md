# spring-boot-demo-dubbo-common

> This module is primarily used for public parts, primarily for utility classes, entities, and interface definitions for service providers/callers

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>spring-boot-demo-dubbo</artifactId>
        <groupId>com.xkcoding</groupId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>spring-boot-demo-dubbo-common</artifactId>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
    </properties>

    <build>
        <finalName>spring-boot-demo-dubbo-common</finalName>
    </build>

</project>
```

## HelloService.java

```java
/**
 * <p>
 * Hello service interface
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-25 16:56
 */
public interface HelloService {
    /**
     * Say hello
     *
     * @param name name
     * @return Say hello
     */
    String sayHello(String name);
}
```

