# spring-boot-demo-orm-beetlsql

> This demo mainly demonstrates how Spring Boot integrates the beetl sql quick operation database, using the beetl-framework-starter integration provided by beetl. The integration process is not very smooth, and there is no other orm framework integration convenience.

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>spring-boot-demo-orm-beetlsql</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>spring-boot-demo-orm-beetlsql</name>
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
        <ibeetl.version>1.1.68.RELEASE</ibeetl.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>

        <dependency>
            <groupId>com.ibeetl</groupId>
            <artifactId>beetl-framework-starter</artifactId>
            <version>${ibeetl.version}</version>
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
        <finalName>spring-boot-demo-orm-beetlsql</finalName>
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

> Note where the comments below are, **Cannot uncomment and need to manually configure the data source through JavaConfig**, otherwise, it will cause beetl startup failure, so to initialize the data of the database, you can only manually use the table building statements and database initialization data under resources/db in the database.

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/spring-boot-demo?useUnicode=true&characterEncoding=UTF-8&useSSL=false&autoReconnect=true&failOverReadOnly=false&serverTimezone=GMT%2B8
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
#### beetlsql starter does not open the following options
#    type: com.zaxxer.hikari.HikariDataSource
#    initialization-mode: always
#    continue-on-error: true
#    schema:
#    - "classpath:db/schema.sql"
#    data:
#    - "classpath:db/data.sql"
#    hikari:
#      minimum-idle: 5
#      connection-test-query: SELECT 1 FROM DUAL
#      maximum-pool-size: 20
#      auto-commit: true
#      idle-timeout: 30000
#      pool-name: SpringBootDemoHikariCP
#      max-lifetime: 60000
#      connection-timeout: 30000
logging:
  level:
    com.xkcoding: debug
    com.xkcoding.orm.beetlsql: trace
beetl:
  enabled: false
beetlsql:
  enabled: true
  sqlPath: /sql
  daoSuffix: Dao
  basePackage: com.xkcoding.orm.beetlsql.dao
  dbStyle: org.beetl.sql.core.db.MySqlStyle
  nameConversion: org.beetl.sql.core.UnderlinedNameConversion
beet-beetlsql:
  dev: true
```

## BeetlConfig.java

```java
/**
 * <p>
 * Beetl data source configuration
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-14 17:15
 */
@Configuration
public class BeetlConfig {

    /**
     * Beettl needs to display the configuration data source to start the project, big pit, remember!
     */
    @Bean(name = "datasource")
    public DataSource getDataSource(Environment env){
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
        dataSource.setJdbcUrl(env.getProperty("spring.datasource.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.password"));
        return dataSource;
    }
}
```

## UserDao.java

```java
/**
 * <p>
 * UserDao
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-14 16:18
 */
@Component
public interface UserDao extends BaseMapper<User> {

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
 * @date Created in 2018-11-14 16:28
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * New users
     *
     * @param user user
     */
    @Override
    public User saveUser(User user) {
        userDao.insert(user, true);
        return user;
    }

    /**
     * Batch insert users
     *
     * @param users user list
     */
    @Override
    public void saveUserList(List<User> users) {
        userDao.insertBatch(users);
    }

    /**
     * Delete users based on primary keys
     *
     * @param id primary key
     */
    @Override
    public void deleteUser(Long id) {
        userDao.deleteById(id);
    }

    /**
     * Update users
     *
     * @param user user
     * @return updated users
     */
    @Override
    public User updateUser(User user) {
        if (ObjectUtil.isNull(user)) {
            throw new RuntimeException ("user id cannot be null");
        }
        userDao.updateTemplateById(user);
        return userDao.single(user.getId());
    }

    /**
     * Query for a single user
     *
     * @param id primary key id
     * @return User information
     */
    @Override
    public User getUser(Long id) {
        return userDao.single(id);
    }

    /**
     * Query the list of users
     *
     * @return User list
     */
    @Override
    public List<User> getUserList() {
        return userDao.all();
    }

    /**
     * Paginated queries
     *
     * @param currentPage current page
     * @param pageSize per page
     * @return Paginated user list
     */
    @Override
    public PageQuery<User> getUserByPage(Integer currentPage, Integer pageSize) {
        return userDao.createLambdaQuery().page(currentPage, pageSize);
    }
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
 * @date Created in 2018-11-14 16:30
 */
@Slf4j
public class UserServiceTest extends SpringBootDemoOrmBeetlsqlApplicationTests {
    @Autowired
    private UserService userService;

    @Test
    public void saveUser() {
        String salt = IdUtil.fastSimpleUUID();
        User user = User.builder().name("testSave3").password(SecureUtil.md5("123456" + salt)).salt(salt).email("testSave3@xkcoding.com").phoneNumber("17300000003").status(1). lastLoginTime(new DateTime()).createTime(new DateTime()).lastUpdateTime(new DateTime()).build();

        user = userService.saveUser(user);
        Assert.assertTrue(ObjectUtil.isNotNull(user.getId()));
        log.debug("【user】= {}", user);
    }

    @Test
    public void saveUserList() {
        List<User> users = Lists.newArrayList();
        for (int i = 5; i < 15; i++) {
            String salt = IdUtil.fastSimpleUUID();
            User user = User.builder().name("testSave" + i).password(SecureUtil.md5("123456" + salt)).salt(salt).email("testSave" + i + "@xkcoding.com").phoneNumber("1730000000" + i).status(1 ).lastLoginTime(new DateTime()).createTime(new DateTime()).lastUpdateTime(new DateTime()).build();
            users.add(user);
        }
        userService.saveUserList(users);
        Assert.assertTrue(userService.getUserList().size() > 2);
    }

    @Test
    public void deleteUser() {
        userService.deleteUser(1L);
        User user = userService.getUser(1L);
        Assert.assertTrue(ObjectUtil.isNull(user));
    }

    @Test
    public void updateUser() {
        User user = userService.getUser(2L);
        user.setName ("beetlSql modified name");
        User update = userService.updateUser(user);
        Assert.assertEquals ("beetlSql modified name", update.getName());
        log.debug("【update】= {}", update);
    }

    @Test
    public void getUser() {
        User user = userService.getUser(1L);
        Assert.assertNotNull(user);
        log.debug("【user】= {}", user);
    }

    @Test
    public void getUserList() {
        List<User> userList = userService.getUserList();
        Assert.assertTrue(CollUtil.isNotEmpty(userList));
        log.debug("【userList】= {}", userList);
    }

    @Test
    public void getUserByPage() {
        List<User> userList = userService.getUserList();
        PageQuery<User> userByPage = userService.getUserByPage(1, 5);
        Assert.assertEquals(5, userByPage.getList().size());
        Assert.assertEquals(userList.size(), userByPage.getTotalRow());
        log.debug("【userByPage】= {}", JSONUtil.toJsonStr(userByPage));
    }
}
```

## Reference

- Official BeetlSQL Documentation: http://ibeetl.com/guide/#beetlsql
- Open Source Project: https://gitee.com/yangkb/springboot-beetl-beetlsql
- Blog: https://blog.csdn.net/flystarfly/article/details/82752597
