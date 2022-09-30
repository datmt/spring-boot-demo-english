# spring-boot-demo-zookeeper

> This demo mainly demonstrates how to use Spring Boot integrated Zookeeper to implement distributed locks in conjunction with AOP.

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>spring-boot-demo-zookeeper</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>spring-boot-demo-zookeeper</name>
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
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>

        <!-- curator version 4.1.0 corresponds to zookeeper version 3.5.x -->
        <!-- curator corresponds to the zookeeper version: https://curator.apache.org/zk-compatibility.html -->
        <dependency>
            <groupId>org.apache.curator</groupId>
            <artifactId>curator-recipes</artifactId>
            <version>4.1.0</version>
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
        <finalName>spring-boot-demo-zookeeper</finalName>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

## ZkProps.java

```java
/**
 * <p>
 * Zookeeper configuration items
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-27 14:47
 */
@Data
@ConfigurationProperties(prefix = "zk")
public class ZkProps {
    /**
     * Connection address
     */
    private String url;

    /**
     * Timeout (milliseconds), default 1000
     */
    private int timeout = 1000;

    /**
     * Number of retries, default 3
     */
    private int retry = 3;
}
```

## application.yml

```yaml
server:
  port: 8080
  servlet:
    context-path: /demo

zk:
  url: 127.0.0.1:2181
  timeout: 1000
  retry: 3
```

## ZkConfig.java

```java
/**
 * <p>
 * Zookeeper configuration class
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-27 14:45
 */
@Configuration
@EnableConfigurationProperties(ZkProps.class)
public class ZkConfig {
    private final ZkProps zkProps;

    @Autowired
    public ZkConfig(ZkProps zkProps) {
        this.zkProps = zkProps;
    }

    @Bean
    public CuratorFramework curatorFramework() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(zkProps.getTimeout(), zkProps.getRetry());
        CuratorFramework client = CuratorFrameworkFactory.newClient(zkProps.getUrl(), retryPolicy);
        client.start();
        return client;
    }
}
```

## ZooLock.java

> key note for distributed locks

```java
/**
 * <p>
 * Zookeeper-based distributed lock annotations
 * After you make this note on the method that needs to be locked, AOP will help you manage the lock of that method uniformly
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-27 14:11
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ZooLock {
    /**
     * Distributed lock key
     */
    String key();

    /**
     * Lock release time, default 5 seconds
     */
    long timeout() default 5 * 1000;

    /**
     * Time format, default: milliseconds
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
```

## LockKeyParam.java

> key note for the dynamic key of distributed locks

```java
/**
 * <p>
 * Distributed lock dynamic key annotation, after the configuration of the key value will dynamically get the parameter content
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-27 14:17
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface LockKeyParam {
    /**
     * If the dynamic key is in the user object, then it is necessary to set the value of fields to the user property name in the object can be more than one, and the base type does not need to set the value
     * <p>Example 1: public void count(@LockKeyParam({"id"}) User user)
     * <p>Example 2: public void count(@LockKeyParam({"id","userName"}) User user)
     * <p>Example 3: public void count (@LockKeyParam String userId)
     */
    String[] fields() default {};
}
```

## ZooLockAspect.java

> a key part of distributed locks

```java
/**
 * <p>
 * Use aop slices to record request log information
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-10-01 22:05
 */
@Aspect
@Component
@Slf4j
public class ZooLockAspect {
    private final CuratorFramework zkClient;

    private static final String KEY_PREFIX = "DISTRIBUTED_LOCK_";

    private static final String KEY_SEPARATOR = "/";

    @Autowired
    public ZooLockAspect(CuratorFramework zkClient) {
        this.zkClient = zkClient;
    }

    /**
     * Entry point
     */
    @Pointcut("@annotation(com.xkcoding.zookeeper.annotation.ZooLock)")
    public void doLock() {

    }

    /**
     * Wrap operation
     *
     * @param point pointcut
     * @return Original method return value
     * @throws Throwable exception information
     */
    @Around("doLock()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Object[] args = point.getArgs();
        ZooLock zooLock = method.getAnnotation(ZooLock.class);
        if (StrUtil.isBlank(zooLock.key())) {
            throw new RuntimeException ("distributed lock keys cannot be empty");
        }
        String lockKey = buildLockKey(zooLock, method, args);
        InterProcessMutex lock = new InterProcessMutex(zkClient, lockKey);
        try {
            Assuming that the lock is successful, everything you get later is false
            if (lock.acquire(zooLock.timeout(), zooLock.timeUnit())) {
                return point.proceed();
            } else {
                throw new RuntimeException ("Do not repeat submissions");
            }
        } finally {
            lock.release();
        }
    }

    /**
     * The key to construct a distributed lock
     *
     * @param lock annotation
     * @param method annotates the method of markup
     * @param parameters on the args method
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private String buildLockKey(ZooLock lock, Method method, Object[] args) throws NoSuchFieldException, IllegalAccessException {
        StringBuilder key = new StringBuilder(KEY_SEPARATOR + KEY_PREFIX + lock.key());

        Iteration of the annotation of all parameters, according to the subscript of the parameter of the annotation using LockKeyParam, to obtain the parameter value of the corresponding subscript in args and spliced to the first half of the key
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        for (int i = 0; i < parameterAnnotations.length; i++) {
            Loop through all annotations for this parameter
            for (Annotation annotation : parameterAnnotations[i]) {
                The annotation is not @LockKeyParam
                if (!annotation.annotationType().isInstance(LockKeyParam.class)) {
                    continue;
                }

                Get all fields
                String[] fields = ((LockKeyParam) annotation).fields();
                if (ArrayUtil.isEmpty(fields)) {
                    Normal data types are directly stitched
                    if (ObjectUtil.isNull(args[i])) {
                        throw new RuntimeException("Dynamic parameters cannot be null");
                    }
                    key.append(KEY_SEPARATOR).append(args[i]);
                } else {
                    The fields value of the @LockKeyParam is not null, so the current parameter should be the object type
                    for (String field : fields) {
                        Class<?> clazz = args[i].getClass();
                        Field declaredField = clazz.getDeclaredField(field);
                        declaredField.setAccessible(true);
                        Object value = declaredField.get(clazz);
                        key.append(KEY_SEPARATOR).append(value);
                    }
                }
            }
        }
        return key.toString();
    }

}
```

## SpringBootDemoZookeeperApplicationTests.java

> Test distributed locks

```java
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class SpringBootDemoZookeeperApplicationTests {

    public Integer getCount() {
        return count;
    }

    private Integer count = 10000;
    private ExecutorService executorService = Executors.newFixedThreadPool(1000);

    @Autowired
    private CuratorFramework zkClient;

    /**
     * Without using a distributed lock, the program ends to see if the value of count is 0
     */
    @Test
    public void test() throws InterruptedException {
        IntStream.range(0, 10000).forEach(i -> executorService.execute(this::doBuy));
        TimeUnit.MINUTES.sleep(1);
        log.error("count value is {}", count);
    }

    /**
     * Test AOP distributed locks
     */
    @Test
    public void testAopLock() throws InterruptedException {
        Using AOP in the test class requires a manual proxy
        SpringBootDemoZookeeperApplicationTests target = new SpringBootDemoZookeeperApplicationTests();
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        ZooLockAspect aspect = new ZooLockAspect(zkClient);
        factory.addAspect(aspect);
        SpringBootDemoZookeeperApplicationTests proxy = factory.getProxy();
        IntStream.range(0, 10000).forEach(i -> executorService.execute(() -> proxy.aopBuy(i)));
        TimeUnit.MINUTES.sleep(1);
        log.error("count value is {}", proxy.getCount());
    }

    /**
     * Test manual locking
     */
    @Test
    public void testManualLock() throws InterruptedException {
        IntStream.range(0, 10000).forEach(i -> executorService.execute(this::manualBuy));
        TimeUnit.MINUTES.sleep(1);
        log.error("count value is {}", count);
    }

    @ZooLock(key = "buy", timeout = 1, timeUnit = TimeUnit.MINUTES)
    public void aopBuy(int userId) {
        log.info ("{} is out of the library..." , userId);
        doBuy();
        log.info ("{} Deduction inventory successfully..." , userId);
    }

    public void manualBuy() {
        String lockPath = "/buy";
        log.info("try to buy sth.");
        try {
            InterProcessMutex lock = new InterProcessMutex(zkClient, lockPath);
            try {
                if (lock.acquire(1, TimeUnit.MINUTES)) {
                    doBuy();
                    log.info("buy successfully!");
                }
            } finally {
                lock.release();
            }
        } catch (Exception e) {
            log.error("zk error");
        }
    }

    public void doBuy() {
        count--;
        log.info ("count value is {}", count);
    }

}
```

## Reference

1. [How to use AOP in test classes] (https://stackoverflow.com/questions/11436600/unit-testing-spring-around-aop-methods)
2. zookeeper implements distributed locks: The essence of Spring Boot 2 From building small systems to architecting distributed large systems, Li Jiazhi - Chapter 16 - Spring Boot and Zoo Keeper - 16.3 Implementing distributed locks
