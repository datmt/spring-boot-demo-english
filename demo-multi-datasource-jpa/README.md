# spring-boot-demo-multi-datasource-jpa

> This demo mainly demonstrates how Spring Boott integrates with JPA's multiple data sources.

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>spring-boot-demo-multi-datasource-jpa</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>spring-boot-demo-multi-datasource-jpa</name>
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
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
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
        <finalName>spring-boot-demo-multi-datasource-jpa</finalName>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

## PrimaryDataSourceConfig.java

> Primary data source configuration

```java
/**
 * <p>
 * JPA Multiple Data Source Configuration - Primary Data Source
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-01-17 15:58
 */
@Configuration
public class PrimaryDataSourceConfig {

    /**
     * Scan configuration information starting with spring.datasource.primary
     *
     * @return data source configuration information
     */
    @Primary
    @Bean(name = "primaryDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * Get the main library data source object
     *
     * @param dataSourceProperties injects a bean called primaryDataSourceProperties
     * @return data source object
     */
    @Primary
    @Bean(name = "primaryDataSource")
    public DataSource dataSource(@Qualifier("primaryDataSourceProperties") DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    /**
     * This method is only used when a JdbcTemplate object is required
     *
     * @param dataSource injects a bean called primaryDataSource
     * @return data source JdbcTemplate object
     */
    @Primary
    @Bean(name = "primaryJdbcTemplate")
    public JdbcTemplate jdbcTemplate(@Qualifier("primaryDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
```

## SecondDataSourceConfig.java

> Configure from a data source

```java
/**
 * <p>
 * JPA Multiple Data Source Configuration - Secondary Data Source
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-01-17 15:58
 */
@Configuration
public class SecondDataSourceConfig {

    /**
     * Scan the configuration information at the beginning of spring.datasource.second
     *
     * @return data source configuration information
     */
    @Bean(name = "secondDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.second")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * Get the main library data source object
     *
     * @param dataSourceProperties injects a bean called secondDataSourceProperties
     * @return data source object
     */
    @Bean(name = "secondDataSource")
    public DataSource dataSource(@Qualifier("secondDataSourceProperties") DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    /**
     * This method is only used when a JdbcTemplate object is required
     *
     * @param dataSource injects a bean named secondDataSource
     * @return data source JdbcTemplate object
     */
    @Bean(name = "secondJdbcTemplate")
    public JdbcTemplate jdbcTemplate(@Qualifier("secondDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
```

## PrimaryJpaConfig.java

> the primary JPA configuration

```java
/**
 * <p>
 * JPA Multi-Data Source Configuration - Primary JPA configuration
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-01-17 16:54
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        repository package name
        basePackages = PrimaryJpaConfig.REPOSITORY_PACKAGE,
        The entity manages the bean name
        entityManagerFactoryRef = "primaryEntityManagerFactory",
        Transaction management bean name
        transactionManagerRef = "primaryTransactionManager")
public class PrimaryJpaConfig {
    static final String REPOSITORY_PACKAGE = "com.xkcoding.multi.datasource.jpa.repository.primary";
    private static final String ENTITY_PACKAGE = "com.xkcoding.multi.datasource.jpa.entity.primary";


    /**
     * Scan configuration information starting with spring.jpa.primary
     *
     * @return jpa configuration information
     */
    @Primary
    @Bean(name = "primaryJpaProperties")
    @ConfigurationProperties(prefix = "spring.jpa.primary")
    public JpaProperties jpaProperties() {
        return new JpaProperties();
    }

    /**
     * Get the master library entity management factory object
     *
     * @param primaryDataSource injects a data source called primaryDataSource
     * @param jpaProperties injects jpa configuration information called primaryJpaProperties
     * @param builder injected into EntityManagerFactoryBuilder
     * @return Entity management factory objects
     */
    @Primary
    @Bean(name = "primaryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier("primaryDataSource") DataSource primaryDataSource, @Qualifier("primaryJpaProperties") JpaProperties jpaProperties, EntityManagerFactoryBuilder builder) {
        return builder
                Set up the data source
                .dataSource(primaryDataSource)
                Set the jpa configuration
                .properties(jpaProperties.getProperties())
                Set the entity package name
                .packages(ENTITY_PACKAGE)
                Sets the persistence unit name that specifies the data source when @PersistenceContext annotation gets EntityManager
                .persistenceUnit("primaryPersistenceUnit").build();
    }

    /**
     * Get the entity management object
     *
     * @param factory injects a bean called primaryEntityManagerFactory
     * @return Entity management objects
     */
    @Primary
    @Bean(name = "primaryEntityManager")
    public EntityManager entityManager(@Qualifier("primaryEntityManagerFactory") EntityManagerFactory factory) {
        return factory.createEntityManager();
    }

    /**
     * Get the main library transaction management object
     *
     * @param factory injects a bean called primaryEntityManagerFactory
     * @return Transaction management objects
     */
    @Primary
    @Bean(name = "primaryTransactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("primaryEntityManagerFactory") EntityManagerFactory factory) {
        return new JpaTransactionManager(factory);
    }

}
```

## SecondJpaConfig.java

> Configuration from JPA

```java
/**
 * <p>
 * JPA Multiple Data Source Configuration - Sub-JPA configuration
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-01-17 16:54
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        repository package name
        basePackages = SecondJpaConfig.REPOSITORY_PACKAGE,
        The entity manages the bean name
        entityManagerFactoryRef = "secondEntityManagerFactory",
        Transaction management bean name
        transactionManagerRef = "secondTransactionManager")
public class SecondJpaConfig {
    static final String REPOSITORY_PACKAGE = "com.xkcoding.multi.datasource.jpa.repository.second";
    private static final String ENTITY_PACKAGE = "com.xkcoding.multi.datasource.jpa.entity.second";


    /**
     * Scan configuration information beginning with spring.jpa.second
     *
     * @return jpa configuration information
     */
    @Bean(name = "secondJpaProperties")
    @ConfigurationProperties(prefix = "spring.jpa.second")
    public JpaProperties jpaProperties() {
        return new JpaProperties();
    }

    /**
     * Get the master library entity management factory object
     *
     * @param secondDataSource injects a data source named secondDataSource
     * @param jpaProperties injects jpa configuration information called secondJpaProperties
     * @param builder injected into EntityManagerFactoryBuilder
     * @return Entity management factory objects
     */
    @Bean(name = "secondEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier("secondDataSource") DataSource secondDataSource, @Qualifier("secondJpaProperties") JpaProperties jpaProperties, EntityManagerFactoryBuilder builder) {
        return builder
                Set up the data source
                .dataSource(secondDataSource)
                Set the jpa configuration
                .properties(jpaProperties.getProperties())
                Set the entity package name
                .packages(ENTITY_PACKAGE)
                Sets the persistence unit name that specifies the data source when @PersistenceContext annotation gets EntityManager
                .persistenceUnit("secondPersistenceUnit").build();
    }

    /**
     * Get the entity management object
     *
     * @param factory injects a bean called secondEntityManagerFactory
     * @return Entity management objects
     */
    @Bean(name = "secondEntityManager")
    public EntityManager entityManager(@Qualifier("secondEntityManagerFactory") EntityManagerFactory factory) {
        return factory.createEntityManager();
    }

    /**
     * Get the main library transaction management object
     *
     * @param factory injects a bean called secondEntityManagerFactory
     * @return Transaction management objects
     */
    @Bean(name = "secondTransactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("secondEntityManagerFactory") EntityManagerFactory factory) {
        return new JpaTransactionManager(factory);
    }

}
```

## application.yml

```yaml
spring:
  datasource:
    primary:
      url: jdbc:mysql://127.0.0.1:3306/spring-boot-demo?useUnicode=true&characterEncoding=UTF-8&useSSL=false&autoReconnect=true&failOverReadOnly=false&serverTimezone=GMT%2B8
      username: root
      password: root
      driver-class-name: com.mysql.cj.jdbc.Driver
      type: com.zaxxer.hikari.HikariDataSource
      hikari:
        minimum-idle: 5
        connection-test-query: SELECT 1 FROM DUAL
        maximum-pool-size: 20
        auto-commit: true
        idle-timeout: 30000
        pool-name: PrimaryHikariCP
        max-lifetime: 60000
        connection-timeout: 30000
    second:
      url: jdbc:mysql://127.0.0.1:3306/spring-boot-demo-2?useUnicode=true&characterEncoding=UTF-8&useSSL=false&autoReconnect=true&failOverReadOnly=false&serverTimezone=GMT% 2B8
      username: root
      password: root
      driver-class-name: com.mysql.cj.jdbc.Driver
      type: com.zaxxer.hikari.HikariDataSource
      hikari:
        minimum-idle: 5
        connection-test-query: SELECT 1 FROM DUAL
        maximum-pool-size: 20
        auto-commit: true
        idle-timeout: 30000
        pool-name: SecondHikariCP
        max-lifetime: 60000
        connection-timeout: 30000
  jpa:
    primary:
      show-sql: true
      generate-ddl: true
      hibernate:
        ddl-auto: update
      properties:
        hibernate:
          dialect: org.hibernate.dialect.MySQL57InnoDBDialect
      open-in-view: true
    second:
      show-sql: true
      generate-ddl: true
      hibernate:
        ddl-auto: update
      properties:
        hibernate:
          dialect: org.hibernate.dialect.MySQL57InnoDBDialect
      open-in-view: true
logging:
  level:
    com.xkcoding: debug
    org.hibernate.SQL: debug
    org.hibernate.type: trace
```

## SpringBootDemoMultiDatasourceJpaApplicationTests.java

```java
package com.xkcoding.multi.datasource.jpa;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Snowflake;
import com.xkcoding.multi.datasource.jpa.entity.primary.PrimaryMultiTable;
import com.xkcoding.multi.datasource.jpa.entity.second.SecondMultiTable;
import com.xkcoding.multi.datasource.jpa.repository.primary.PrimaryMultiTableRepository;
import com.xkcoding.multi.datasource.jpa.repository.second.SecondMultiTableRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class SpringBootDemoMultiDatasourceJpaApplicationTests {
    @Autowired
    private PrimaryMultiTableRepository primaryRepo;
    @Autowired
    private SecondMultiTableRepository secondRepo;
    @Autowired
    private Snowflake snowflake;

    @Test
    public void testInsert() {
        PrimaryMultiTable primary = new PrimaryMultiTable(snowflake.nextId(), "test name-1");
        primaryRepo.save(primary);

        SecondMultiTable second = new SecondMultiTable();
        BeanUtil.copyProperties(primary, second);
        secondRepo.save(second);
    }

    @Test
    public void testUpdate() {
        primaryRepo.findAll().forEach(primary -> {
            primary.setName ("modified"+primary.getName());
            primaryRepo.save(primary);

            SecondMultiTable second = new SecondMultiTable();
            BeanUtil.copyProperties(primary, second);
            secondRepo.save(second);
        });
    }

    @Test
    public void testDelete() {
        primaryRepo.deleteAll();

        secondRepo.deleteAll();
    }

    @Test
    public void testSelect() {
        List<PrimaryMultiTable> primary = primaryRepo.findAll();
        log.info("【primary】= {}", primary);

        List<SecondMultiTable> second = secondRepo.findAll();
        log.info("【second】= {}", second);
    }

}
```

## Directory structure

```
.
├── README.md
├── pom.xml
├── spring-boot-demo-multi-datasource-jpa.iml
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.xkcoding.multi.datasource.jpa
│   │   │       ├── SpringBootDemoMultiDatasourceJpaApplication.java
│   │   │       ├── config
│   │   │       │   ├── PrimaryDataSourceConfig.java
│   │   │       │   ├── PrimaryJpaConfig.java
│   │   │       │   ├── SecondDataSourceConfig.java
│   │   │       │   ├── SecondJpaConfig.java
│   │   │       │   └── SnowflakeConfig.java
│   │   │       ├── entity
│   │   │       │   ├── primary
│   │   │       │   │   └── PrimaryMultiTable.java
│   │   │       │   └── second
│   │   │       │       └── SecondMultiTable.java
│   │   │       └── repository
│   │   │               ├── primary
│   │   │               │   └── PrimaryMultiTableRepository.java
│   │   │               └── second
│   │   │                   └── SecondMultiTableRepository.java
│   │   └── resources
│   │       └── application.yml
│   └── test
│       └── java
│           └── com.xkcoding.multi.datasource.jpa
│               └── SpringBootDemoMultiDatasourceJpaApplicationTests.java
└── target
```

## Reference

1. https://www.jianshu.com/p/34730e595a8c
2. https://blog.csdn.net/anxpp/article/details/52274120
