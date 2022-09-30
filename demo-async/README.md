# spring-boot-demo-async

> This demo primarily demonstrates how Spring Boott can use natively provided support for asynchronous tasks to perform tasks asynchronously.

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>spring-boot-demo-async</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>spring-boot-demo-async</name>
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
            <artifactId>spring-boot-starter</artifactId>
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
        <finalName>spring-boot-demo-async</finalName>
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
spring:
  task:
    execution:
      pool:
        # Maximum number of threads
        max-size: 16
        # Number of core threads
        core-size: 16
        # Time to live
        keep-alive: 10s
        # Queue size
        queue-capacity: 100
        # Whether to allow core threads to time out
        allow-core-thread-timeout: true
      # Thread name prefix
      thread-name-prefix: async-task-
```

## SpringBootDemoAsyncApplication.java

```java
/**
 * <p>
 * Launcher
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-29 10:28
 */
@EnableAsync
@SpringBootApplication
public class SpringBootDemoAsyncApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootDemoAsyncApplication.class, args);
    }

}
```

## TaskFactory.java

```java
/**
 * <p>
 * Mission Factory
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-29 10:37
 */
@Component
@Slf4j
public class TaskFactory {

    /**
     * Simulate an asynchronous mission for 5 seconds
     */
    @Async
    public Future<Boolean> asyncTask1() throws InterruptedException {
        doTask("asyncTask1", 5);
        return new AsyncResult<>(Boolean.TRUE);
    }

    /**
     * Simulate an asynchronous task for 2 seconds
     */
    @Async
    public Future<Boolean> asyncTask2() throws InterruptedException {
        doTask("asyncTask2", 2);
        return new AsyncResult<>(Boolean.TRUE);
    }

    /**
     * Simulate an asynchronous task for 3 seconds
     */
    @Async
    public Future<Boolean> asyncTask3() throws InterruptedException {
        doTask("asyncTask3", 3);
        return new AsyncResult<>(Boolean.TRUE);
    }

    /**
     * Simulate 5 seconds of synchronous tasks
     */
    public void task1() throws InterruptedException {
        doTask("task1", 5);
    }

    /**
     * Simulate a 2-second sync task
     */
    public void task2() throws InterruptedException {
        doTask("task2", 2);
    }

    /**
     * Simulate 3 seconds of synchronous tasks
     */
    public void task3() throws InterruptedException {
        doTask("task3", 3);
    }

    private void doTask(String taskName, Integer time) throws InterruptedException {
        log.info("{}Start execution, current thread name [{}]", taskName, Thread.currentThread().getName());
        TimeUnit.SECONDS.sleep(time);
        log.info("{} execution successful, current thread name [{}] ", taskName, Thread.currentThread().getName());
    }
}
```

## TaskFactoryTest.java

```java
/**
 * <p>
 * Test tasks
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-29 10:49
 */
@Slf4j
public class TaskFactoryTest extends SpringBootDemoAsyncApplicationTests {
    @Autowired
    private TaskFactory task;

    /**
     * Test asynchronous tasks
     */
    @Test
    public void asyncTaskTest() throws InterruptedException, ExecutionException {
        long start = System.currentTimeMillis();
        Future<Boolean> asyncTask1 = task.asyncTask1();
        Future<Boolean> asyncTask2 = task.asyncTask2();
        Future<Boolean> asyncTask3 = task.asyncTask3();

        Calling get() blocks the main thread
        asyncTask1.get();
        asyncTask2.get();
        asyncTask3.get();
        long end = System.currentTimeMillis();

        log.info ("Asynchronous task is all executed, total time: {} ms", (end - start));
    }

    /**
     * Test synchronization tasks
     */
    @Test
    public void taskTest() throws InterruptedException {
        long start = System.currentTimeMillis();
        task.task1();
        task.task2();
        task.task3();
        long end = System.currentTimeMillis();

        log.info ("Synchronization task is all executed, total time: {} ms", (end - start));
    }
}
```

## Run the result

### Asynchronous tasks

```bash
2018-12-29 10:57:28.511 INFO 3134 --- [ async-task-3] com.xkcoding.async.task.TaskFactory : asyncTask3 starts executing, current thread name [async-task-3]
2018-12-29 10:57:28.511 INFO 3134 --- [ async-task-1] com.xkcoding.async.task.TaskFactory : asyncTask1 starts executing, current thread name [async-task-1]
2018-12-29 10:57:28.511 INFO 3134 --- [ async-task-2] com.xkcoding.async.task.TaskFactory : asyncTask2 starts executing, current thread name [async-task-2]
2018-12-29 10:57:30.514 INFO 3134 --- [ async-task-2] com.xkcoding.async.task.TaskFactory : asyncTask2 executed successfully, current thread name [async-task-2]
2018-12-29 10:57:31.516 INFO 3134 --- [ async-task-3] com.xkcoding.async.task.TaskFactory : asyncTask3 executed successfully, current thread name [async-task-3]
2018-12-29 10:57:33.517 INFO 3134 --- [ async-task-1] com.xkcoding.async.task.TaskFactory : asyncTask1 executed successfully, current thread name [async-task-1]
2018-12-29 10:57:33.517 INFO 3134 --- [ main] com.xkcoding.async.task.TaskFactoryTest : The asynchronous task has all finished executing, total time: 5015 milliseconds
```

### Sync tasks

```bash
2018-12-29 10:55:49.830 INFO 3079 --- [ main] com.xkcoding.async.task.TaskFactory : task1 starts executing, current thread name [main]
2018-12-29 10:55:54.834 INFO 3079 --- [ main] com.xkcoding.async.task.TaskFactory : task1 executed successfully, current thread name [main]
2018-12-29 10:55:54.835 INFO 3079 --- [ main] com.xkcoding.async.task.TaskFactory : task2 starts executing, current thread name [main]
2018-12-29 10:55:56.839 INFO 3079 --- [ main] com.xkcoding.async.task.TaskFactory : task2 executed successfully, current thread name [main]
2018-12-29 10:55:56.839 INFO 3079 --- [ main] com.xkcoding.async.task.TaskFactory : task3 starts executing, current thread name [main]
2018-12-29 10:55:59.843 INFO 3079 --- [ main] com.xkcoding.async.task.TaskFactory : task3 executed successfully, current thread name [main]
2018-12-29 10:55:59.843 INFO 3079 --- [ main] com.xkcoding.async.task.TaskFactoryTest : The synchronization task is all executed, total time: 10023 ms
```

## Reference

- Configuration of the Spring Boot asynchronous task thread pool Refer to the official documentation: https://docs.spring.io/spring-boot/docs/2.1.0.RELEASE/reference/htmlsingle/#boot-features-task-execution-scheduling
