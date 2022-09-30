# spring-boot-demo-admin

> This demo mainly demonstrates how Spring Boot integrates with the Admin control station to monitor and manage the Spring Boot application, which are two modules: admin server and admin client.

## Run the steps

1. Enter the 'spring-boot-demo-admin-server' server and start the control desk server
2. Enter the 'spring-boot-demo-admin-client' client, start the client program, and register with the server
3. Observe the running status of the client program on the server side

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <parent>
    <artifactId>spring-boot-demo</artifactId>
    <groupId>com.xkcoding</groupId>
    <version>1.0.0-SNAPSHOT</version>
  </parent>
  <modelVersion>4.0.0</modelVersion>

  <artifactId>spring-boot-demo-admin</artifactId>
  <packaging>pom</packaging>

  <properties>
    <spring-boot-admin.version>2.1.0</spring-boot-admin.version>
  </properties>

  <modules>
    <module>spring-boot-demo-admin-client</module>
    <module>spring-boot-demo-admin-server</module>
  </modules>

  <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>de.codecentric</groupId>
        <artifactId>spring-boot-admin-dependencies</artifactId>
        <version>${spring-boot-admin.version}</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>

</project>
```

