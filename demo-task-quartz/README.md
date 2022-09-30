# spring-boot-demo-task-quartz

> This demo demonstrates how Spring Boot integrates with Quartz scheduled tasks and implements the management of scheduled tasks, including adding scheduled tasks, deleting scheduled tasks, pausing scheduled tasks, resuming scheduled tasks, modifying scheduled task start time, and querying the scheduled task list.

## Backend

### Initialization

Under 'init/dbTables', select the table structure that Quartz needs and create the table manually.

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>spring-boot-demo-task-quartz</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>spring-boot-demo-task-quartz</name>
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
        <mybatis.mapper.version>2.1.0</mybatis.mapper.version>
        <mybatis.pagehelper.version>1.2.10</mybatis.pagehelper.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-quartz</artifactId>
        </dependency>

        <dependency>
            <groupId>tk.mybatis</groupId>
            <artifactId>mapper-spring-boot-starter</artifactId>
            <version>${mybatis.mapper.version}</version>
        </dependency>

        <dependency>
            <groupId>com.github.pagehelper</groupId>
            <artifactId>pagehelper-spring-boot-starter</artifactId>
            <version>${mybatis.pagehelper.version}</version>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
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

        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>

    <build>
        <finalName>spring-boot-demo-task-quartz</finalName>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

### application.yml

```yaml
server:
  port: 8080
  servlet:
    context-path: /demo
spring:
# Omit the remaining configurations, please clone for details in this project
# ......
  quartz:
    See org.springframework.boot.autoconfigure.quartz.QuartzProperties
    job-store-type: jdbc
    wait-for-jobs-to-complete-on-shutdown: true
    scheduler-name: SpringBootDemoScheduler
    properties:
      org.quartz.threadPool.threadCount: 5
      org.quartz.threadPool.threadPriority: 5
      org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread: true
      org.quartz.jobStore.misfireThreshold: 5000
      org.quartz.jobStore.class: org.quartz.impl.jdbcjobstore.JobStoreTX
      org.quartz.jobStore.driverDelegateClass: org.quartz.impl.jdbcjobstore.StdJDBCDelegate
      # In the first step of the scheduling process, that is, when pulling triggers to be triggered, it is a locked state, that is, there will not be multiple threads pulling to the same trigger at the same time, so the danger of repeated scheduling is avoided. Reference: https://segmentfault.com/a/1190000015492260
      org.quartz.jobStore.acquireTriggersWithinLock: true

# Omit the remaining configurations, please clone for details in this project
# ......
```

---

> the rest of the code of the backend, please clone this project, to see the specific code

## Front end

> front-end page, please clone this project, to see the specific code

## Launch

1. clone This project
2. Initialize the table
3. Launch 'SpringBootDemoTaskQuartzApplication.java'
4. Open a browser to view the http://localhost:8080/demo/job.html 

! [image-20181126214007372] (http://static.xkcoding.com/spring-boot-demo/task/quartz/064006-1.jpg)

! [image-20181126214109926] (http://static.xkcoding.com/spring-boot-demo/task/quartz/064008.jpg)

! [image-20181126214212905] (http://static.xkcoding.com/spring-boot-demo/task/quartz/064009-1.jpg)

! [image-20181126214138641] (http://static.xkcoding.com/spring-boot-demo/task/quartz/064009.jpg)

! [image-20181126214250757] (http://static.xkcoding.com/spring-boot-demo/task/quartz/064007.jpg)

## Reference

- Spring Boot Official Documentation: https://docs.spring.io/spring-boot/docs/2.1.0.RELEASE/reference/htmlsingle/#boot-features-quartz

- Quartz Official Documentation: http://www.quartz-scheduler.org/documentation/quartz-2.2.x/quick-start.html

- Quartz Duplicate Scheduling Problem: https://segmentfault.com/a/1190000015492260

- About Quartz timed task status ('QRTZ_TRIGGERS' field in the 'TRIGGER_STATE' table)

  ! [image-20181126171110378] (http://static.xkcoding.com/spring-boot-demo/task/quartz/064006.jpg)

- Vue.js Official Documentation: https://cn.vuejs.org/v2/guide/

- Element-UI Official Documentation: http://element-cn.eleme.io/#/zh-CN
