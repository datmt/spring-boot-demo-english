# spring-boot-demo-ratelimit-redis

> This demo mainly demonstrates how the Spring Boot project implements distributed throttling through AOP combined with Redis + Lua scripts, aiming to protect the API from malicious and frequent access, and is an upgraded version of 'spring-boot-demo-ratelimit-guava'.

## 1. Primary code

### 1.1. pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <artifactId>spring-boot-demo-ratelimit-redis</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <packaging>jar</packaging>

  <name>spring-boot-demo-ratelimit-redis</name>
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
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>

    <!-- object pool, --> must be introduced when using redis
    <dependency>
      <groupId>org.apache.commons</groupId>
      <artifactId>commons-pool2</artifactId>
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

    <dependency>
      <groupId>org.projectlombok</groupId>
      <artifactId>lombok</artifactId>
      <optional>true</optional>
    </dependency>
  </dependencies>

  <build>
    <finalName>spring-boot-demo-ratelimit-redis</finalName>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
      </plugin>
    </plugins>
  </build>

</project>
```

### 1.2. Flow restriction annotations

```java
/**
 * <p>
 * Flow restriction annotation, added {@link AliasFor} must be obtained via {@link AnnotationUtils} to take effect
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-09-30 10:31
 * @see AnnotationUtils
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {
    long DEFAULT_REQUEST = 10;

    /**
     * max Maximum number of requests
     */
    @AliasFor("max") long value() default DEFAULT_REQUEST;

    /**
     * max Maximum number of requests
     */
    @AliasFor("value") long max() default DEFAULT_REQUEST;

    /**
     * Flow restriction key
     */
    String key() default "";

    /**
     * Timeout timeout, default 1 minute
     */
    long timeout() default 1;

    /**
     * Timeout unit, default minutes
     */
    TimeUnit timeUnit() default TimeUnit.MINUTES;
}
```

### 1.3. AOP handles stream throttling

```java
/**
 * <p>
 * Flow restriction slices
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-09-30 10:30
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RateLimiterAspect {
    private final static String SEPARATOR = ":";
    private final static String REDIS_LIMIT_KEY_PREFIX = "limit:";
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisScript<Long> limitRedisScript;

    @Pointcut("@annotation(com.xkcoding.ratelimit.redis.annotation.RateLimiter)")
    public void rateLimit() {

    }

    @Around("rateLimit()")
    public Object pointcut(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Get RateLimiter annotations via AnnotationUtils.findAnnotation
        RateLimiter rateLimiter = AnnotationUtils.findAnnotation(method, RateLimiter.class);
        if (rateLimiter != null) {
            String key = rateLimiter.key();
            By default, the class name + method name is used as the key prefix for throttling
            if (StrUtil.isBlank(key)) {
                key = method.getDeclaringClass().getName()+StrUtil.DOT+method.getName();
            }
            The key of the final throttling is prefix + IP address
            TODO: At this time, you need to consider the case of multi-user access on the local area network, so it is more reasonable for key to add method parameters later
            key = key + SEPARATOR + IpUtil.getIpAddr();

            long max = rateLimiter.max();
            long timeout = rateLimiter.timeout();
            TimeUnit timeUnit = rateLimiter.timeUnit();
            boolean limited = shouldLimited(key, max, timeout, timeUnit);
            if (limited) {
                throw new RuntimeException ("Hand speed is too fast, slow down~");
            }
        }

        return point.proceed();
    }

    private boolean shouldLimited(String key, long max, long timeout, TimeUnit timeUnit) {
        The final key format is:
        limit:custom key:IP
        limit:class name. Method name: IP
        key = REDIS_LIMIT_KEY_PREFIX + key;
        Use units milliseconds uniformly
        long ttl = timeUnit.toMillis(timeout);
        The number of milliseconds at the current time
        long now = Instant.now().toEpochMilli();
        long expired = now - ttl;
        Note that you must switch to String here, otherwise you will report an error java.lang.Long cannot be cast to java.lang.String
        Long executeTimes = stringRedisTemplate.execute(limitRedisScript, Collections.singletonList(key), now + "", ttl + "", expired + "", max + "");
        if (executeTimes != null) {
            if (executeTimes == 0) {
                log.error("[{}]The upper limit of access has been reached in {} milliseconds per unit time, the current interface limit {}", key, ttl, max);
                return true;
            } else {
                log.info("[{}]Access {} 次", key, ttl, executeTimes);
                return false;
            }
        }
        return false;
    }
}
```

### 1.4. lua script

```lua
-- Subscripts start at 1
local key = KEYS[1]
local now = tonumber(ARGV[1])
local ttl = tonumber(ARGV[2])
local expired = tonumber(ARGV[3])
-- Maximum number of visits
local max = tonumber(ARGV[4])

-- Purge outdated data
-- Remove all elements within the specified score interval, and expired is the expired score
-- Based on the current time milliseconds - the number of milliseconds that have been timed out, the expiration time expired
redis.call('zremrangebyscore', key, 0, expired)

-- Gets the current number of elements in zset
local current = tonumber(redis.call('zcard', key))
local next = current + 1

if next > max then
  -- Stream limit size reached Returns 0
  return 0;
else
  -- Add a value to zset with an element with a score of the current timestamp, [value,score]
  redis.call("zadd", key, now, now)
  -- The expiration time of zset is reset for each access, in milliseconds
  redis.call("pexpire", key, ttl)
  return next
end
```

### 1.5. Interface testing

```java
/**
 * <p>
 * Test
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-09-30 10:30
 */
@Slf4j
@RestController
public class TestController {

    @RateLimiter(value = 5)
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

    @RateLimiter (value = 2, key = "Test custom key")
    @GetMapping("/test3")
    public Dict test3() {
        log.info ("[test3] was executed....) );
        return Dict.create().set("msg", "hello,world!"). set("description", "Don't want to see me all the time, don't believe you quickly refresh to see ~");
    }
}
```

### 1.6. For the rest of the code, see demo

## 2. Test

- Console printing when the flow limit is triggered

! [image-20190930155856711] (http://static.xkcoding.com/spring-boot-demo/ratelimit/redis/063812.jpg)

- Redis data when the flow throttling is triggered

! [image-20190930155735300] (http://static.xkcoding.com/spring-boot-demo/ratelimit/redis/063813.jpg)

## 3. reference

- [distributed current-limiting implementation of mica-plus-redis] (https://github.com/lets-mica/mica/tree/master/mica-plus-redis)
- [Java Concurrency: Redis + Lua Practice for Distributed Application Throttling] (https://segmentfault.com/a/1190000016042927)
