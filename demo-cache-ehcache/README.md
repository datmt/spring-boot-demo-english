# spring-boot-demo-cache-ehcache

> This demo mainly demonstrates how Spring Boot integrates ehcache using caching.

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>spring-boot-demo-cache-ehcache</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>spring-boot-demo-cache-ehcache</name>
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
            <artifactId>spring-boot-starter-cache</artifactId>
        </dependency>

        <dependency>
            <groupId>net.sf.ehcache</groupId>
            <artifactId>ehcache</artifactId>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
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
    </dependencies>

    <build>
        <finalName>spring-boot-demo-cache-ehcache</finalName>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

## SpringBootDemoCacheEhcacheApplication.java

```java
/**
 * <p>
 * Startup class
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-16 17:02
 */
@SpringBootApplication
@EnableCaching
public class SpringBootDemoCacheEhcacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootDemoCacheEhcacheApplication.class, args);
    }
}
```

## application.yml

```yaml
spring:
  cache:
    type: ehcache
    ehcache:
      config: classpath:ehcache.xml
logging:
  level:
    com.xkcoding: debug
```

## ehcache.xml

```xml
<!-- ehcache configuration -->
<ehcache
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:noNamespaceSchemaLocation="http://ehcache.org/ehcache.xsd"
        updateCheck="false">
    <!-- cache path, base_ehcache directory under User Directory - >
    <diskStore path="user.home/base_ehcache"/>

    <defaultCache
            maxElementsInMemory="20000"
            eternal="false"
            timeToIdleSeconds="120"
            timeToLiveSeconds="120"
            overflowToDisk="true"
            maxElementsOnDisk="10000000"
            diskPersistent="false"
            diskExpiryThreadIntervalSeconds="120"
            memoryStoreEvictionPolicy="LRU"/>

    <!--
    Cache file name: user, the same can configure multiple caches
    maxElementsInMemory: The maximum amount of storage in memory
    eternal: External storage
    overflowToDisk: Beyond the cache to disk
    diskPersistent: Disk persistence
    timeToLiveSeconds: Cache time
    diskExpiryThreadIntervalSeconds: Disk expiration time
    -->
    <cache name="user"
           maxElementsInMemory="20000"
           eternal="true"
           overflowToDisk="true"
           diskPersistent="false"
           timeToLiveSeconds="0"
           diskExpiryThreadIntervalSeconds="120"/>

</ehcache>
```

## UserServiceImpl.java

```java
/**
 * <p>
 * UserService
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-16 16:54
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

## UserServiceTest.java

```java
/**
 * <p>
 * ehcache test
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-16 16:58
 */
@Slf4j
public class UserServiceTest extends SpringBootDemoCacheEhcacheApplicationTests {

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
        userService.saveOrUpdate(new User(4L, "user4"));

        User user = userService.get(4L);
        log.debug("【user】= {}", user);
        View the log, only print the log of the saved user, the query is not triggered query log, so the cache takes effect
    }

    /**
     * Test deletion to see if redis has cached data
     */
    @Test
    public void deleteUser() {
        Query once so that cached data exists in the ehcache
        userService.get(1L);
        Delete to see if the ehcache exists for cached data
        userService.delete(1L);
    }
}
```

## Reference

- Ehcache: http://www.ehcache.org/documentation/
- Spring Boott Official Documentation: https://docs.spring.io/spring-boot/docs/2.1.0.RELEASE/reference/htmlsingle/#boot-features-caching-provider-ehcache2
- Blog: https://juejin.im/post/5b308de9518825748b56ae1d
