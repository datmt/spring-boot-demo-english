# spring-boot-demo-dynamic-datasource

> This demo mainly demonstrates how a Spring Boot project can 'dynamically add/remove' data sources through the interface, how to 'dynamically switch' data sources after adding data sources, and then use mybatis to query the data of the switched data sources.

## 1. Environment preparation

1. Execute the SQL script in the db directory
2. Execute 'init.sql' under the default data source
3. Execute 'user.sql' separately across all data sources

## 2. Primary code

### 2.1.pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <artifactId>spring-boot-demo-dynamic-datasource</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <packaging>jar</packaging>

  <name>spring-boot-demo-dynamic-datasource</name>
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
      <groupId>tk.mybatis</groupId>
      <artifactId>mapper-spring-boot-starter</artifactId>
      <version>2.1.5</version>
    </dependency>

    <dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
      <scope>runtime</scope>
    </dependency>

    <dependency>
      <groupId>org.projectlombok</groupId>
      <artifactId>lombok</artifactId>
      <optional>true</optional>
    </dependency>

    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-test</artifactId>
      <scope>test</scope>
    </dependency>
  </dependencies>

  <build>
    <finalName>spring-boot-demo-dynamic-datasource</finalName>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
      </plugin>
    </plugins>
  </build>

</project>
```

### 2.2. The underlying configuration class

- DatasourceConfiguration.java

> This class is mainly used by 'DataSourceBuilder' to build a data source that we customize and put into a Spring container

```java
/**
 * <p>
 * Data source configuration
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-09-04 10:27
 */
@Configuration
public class DatasourceConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.type(DynamicDataSource.class);
        return dataSourceBuilder.build();
    }
}
```

- MybatisConfiguration.java

> This class is mainly to configure the data source we built in the previous step into Mybatis's 'SqlSessionFactory'

```java
/**
 * <p>
 * Mybatis configuration
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-09-04 16:20
 */
@Configuration
@MapperScan(basePackages = "com.xkcoding.dynamicdatasource.mapper", sqlSessionFactoryRef = "sqlSessionFactory")
public class MybatisConfiguration {
    /**
     * Create a session factory.
     *
     * @param dataSource data source
     * @return Session factory
     */
    @Bean(name = "sqlSessionFactory")
    @SneakyThrows
    public SqlSessionFactory getSqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        return bean.getObject();
    }
}
```

### 2.3. Dynamic data source main logic

- DatasourceConfigContextHolder.java

> This class is mainly used to bind the data source id used by the current thread, and ensures that it cannot be modified within the same thread through ThreadLocal

```java
/**
 * <p>
 * Data source identity management
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-09-04 14:16
 */
public class DatasourceConfigContextHolder {
    private static final ThreadLocal<Long> DATASOURCE_HOLDER = ThreadLocal.withInitial(() -> DatasourceHolder.DEFAULT_ID);

    /**
     * Set the default data source
     */
    public static void setDefaultDatasource() {
        DATASOURCE_HOLDER.remove();
        setCurrentDatasourceConfig(DatasourceHolder.DEFAULT_ID);
    }

    /**
     * Get the current data source configuration ID
     *
     * @return Data source configuration ID
     */
    public static Long getCurrentDatasourceConfig() {
        return DATASOURCE_HOLDER.get();
    }

    /**
     * Set the current data source configuration ID
     *
     * @param id data source configuration ID
     */
    public static void setCurrentDatasourceConfig(Long id) {
        DATASOURCE_HOLDER.set(id);
    }

}
```

- DynamicDataSource.java

> This class inherits 'com.zaxxer.hikari.HikariDataSource' and is primarily used to dynamically switch data source connections.

```java
/**
 * <p>
 * Dynamic data sources
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-09-04 10:41
 */
@Slf4j
public class DynamicDataSource extends HikariDataSource {
    @Override
    public Connection getConnection() throws SQLException {
        Gets the current data source ID
        Long id = DatasourceConfigContextHolder.getCurrentDatasourceConfig();
        Get the data source based on the current id
        HikariDataSource datasource = DatasourceHolder.INSTANCE.getDatasource(id);

        if (null == datasource) {
            datasource = initDatasource(id);
        }

        return datasource.getConnection();
    }

    /**
     * Initialize the data source
     * @param id data source id
     * @return Data source
     */
    private HikariDataSource initDatasource(Long id) {
        HikariDataSource dataSource = new HikariDataSource();

        Determine whether it is the default data source
        if (DatasourceHolder.DEFAULT_ID.equals(id)) {
            The default data source is generated according to the application.yml configuration
            DataSourceProperties properties = SpringUtil.getBean(DataSourceProperties.class);
            dataSource.setJdbcUrl(properties.getUrl());
            dataSource.setUsername(properties.getUsername());
            dataSource.setPassword(properties.getPassword());
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        } else {
            If it is not the default data source, the configuration of the data source corresponding to the id is obtained through the cache
            DatasourceConfig datasourceConfig = DatasourceConfigCache.INSTANCE.getConfig(id);

            if (datasourceConfig == null) {
                throw new RuntimeException("No such data source");
            }

            dataSource.setJdbcUrl(datasourceConfig.buildJdbcUrl());
            dataSource.setUsername(datasourceConfig.getUsername());
            dataSource.setPassword(datasourceConfig.getPassword());
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        }
        Adds the created data source to the Data Source Manager and binds the current thread
        DatasourceHolder.INSTANCE.addDatasource(id, dataSource);
        return dataSource;
    }
}
```

- DatasourceScheduler.java

> This class is primarily used to schedule tasks

```java
/**
 * <p>
 * Data source cache release scheduler
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-09-04 14:42
 */
public enum DatasourceScheduler {
    /**
     * Current instance
     */
    INSTANCE;

    private AtomicInteger cacheTaskNumber = new AtomicInteger(1);
    private ScheduledExecutorService scheduler;

    DatasourceScheduler() {
        create();
    }

    private void create() {
        this.shutdown();
        this.scheduler = new ScheduledThreadPoolExecutor(10, r -> new Thread(r, String.format("Datasource-Release-Task-%s", cacheTaskNumber.getAndIncrement())));
    }

    private void shutdown() {
        if (null != this.scheduler) {
            this.scheduler.shutdown();
        }
    }

    public void schedule(Runnable task,long delay){
        this.scheduler.scheduleAtFixedRate(task, delay, delay, TimeUnit.MILLISECONDS);
    }

}
```

- DatasourceManager.java

> This class is mainly used to manage data sources, record the last use time of data sources, and determine whether they have not been used for a long time, and if they have not been used for more than a certain period of time, the connection will be released

```java
/**
 * <p>
 * Data source management class
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-09-04 14:27
 */
public class DatasourceManager {
    /**
     * Default release time
     */
    private static final Long DEFAULT_RELEASE = 10L;

    /**
     * Data source
     */
    @Getter
    private HikariDataSource dataSource;

    /**
     * Last use time
     */
    private LocalDateTime lastUseTime;

    public DatasourceManager(HikariDataSource dataSource) {
        this.dataSource = dataSource;
        this.lastUseTime = LocalDateTime.now();
    }

    /**
     * Whether it has expired, and if so, close the data source
     *
     * @return whether expired, {@code true} expired, {@code false} did not expire
     */
    public boolean isExpired() {
        if (LocalDateTime.now().isBefore(this.lastUseTime.plusMinutes(DEFAULT_RELEASE))) {
            return false;
        }
        this.dataSource.close();
        return true;
    }

    /**
     * Refresh last use time
     */
    public void refreshTime() {
        this.lastUseTime = LocalDateTime.now();
    }
}
```

- DatasourceHolder.java

> This class is mainly used to manage data sources, and at the same time, the data source is checked by 'DatasourceScheduler' to check whether the data source has not been used for a long time, and the connection is released when it times out

```java
/**
 * <p>
 * Data source management
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-09-04 14:23
 */
public enum DatasourceHolder {
    /**
     * Current instance
     */
    INSTANCE;

    /**
     * Start execution, timed 5 minutes to clean up
     */
    DatasourceHolder() {
        DatasourceScheduler.INSTANCE.schedule(this::clearExpiredDatasource, 5 * 60 * 1000);
    }

    /**
     * The id of the default data source
     */
    public static final Long DEFAULT_ID = -1L;

    /**
     * Manage dynamic data source lists.
     */
    private static final Map<Long, DatasourceManager> DATASOURCE_CACHE = new ConcurrentHashMap<>();

    /**
     * Add dynamic data source
     *
     * @param id data source id
     * @param dataSource data source
     */
    public synchronized void addDatasource(Long id, HikariDataSource dataSource) {
        DatasourceManager datasourceManager = new DatasourceManager(dataSource);
        DATASOURCE_CACHE.put(id, datasourceManager);
    }

    /**
     * Query dynamic data sources
     *
     * @param id data source id
     * @return Data source
     */
    public synchronized HikariDataSource getDatasource(Long id) {
        if (DATASOURCE_CACHE.containsKey(id)) {
            DatasourceManager datasourceManager = DATASOURCE_CACHE.get(id);
            datasourceManager.refreshTime();
            return datasourceManager.getDataSource();
        }
        return null;
    }

    /**
     * Clear the timed out data source
     */
    public synchronized void clearExpiredDatasource() {
        DATASOURCE_CACHE.forEach((k, v) -> {
            Excludes the default data source
            if (! DEFAULT_ID.equals(k)) {
                if (v.isExpired()) {
                    DATASOURCE_CACHE.remove(k);
                }
            }
        });
    }

    /**
     * Clear dynamic data sources
     * @param id data source id
     */
    public synchronized void removeDatasource(Long id) {
        if (DATASOURCE_CACHE.containsKey(id)) {
            Close the data source
            DATASOURCE_CACHE.get(id).getDataSource().close();
            Remove the cache
            DATASOURCE_CACHE.remove(id);
        }
    }
}
```

- DatasourceConfigCache.java

> This class is mainly used to cache the configuration of a data source, and when a user generates a data source, gets the data source connection parameters

```java
/**
 * <p>
 * Data source configuration cache
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-09-04 17:13
 */
public enum DatasourceConfigCache {
    /**
     * Current instance
     */
    INSTANCE;

    /**
     * Manage dynamic data source lists.
     */
    private static final Map<Long, DatasourceConfig> CONFIG_CACHE = new ConcurrentHashMap<>();

    /**
     * Add data source configuration
     *
     * @param id data source configuration ID
     * @param config data source configuration
     */
    public synchronized void addConfig(Long id, DatasourceConfig config) {
        CONFIG_CACHE.put(id, config);
    }

    /**
     * Query data source configuration
     *
     * @param id data source configuration ID
     * @return data source configuration
     */
    public synchronized DatasourceConfig getConfig(Long id) {
        if (CONFIG_CACHE.containsKey(id)) {
            return CONFIG_CACHE.get(id);
        }
        return null;
    }

    /**
     * Clear the data source configuration
     */
    public synchronized void removeConfig(Long id) {
        CONFIG_CACHE.remove(id);
        Synchronously clears the data source corresponding to the DatasourceHolder
        DatasourceHolder.INSTANCE.removeDatasource(id);
    }
}
```

### 2.4. Start the class

After > starts, query the list of data source configurations using the default data source and cache it into the 'DatasourceConfigCache' for subsequent use

```java
/**
 * <p>
 * Launcher
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-09-04 17:57
 */
@SpringBootApplication
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SpringBootDemoDynamicDatasourceApplication implements CommandLineRunner {
    private final DatasourceConfigMapper configMapper;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootDemoDynamicDatasourceApplication.class, args);
    }

    @Override
    public void run(String... args) {
        Sets the default data source
        DatasourceConfigContextHolder.setDefaultDatasource();
        Query the list of all database configurations
        List<DatasourceConfig> datasourceConfigs = configMapper.selectAll();
        System.out.println("Load the rest of the data source configuration list: " + datasourceConfigs);
        Join the database configuration to the cache
        datasourceConfigs.forEach(config -> DatasourceConfigCache.INSTANCE.addConfig(config.getId(), config));
    }
}
```

### 2.5. For the rest of the code, refer to demo

## 3. Test

Start the project and you can see that the console reads the data source information that has been configured in the database

! [image-20190905164824155] (http://static.xkcoding.com/spring-boot-demo/dynamic-datasource/062351.png)

Test with tools like PostMan

- Default data source query

! [image-20190905165240373] (http://static.xkcoding.com/spring-boot-demo/dynamic-datasource/062353.png)

- Query according to the data source with data source id 1

! [image-20190905165323097] (http://static.xkcoding.com/spring-boot-demo/dynamic-datasource/062354.png)

- Query according to the data source with data source id 2

! [image-20190905165350355] (http://static.xkcoding.com/spring-boot-demo/dynamic-datasource/062355.png)

- You can test the 'add/delete' of the data source and then query the data of the corresponding data source

> Delete a data source:
>
> - DELETE http://localhost:8080/config/{id}
>
> Add a data source:
>
> - POST http://localhost:8080/config
>
> - Parameters:
>
> ```json
> {
> "host": "Database IP",
>     "port": 3306,
> "username": "username",
> "password": "password",
> "database": "Database"
> }
> ```

## 4. optimize

As in the above test, we only need to pass the parameters of the data source in the header to dynamically switch the data source, how to do it?

The answer is 'AOP'.

```java
/**
 * <p>
 * Data source selector slice
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-09-04 16:52
 */
@Aspect
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DatasourceSelectorAspect {
    @Pointcut("execution(public * com.xkcoding.dynamic.datasource.controller.*.*(..))")
    public void datasourcePointcut() {
    }

    /**
     * Front-end operation, intercept specific requests, get the data source id in the header, set the thread variable, and use it for subsequent switching data sources
     */
    @Before("datasourcePointcut()")
    public void doBefore(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();

        Excludes methods that do not switch data sources
        DefaultDatasource annotation = method.getAnnotation(DefaultDatasource.class);
        if (null != annotation) {
            DatasourceConfigContextHolder.setDefaultDatasource();
        } else {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
            HttpServletRequest request = attributes.getRequest();
            String configIdInHeader = request.getHeader("Datasource-Config-Id");
            if (StringUtils.hasText(configIdInHeader)) {
                long configId = Long.parseLong(configIdInHeader);
                DatasourceConfigContextHolder.setCurrentDatasourceConfig(configId);
            } else {
                DatasourceConfigContextHolder.setDefaultDatasource();
            }
        }
    }

    /**
     * Post-operation, set back to the default data source id
     */
    @AfterReturning("datasourcePointcut()")
    public void doAfter() {
        DatasourceConfigContextHolder.setDefaultDatasource();
    }

}
```

At this point, we need to consider, do we allow users to switch data sources in each method? The answer is definitely no, so we have defined a note to identify that the current method can only use the default data source.

```java
/**
 * <p>
 * The user ID can only use the default data source
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-09-04 17:37
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DefaultDatasource {
}
```

When finished, sprinkle flowers ✿✿ヽ(°▽°)ノ✿
