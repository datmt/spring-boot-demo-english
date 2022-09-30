# spring-boot-demo-orm-mybatis-plus

> This demo demonstrates how Spring Boot integrates with mybatis-plus, simplifying Mybatis development and giving you an incredible development experience.
>
> - 2019-09-14 New: ActiveRecord mode operation

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>spring-boot-demo-orm-mybatis-plus</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>spring-boot-demo-orm-mybatis-plus</name>
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
        <mybatis.plus.version>3.0.5</mybatis.plus.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>

        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>${mybatis.plus.version}</version>
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
        <finalName>spring-boot-demo-orm-mybatis-plus</finalName>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

## MybatisPlusConfig.java

```java
/**
 * <p>
 * mybatis-plus configuration
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-08 17:29
 */
@Configuration
@MapperScan(basePackages = {"com.xkcoding.orm.mybatis.plus.mapper"})
@EnableTransactionManagement
public class MybatisPlusConfig {
    /**
     * Performance analysis interceptor, not recommended for production
     */
    @Bean
    public PerformanceInterceptor performanceInterceptor(){
        return new PerformanceInterceptor();
    }

    /**
     * Pagination plugin
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}
```

## CommonFieldHandler.java

```java
package com.xkcoding.orm.mybatis.plus.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * <p>
 * Common field population
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-08 17:40
 */
@Slf4j
@Component
public class CommonFieldHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start insert fill ....");
        this.setFieldValByName("createTime", new Date(), metaObject);
        this.setFieldValByName("lastUpdateTime", new Date(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("start update fill ....");
        this.setFieldValByName("lastUpdateTime", new Date(), metaObject);
    }
}
```

## application.yml

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/spring-boot-demo?useUnicode=true&characterEncoding=UTF-8&useSSL=false&autoReconnect=true&failOverReadOnly=false&serverTimezone=GMT%2B8
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
    type: com.zaxxer.hikari.HikariDataSource
    initialization-mode: always
    continue-on-error: true
    schema:
    - "classpath:db/schema.sql"
    data:
    - "classpath:db/data.sql"
    hikari:
      minimum-idle: 5
      connection-test-query: SELECT 1 FROM DUAL
      maximum-pool-size: 20
      auto-commit: true
      idle-timeout: 30000
      pool-name: SpringBootDemoHikariCP
      max-lifetime: 60000
      connection-timeout: 30000
logging:
  level:
    com.xkcoding: debug
    com.xkcoding.orm.mybatis.plus.mapper: trace
mybatis-plus:
  mapper-locations: classpath:mappers/*.xml
  #实体扫描, multiple packages are separated by commas or semicolons
  typeAliasesPackage: com.xkcoding.orm.mybatis.plus.entity
  global-config:
    # Database-related configuration
    db-config:
      #主键类型 AUTO: "Database ID auto-increment", INPUT: "User input ID", ID_WORKER: "Globally unique ID (unique ID of numeric type)", UUID: "Globally unique ID UUID";
      id-type: auto
      #字段策略 IGNORED: "Ignore Judgment", NOT_NULL: "Non-NULL Judgment"), NOT_EMPTY: "Non-Null Judgment"
      field-strategy: not_empty
      #驼峰下划线转换
      table-underline: true
      #是否开启大写命名, it is not turned on by default
      #capital-mode: true
      #逻辑删除配置
      #logic-delete-value: 1
      #logic-not-delete-value: 0
      db-type: mysql
    #刷新mapper Debugging artifacts
    refresh: true
  # Native configuration
  configuration:
    map-underscore-to-camel-case: true
    cache-enabled: true
```

## UserMapper.java

```java
/**
 * <p>
 * UserMapper
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-08 16:57
 */
@Component
public interface UserMapper extends BaseMapper<User> {
}
```

## UserService.java

```java
/**
 * <p>
 * User Service
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-08 18:10
 */
public interface UserService extends IService<User> {
}
```

## UserServiceImpl.java

```java
/**
 * <p>
 * User Service
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-08 18:10
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
```

## UserServiceTest.java

```java
/**
 * <p>
 * User Service test
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-08 18:13
 */
@Slf4j
public class UserServiceTest extends SpringBootDemoOrmMybatisPlusApplicationTests {
    @Autowired
    private UserService userService;

    /**
     * Test Mybatis-Plus new
     */
    @Test
    public void testSave() {
        String salt = IdUtil.fastSimpleUUID();
        User testSave3 = User.builder().name("testSave3").password(SecureUtil.md5("123456" + salt)).salt(salt).email("testSave3@xkcoding.com").phoneNumber("17300000003").status(1) .lastLoginTime(new DateTime()).build();
        boolean save = userService.save(testSave3);
        Assert.assertTrue(save);
        log.debug("[testid echo #testSave3.getId()] = {}", testSave3.getId());
    }

    /**
     * Test Mybatis-Plus batch added
     */
    @Test
    public void testSaveList() {
        List<User> userList = Lists.newArrayList();
        for (int i = 4; i < 14; i++) {
            String salt = IdUtil.fastSimpleUUID();
            User user = User.builder().name("testSave" + i).password(SecureUtil.md5("123456" + salt)).salt(salt).email("testSave" + i + "@xkcoding.com").phoneNumber("1730000000" + i).status(1 ).lastLoginTime(new DateTime()).build();
            userList.add(user);
        }
        boolean batch = userService.saveBatch(userList);
        Assert.assertTrue(batch);
        List<Long> ids = userList.stream().map(User::getId).collect(Collectors.toList());
        log.debug("【userList#ids】= {}", ids);
    }

    /**
     * Test Mybatis-Plus delete
     */
    @Test
    public void testDelete() {
        boolean remove = userService.removeById(1L);
        Assert.assertTrue(remove);
        User byId = userService.getById(1L);
        Assert.assertNull(byId);
    }

    /**
     * Test Mybatis-Plus modifications
     */
    @Test
    public void testUpdate() {
        User user = userService.getById(1L);
        Assert.assertNotNull(user);
        user.setName ("MybatisPlus Modified Name");
        boolean b = userService.updateById(user);
        Assert.assertTrue(b);
        User update = userService.getById(1L);
        Assert.assertEquals ("MybatisPlus name modified", update.getName());
        log.debug("【update】= {}", update);
    }

    /**
     * Test Mybatis-Plus query single
     */
    @Test
    public void testQueryOne() {
        User user = userService.getById(1L);
        Assert.assertNotNull(user);
        log.debug("【user】= {}", user);
    }

    /**
     * Test Mybatis-Plus query all
     */
    @Test
    public void testQueryAll() {
        List<User> list = userService.list(new QueryWrapper<>());
        Assert.assertTrue(CollUtil.isNotEmpty(list));
        log.debug("【list】= {}", list);
    }

    /**
     * Test Mybatis-Plus paginated sort query
     */
    @Test
    public void testQueryByPageAndSort() {
        initData();
        int count = userService.count(new QueryWrapper<>());
        Page<User> userPage = new Page<>(1, 5);
        userPage.setDesc("id");
        IPage<User> page = userService.page(userPage, new QueryWrapper<>());
        Assert.assertEquals(5, page.getSize());
        Assert.assertEquals(count, page.getTotal());
        log.debug("【page.getRecords()】= {}", page.getRecords());
    }

    /**
     * Test Mybatis-Plus custom queries
     */
    @Test
    public void testQueryByCondition() {
        initData();
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.like("name", "Save1").or().eq("phone_number", "17300000001").orderByDesc("id");
        int count = userService.count(wrapper);
        Page<User> userPage = new Page<>(1, 3);
        IPage<User> page = userService.page(userPage, wrapper);
        Assert.assertEquals(3, page.getSize());
        Assert.assertEquals(count, page.getTotal());
        log.debug("【page.getRecords()】= {}", page.getRecords());
    }

    /**
     * Initialize data
     */
    private void initData() {
        testSaveList();
    }

}
```

## Added on 2019-09-14

### ActiveRecord mode

- Role.java

```java
/**
 * <p>
 * Role entity class
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-09-16 14:04
 */
@Data
@TableName("orm_role")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class Role extends Model<Role> {
    /**
     * Primary key
     */
    private Long id;

    /**
     * Role name
     */
    private String name;

    /**
     * Primary key value, ActiveRecord mode this must have, otherwise xxById method will be invalidated!
     * Even if RoleMapper is not used with ActiveRecord, the RoleMapper interface must be created
     */
    @Override
    protected Serializable pkVal() {

        return this.id;
    }
}
```

- RoleMapper.java

```java
/**
 * <p>
 * RoleMapper
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-09-16 14:06
 */
public interface RoleMapper extends BaseMapper<Role> {
}
```

- ActiveRecordTest.java

```java
/**
 * <p>
 * Role
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-09-16 14:19
 */
@Slf4j
public class ActiveRecordTest extends SpringBootDemoOrmMybatisPlusApplicationTests {
    /**
     * Test ActiveRecord insertion data
     */
    @Test
    public void testActiveRecordInsert() {
        Role role = new Role();
        role.setName("VIP");
        Assert.assertTrue(role.insert());
        Successfully get the ID that can be written directly
        log.debug("【role】= {}", role);
    }

    /**
     * Test ActiveRecord update data
     */
    @Test
    public void testActiveRecordUpdate() {
        Assert.assertTrue (new Role().setId(1L).setName("Admin-1").updateById());
        Assert.assertTrue (new Role().update(new UpdateWrapper().lambda().set(Role::<Role>getName, "normal user-1").eq(Role::getId, 2)));
    }

    /**
     * Test ActiveRecord query data
     */
    @Test
    public void testActiveRecordSelect() {
        Assert.assertEquals("Administrator", new Role().setId(1L).selectById().getName());
        Role role = new Role().selectOne(new QueryWrapper<Role>().lambda().eq(Role::getId, 2));
        Assert.assertEquals ("Ordinary User", role.getName());
        List<Role> roles = new Role().selectAll();
        Assert.assertTrue(roles.size() > 0);
        log.debug("【roles】= {}", roles);
    }

    /**
     * Test ActiveRecord deletes data
     */
    @Test
    public void testActiveRecordDelete() {
        Assert.assertTrue(new Role().setId(1L).deleteById());
        Assert.assertTrue (new Role().delete(new QueryWrapper().<Role>lambda().eq(Role::getName, "Regular User")));
    }
}
```

## Reference

- mybatis-plus Official Documentation: http://mp.baomidou.com/

