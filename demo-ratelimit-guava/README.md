# spring-boot-demo-ratelimit-guava

> This demo demonstrates how the Spring Boot project can implement throttling through AOP combined with Guava's RateLimiter, aiming to protect APIs from malicious and frequent access.

## 1. Primary code

### 1.1. pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <artifactId>spring-boot-demo-ratelimit-guava</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <packaging>jar</packaging>

  <name>spring-boot-demo-ratelimit-guava</name>
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
      <artifactId>spring-boot-starter-aop</artifactId>
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
    <finalName>spring-boot-demo-ratelimit-guava</finalName>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
      </plugin>
    </plugins>
  </build>

</project>
```

### 1.2. Define a current-limit annotation 'RateLimiter.java'

> Note that 'AliasFor' is used in the code to set the alias of a set of properties, so when getting annotations, you need to get it through the annotation tool class 'AnnotationUtils' provided by 'Spring', and you can't get it through 'AOP' parameter injection, otherwise the value of some properties will not be set.

```java
/**
 * <p>
 * Flow restriction annotation, added {@link AliasFor} must be obtained via {@link AnnotationUtils} to take effect
 *
 * @author yangkai.shen
 * @date Created in 2019-09-12 14:14
 * @see AnnotationUtils
 * </p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {
    int NOT_LIMITED = 0;

    /**
     * qps
     */
    @AliasFor("qps") double value() default NOT_LIMITED;

    /**
     * qps
     */
    @AliasFor("value") double qps() default NOT_LIMITED;

    /**
     * Timeout duration
     */
    int timeout() default 0;

    /**
     * Timeout unit
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
```

### 1.3. Define a slice 'RateLimiterAspect.java'

```java
/**
 * <p>
 * Flow restriction slices
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-09-12 14:27
 */
@Slf4j
@Aspect
@Component
public class RateLimiterAspect {
    private static final ConcurrentMap<String, com.google.common.util.concurrent.RateLimiter> RATE_LIMITER_CACHE = new ConcurrentHashMap<>();

    @Pointcut("@annotation(com.xkcoding.ratelimit.guava.annotation.RateLimiter)")
    public void rateLimit() {

    }

    @Around("rateLimit()")
    public Object pointcut(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Get RateLimiter annotations via AnnotationUtils.findAnnotation
        RateLimiter rateLimiter = AnnotationUtils.findAnnotation(method, RateLimiter.class);
        if (rateLimiter != null && rateLimiter.qps() > RateLimiter.NOT_LIMITED) {
            double qps = rateLimiter.qps();
            if (RATE_LIMITER_CACHE.get(method.getName()) == null) {
                Initialize QPS
                RATE_LIMITER_CACHE.put(method.getName(), com.google.common.util.concurrent.RateLimiter.create(qps));
            }

            log.debug("[{}]'s QPS is set to: {}", method.getName(), RATE_LIMITER_CACHE.get(method.getName()).getRate());
            Try to get a token
            if (RATE_LIMITER_CACHE.get(method.getName()) != null && ! RATE_LIMITER_CACHE.get(method.getName()).tryAcquire(rateLimiter.timeout(), rateLimiter.timeUnit())) {
                throw new RuntimeException ("Hand speed is too fast, slow down~");
            }
        }
        return point.proceed();
    }
}
```

### 1.4. Define two API interfaces for testing current limiting

```java
/**
 * <p>
 * Test
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-09-12 14:22
 */
@Slf4j
@RestController
public class TestController {

    @RateLimiter(value = 1.0, timeout = 300)
    @GetMapping("/test1")
    public Dict test1() {
        log.info ("[test1] was executed....) );
        return Dict.create().set("msg", "hello,world!"). set("description", "Don't want to see me all the time, don't believe you quickly refresh to see ~");
    }

    @GetMapping("/test2")
    public Dict test2() {
        log.info ("[test2] was executed....") );
        return Dict.create().set("msg", "hello,world!"). set ("description", "I have always been there, porphyrinum away from porphyria");
    }
}
```

## 2. Test

- When the test1 interface is not throttled

<img src="http://static.xkcoding.com/spring-boot-demo/ratelimit/guava/063719.jpg" alt="image-20190912155209716" style="zoom:50%;" />

- The test1 interface is frequently refreshed, triggering the flow throttling

<img src="http://static.xkcoding.com/spring-boot-demo/ratelimit/guava/063718-1.jpg" alt="image-20190912155229745" style="zoom:50%;" />

- The test2 interface does not limit the flow and can be refreshed all the time

<img src="http://static.xkcoding.com/spring-boot-demo/ratelimit/guava/063718.jpg" alt="image-20190912155146012" style="zoom:50%;" />

## 3. reference

- [RateLimiter in Guava Interpretation of the Principle of Current Limiting] (https://juejin.im/post/5bb48d7b5188255c865e31bc)

- [Limiting Flow with Guava's RateLimiter] (https://my.oschina.net/hanchao/blog/1833612)

