# spring-boot-demo-task

> This demo demonstrates how Spring Boott can quickly implement scheduled tasks.

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>spring-boot-demo-task</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>spring-boot-demo-task</name>
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
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <finalName>spring-boot-demo-task</finalName>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

## TaskConfig.java

> is equivalent here to configuration in the configuration file
>
> ```properties
> spring.task.scheduling.pool.size=20
> spring.task.scheduling.thread-name-prefix=Job-Thread-
> ```

```java
/**
 * <p>
 * Scheduled task configuration, configure the thread pool, use different threads to perform tasks, improve efficiency
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-22 19:02
 */
@Configuration
@EnableScheduling
@ComponentScan(basePackages = {"com.xkcoding.task.job"})
public class TaskConfig implements SchedulingConfigurer {
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
    }

    /**
     * This is equivalent to profile configuration
     * {@code spring.task.scheduling.pool.size=20} - Maximum allowed number of threads.
     * {@code spring.task.scheduling.thread-name-prefix=Job-Thread- } - Prefix to use for the names of newly created threads.
     * {@link org.springframework.boot.autoconfigure.task.TaskSchedulingProperties}
     */
    @Bean
    public Executor taskExecutor() {
        return new ScheduledThreadPoolExecutor(20, new BasicThreadFactory.Builder().namingPattern("Job-Thread-%d").build());
    }
}
```

## TaskJob.java

```java
/**
 * <p>
 * Scheduled tasks
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-22 19:09
 */
@Component
@Slf4j
public class TaskJob {

    /**
     * Executes every 10s in standard time
     */
    @Scheduled(cron = "0/10 * * * * ?")
    public void job1() {
        log.info ("[job1] start execution :{}", DateUtil.formatDateTime(new Date()));
    }

    /**
     * Performed at intervals of 2s from the start time
     * Fixed intervals
     */
    @Scheduled(fixedRate = 2000)
    public void job2() {
        log.info ("[job2] start execution :{}", DateUtil.formatDateTime(new Date()));
    }

    /**
     * Starts at startup time and is performed at 4s intervals after a delay of 5s
     * Fixed waiting time
     */
    @Scheduled(fixedDelay = 4000, initialDelay = 5000)
    public void job3() {
        log.info ("[job3] start execution :{}", DateUtil.formatDateTime(new Date()));
    }
}
```

## application.yml

```yaml
server:
  port: 8080
  servlet:
    context-path: /demo
# The following configuration is equivalent to TaskConfig
#spring:
#  task:
#    scheduling:
#      pool:
#        size: 20
#      thread-name-prefix: Job-Thread-
```

## Reference

- Spring Boot Official Documentation: https://docs.spring.io/spring-boot/docs/2.1.0.RELEASE/reference/htmlsingle/#boot-features-task-execution-scheduling
