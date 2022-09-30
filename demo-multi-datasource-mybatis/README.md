# spring-boot-demo-multi-datasource-mybatis

> This demo demonstrates how Spring Boott integrates with Mybati's multiple data sources. You can implement multiple data sources yourself based on AOP, which is based on an elegant open source solution provided by Mybatis-Plus.

## Preparation

Prepare two data sources and execute the following table creation statement

```mysql
DROP TABLE IF EXISTS `multi_user`;
CREATE TABLE `multi_user`(
  `id` bigint(64) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  `age` int(30) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci;
```

## Import dependencies

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>spring-boot-demo-multi-datasource-mybatis</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>spring-boot-demo-multi-datasource-mybatis</name>
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
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>

        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>dynamic-datasource-spring-boot-starter</artifactId>
            <version>2.5.0</version>
        </dependency>

        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.0.7.1</version>
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
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
        </dependency>
    </dependencies>

    <build>
        <finalName>spring-boot-demo-multi-datasource-mybatis</finalName>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

## Prepare the entity class

`User.java`

> 1. @Data / @NoArgsConstructor / @AllArgsConstructor / @Builder are lombok annotations
> 2. @TableName ("multi_user") is a Mybatis-Plus annotation that represents database table names primarily when entity class names and table names do not meet the format of hump and underscore
> 3. @TableId (type = IdType.ID_WORKER) is a Mybatis-Plus annotation that specifies the primary key type, and here I'm using Mybatis-Plus based on the snowflake algorithm provided by twitter

```java
/**
 * <p>
 * User entity class
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-01-21 14:19
 */
@Data
@TableName("multi_user")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements Serializable {
    private static final long serialVersionUID = -1923859222295750467L;

    /**
     * Primary key
     */
    @TableId(type = IdType.ID_WORKER)
    private Long id;

    /**
     * Name
     */
    private String name;

    /**
     * Age
     */
    private Integer age;
}
```

## Data access layer

`UserMapper.java`

> does not need to build the corresponding xml, only needs to inherit BaseMapper to have most of the single-table operation methods.

```java
/**
 * <p>
 * Data access layer
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-01-21 14:28
 */
public interface UserMapper extends BaseMapper<User> {
}
```

## Data Services Layer

### Interface

`UserService.java`

```java
/**
 * <p>
 * Data service layer
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-01-21 14:31
 */
public interface UserService extends IService<User> {

    /**
     * Add User
     *
     * @param user user
     */
    void addUser(User user);
}
```

### Implementation

`UserServiceImpl.java`

> 1. @DS: Annotations switch data sources on a class or method with a @DS precedence over @DS on the class
> 2. baseMapper: The mapper object, known as 'UserMapper', gets CRUD functionality
> 3. Default slave library: '@DS(value = 'slave')' on the class, default go slave library, unless in the method to add '@DS (value = "master")' to the main library

```java
/**
 * <p>
 * Data Services Layer implementation
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-01-21 14:37
 */
@Service
@DS("slave")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * On the class {@code @DS("slave")} represents the default slave library, and on the method, write {@code @DS("master")} represents the default master library
     *
     * @param user user
     */
    @DS("master")
    @Override
    public void addUser(User user) {
        baseMapper.insert(user);
    }
}
```

## Start the class

`SpringBootDemoMultiDatasourceMybatisApplication.java`

> above the startup class needs to use the @MapperScan to scan the package where the mapper class is located

```java
/**
 * <p>
 * Launcher
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-01-21 14:19
 */
@SpringBootApplication
@MapperScan(basePackages = "com.xkcoding.multi.datasource.mybatis.mapper")
public class SpringBootDemoMultiDatasourceMybatisApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootDemoMultiDatasourceMybatisApplication.class, args);
    }

}
```

## Configuration file

`application.yml`

```yaml
spring:
  datasource:
    dynamic:
      datasource:
        master:
          username: root
          password: root
          url: jdbc:mysql://127.0.0.1:3306/spring-boot-demo?useUnicode=true&characterEncoding=UTF-8&useSSL=false&autoReconnect=true&failOverReadOnly=false&serverTimezone=GMT%2B8
          driver-class-name: com.mysql.cj.jdbc.Driver
        slave:
          username: root
          password: root
          url: jdbc:mysql://127.0.0.1:3306/spring-boot-demo-2?useUnicode=true&characterEncoding=UTF-8&useSSL=false&autoReconnect=true&failOverReadOnly=false&serverTimezone=GMT% 2B8
          driver-class-name: com.mysql.cj.jdbc.Driver
      mp-enabled: true
logging:
  level:
    com.xkcoding.multi.datasource.mybatis: debug
```

## Test class

```java
/**
 * <p>
 * Test the master-slave data source
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-01-21 14:45
 */
@Slf4j
public class UserServiceImplTest extends SpringBootDemoMultiDatasourceMybatisApplicationTests {
    @Autowired
    private UserService userService;

    /**
     * Add from master and slave libraries
     */
    @Test
    public void addUser() {
        User userMaster = User.builder().name.age(20).build();
        userService.addUser(userMaster);

        User userSlave = User.builder().name.age(20).build();
        userService.save(userSlave);
    }

    /**
     * Query from the library
     */
    @Test
    public void testListUser() {
        List<User> list = userService.list(new QueryWrapper<>());
        log.info("【list】= {}", JSONUtil.toJsonStr(list));
    }
}
```

### Test results

The master and slave data source loads successfully

```java
2019-01-21 14:55:41.096  INFO 7239 --- [           main] com.zaxxer.hikari.HikariDataSource       : master - Starting...
2019-01-21 14:55:41.307  INFO 7239 --- [           main] com.zaxxer.hikari.HikariDataSource       : master - Start completed.
2019-01-21 14:55:41.308  INFO 7239 --- [           main] com.zaxxer.hikari.HikariDataSource       : slave - Starting...
2019-01-21 14:55:41.312  INFO 7239 --- [           main] com.zaxxer.hikari.HikariDataSource       : slave - Start completed.
2019-01-21 14:55:41.312 INFO 7239 --- [ main] c.b.d.d.DynamicRoutingDataSource : Initially loaded a total of 2 data sources
2019-01-21 14:55:41.313 INFO 7239 --- [ main] c.b.d.d.DynamicRoutingDataSource : Dynamic Data Source - Load slave successful
2019-01-21 14:55:41.313 INFO 7239 --- [ main] c.b.d.d.DynamicRoutingDataSource : Dynamic Data Source - Load master successful
2019-01-21 14:55:41.313 INFO 7239 --- [ main] c.b.d.d.DynamicRoutingDataSource : The current default data source is a single data source with the data source name master
 _ _   |_  _ _|_. ___ _ |    _
| | |\/|_)(_| | |_\  |_)|| _|_\
     /               |
                        3.0.7.1
```

**Main **Library**Recommended** Only perform the **INSERT****UPDATE****DELETE** operation

! [image-20190121153211509] (http://static.xkcoding.com/spring-boot-demo/multi-datasource/mybatis/063506.jpg)

**From **Library**Recommended** Only perform the **SELECT** operation

! [image-20190121152825859] (http://static.xkcoding.com/spring-boot-demo/multi-datasource/mybatis/063505.jpg)

> The production environment needs to be set up **master-slave replication**

## Reference

1. Mybatis-Plus Multiple Data Source Document: https://mybatis.plus/guide/dynamic-datasource.html
2. Mybatis-Plus Multi-Data Source Integration Official demo:https://gitee.com/baomidou/dynamic-datasource-spring-boot-starter/tree/master/samples
