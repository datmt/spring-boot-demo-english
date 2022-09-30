# spring-boot-demo-cache-redis

> This demo mainly demonstrates how Spring Boot can integrate redis, manipulate data in redis, and use redis to cache data. Connection pooling uses Lettuce.

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>spring-boot-demo-cache-redis</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>spring-boot-demo-cache-redis</name>
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
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>

        <!-- object pool, --> must be introduced when using redis
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-pool2</artifactId>
        </dependency>

        <!-- Introduce the jackson object json transform -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-json</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
        </dependency>

        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>

    <build>
        <finalName>spring-boot-demo-cache-redis</finalName>
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
  redis:
    host: localhost
    # Connection timeout (remember to add units, Duration)
    timeout: 10000ms
    # Redis has 16 shards by default, and here you configure the specific shards used
    # database: 0
    lettuce:
      pool:
        # The maximum number of connections in the connection pool (using a negative value to indicate no limit) defaults to 8
        max-active: 8
        # Connection pool maximum blocking wait time (using a negative value to indicate no limit) Default -1
        max-wait: -1ms
        # Maximum idle connections in connection pool Default 8
        max-idle: 8
        # Minimum idle connections in connection pool Default 0
        min-idle: 0
  cache:
    # Generally speaking, it is not configured, and Spring Cache will assemble itself according to the dependent packages
    type: redis
logging:
  level:
    com.xkcoding: debug
```

## RedisConfig.java

```java
/**
 * <p>
 * Redis configuration
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-15 16:41
 */
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
@EnableCaching
public class RedisConfig {

    /**
     * By default, templates can only support RedisTemplate<String, String>, that is, only strings can be saved, so serialization is supported
     */
    @Bean
    public RedisTemplate<String, Serializable> redisCacheTemplate(LettuceConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Serializable> template = new RedisTemplate<>();
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    /**
     * When configuring the cache configuration when using annotations, the default is serialized deserialized form, plus this configuration is in json form
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        Configure serialization
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        RedisCacheConfiguration redisCacheConfiguration = config.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())). serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        return RedisCacheManager.builder(factory).cacheDefaults(redisCacheConfiguration).build();
    }
}
```

## UserServiceImpl.java

```java
/**
 * <p>
 * UserService
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-15 16:45
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    /**
     * Simulated database
     */
    private static final Map<Long, User> DATABASES = Maps.newConcurrentMap();

    /**
     * Initialize data
     */
    static {
        DATABASES.put(1L, new User(1L, "user1"));
        DATABASES.put(2L, new User(2L, "user2"));
        DATABASES.put(3L, new User(3L, "user3"));
    }

    /**
     * Save or modify users
     *
     * @param user user object
     * @return Operation result
     */
    @CachePut(value = "user", key = "#user.id")
    @Override
    public User saveOrUpdate(User user) {
        DATABASES.put(user.getId(), user);
        log.info ("Save user [user] = {}", user);
        return user;
    }

    /**
     * Get users
     *
     * @param id key value
     * @return Returns results
     */
    @Cacheable(value = "user", key = "#id")
    @Override
    public User get(Long id) {
        We assume that a read is made from a database
        log.info ("query user [id] = {}", id);
        return DATABASES.get(id);
    }

    /**
     * Delete
     *
     * @param id key value
     */
    @CacheEvict(value = "user", key = "#id")
    @Override
    public void delete(Long id) {
        DATABASES.remove(id);
        log.info ("Delete user [id] = {}", id);
    }
}
```

## RedisTest.java

> mainly tests data in 'Redis' using 'RedisTemplate' operations:
>
> - opsForValue: String
> - opsForZSet: corresponds to ZSet (ordered set)
> - opsForHash: Corresponding to Hash (hash)
> - opsForList: Corresponds to List
> - opsForSet: Set
> - opsForGeo:** for GEO (geolocation)

```java
/**
 * <p>
 * Redis test
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-15 17:17
 */
@Slf4j
public class RedisTest extends SpringBootDemoCacheRedisApplicationTests {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate<String, Serializable> redisCacheTemplate;

    /**
     * Test Redis operations
     */
    @Test
    public void get() {
        To test thread safety, the program ends to see if the value of count in redis is 1000
        ExecutorService executorService = Executors.newFixedThreadPool(1000);
        IntStream.range(0, 1000).forEach(i -> executorService.execute(() -> stringRedisTemplate.opsForValue().increment("count", 1)));

        stringRedisTemplate.opsForValue().set("k1", "v1");
        String k1 = stringRedisTemplate.opsForValue().get("k1");
        log.debug("【k1】= {}", k1);

        The following demonstrates the integration, and the specific Redis command can refer to the official documentation
        String key = "xkcoding:user:1";
        redisCacheTemplate.opsForValue().set(key, new User(1L, "user1"));
        Corresponding to String (string)
        User user = (User) redisCacheTemplate.opsForValue().get(key);
        log.debug("【user】= {}", user);
    }
}

```

## UserServiceTest.java

> primarily tests whether using Redis caching works

```java
/**
 * <p>
 * Redis - Caching test
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-15 16:53
 */
@Slf4j
public class UserServiceTest extends SpringBootDemoCacheRedisApplicationTests {
    @Autowired
    private UserService userService;

    /**
     * Get twice, view the log verification cache
     */
    @Test
    public void getTwice() {
        Simulates a user whose query has an id of 1
        User user1 = userService.get(1L);
        log.debug("【user1】= {}", user1);

        Query again
        User user2 = userService.get(1L);
        log.debug("【user2】= {}", user2);
        Review the log and print it only once to prove that the cache is in effect
    }

    /**
     * Save first, then query, view the log verification cache
     */
    @Test
    public void getAfterSave() {
        userService.saveOrUpdate (new User(4L, "Test Chinese"));

        User user = userService.get(4L);
        log.debug("【user】= {}", user);
        View the log, only print the log of the saved user, the query is not triggered query log, so the cache takes effect
    }

    /**
     * Test deletion to see if redis has cached data
     */
    @Test
    public void deleteUser() {
        Query once so that cached data exists in redis
        userService.get(1L);
        Delete to see if redis has cached data
        userService.delete(1L);
    }

}
```

## Reference

- Spring-data-redis Official Documentation: https://docs.spring.io/spring-data/redis/docs/2.0.1.RELEASE/reference/html/
- Redis documentation: https://redis.io/documentation
- Redis Chinese Documentation: http://www.redis.cn/commands.html
