# spring-boot-demo-orm-jpa
> This demo demonstrates how Spring Boot can use JPA to manipulate databases, including simple use as well as cascading use.

## Main code

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>spring-boot-demo-orm-jpa</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>spring-boot-demo-orm-jpa</name>
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
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
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
        <finalName>spring-boot-demo-orm-jpa</finalName>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```
###  JpaConfig.java
```java
/**
 * <p>
 * JPA configuration class
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-07 11:05
 */
@Configuration
@EnableTransactionManagement
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.xkcoding.orm.jpa.repository", transactionManagerRef = "jpaTransactionManager")
public class JpaConfig {
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        HibernateJpaVendorAdapter japVendor = new HibernateJpaVendorAdapter();
        japVendor.setGenerateDdl(false);
        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(dataSource());
        entityManagerFactory.setJpaVendorAdapter(japVendor);
        entityManagerFactory.setPackagesToScan("com.xkcoding.orm.jpa.entity");
        return entityManagerFactory;
    }

    @Bean
    public PlatformTransactionManager jpaTransactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }
}
```
###  User.java
```java
/**
 * <p>
 * User entity class
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-07 14:06
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "orm_user")
@ToString(callSuper = true)
public class User extends AbstractAuditModel {
    /**
     * Username
     */
    private String name;

    /**
     * Encrypted password
     */
    private String password;

    /**
     * Salt used for encryption
     */
    private String salt;

    /**
     * Email
     */
    private String email;

    /**
     * Mobile phone number
     */
    @Column(name = "phone_number")
    private String phoneNumber;

    /**
     * Status, -1: Tombstone, 0: Disabled, 1: Enabled
     */
    private Integer status;

    /**
     * Last login time
     */
    @Column(name = "last_login_time")
    private Date lastLoginTime;

    /**
     * Associated department table
     * 1. Relationship maintenance end, responsible for the binding and disarming of many-to-many relationships
     * 2, @JoinTable the name attribute of the annotation specifies the name of the association table, joinColumns specifies the name of the foreign key, and is associated with the relationship maintenance side (User)
     * 3, inverseJoinColumns specifies the name of the foreign key, and the relationship to be associated is maintained (Department)
     * 4, in fact, you can not use the @JoinTable annotation, the default generated associated table name is the main table name + underscore + slave table name,
     * That is, the table name is user_department
     * Foreign key name associated to the main table: primary table name + underscore + primary key column name in the primary table, that is, user_id, specified here using referencedColumnName
     * Foreign key name associated to slave table: The attribute name used in the main table for association + underscore + primary key column name of the slave table, department_id
     * The primary table is the table corresponding to the relationship maintenance side, and the slave table is the table corresponding to the relationship maintenance side
     */
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "orm_user_dept", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "dept_id", referencedColumnName = " id"))
    private Collection<Department> departmentList;

}
```
### Department.java
```java
/**
 * <p>
 * Department entity class
 * </p>
 *
 * @author 76peter
 * @date Created in 2019-10-01 18:07
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "orm_department")
@ToString(callSuper = true)
public class Department extends AbstractAuditModel {

    /**
     * Department name
     */
    @Column(name = "name", columnDefinition = "varchar(255) not null")
    private String name;

    /**
     * Parent department id
     */
    @ManyToOne(cascade = {CascadeType.REFRESH}, optional = true)
    @JoinColumn(name = "superior", referencedColumnName = "id")
    private Department superior;
    /**
     * Level
     */
    @Column(name = "levels", columnDefinition = "int not null default 0")
    private Integer levels;
    /**
     * Sort
     */
    @Column(name = "order_no", columnDefinition = "int not null default 0")
    private Integer orderNo;
    /**
     * Sub-department collection
     */
    @OneToMany(cascade = {CascadeType.REFRESH, CascadeType.REMOVE}, fetch = FetchType.EAGER, mappedBy = "superior")
    private Collection<Department> children;

    /**
     * Collection of users under the department
     */
    @ManyToMany(mappedBy = "departmentList")
    private Collection<User> userList;

}
```
### AbstractAuditModel.java
```java
/**
 * <p>
 * Entity generic parent class
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-07 14:01
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Data
public abstract class AbstractAuditModel implements Serializable {
    /**
     * Primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Creation time
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time", nullable = false, updatable = false)
    @CreatedDate
    private Date createTime;

    /**
     * Last updated
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_update_time", nullable = false)
    @LastModifiedDate
    private Date lastUpdateTime;
}
```
### UserDao.java
```java
/**
 * <p>
 * User Dao
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-07 14:07
 */
@Repository
public interface UserDao extends JpaRepository<User, Long> {

}
```
### DepartmentDao.java
```java
/**
 * <p>
 * User Dao
 * </p>
 *
 * @author 76peter
 * @date Created in 2019-10-01 18:07
 */
@Repository
public interface DepartmentDao extends JpaRepository<Department, Long> {
    /**
     * Query departments based on hierarchy
     *
     * @param level level
     * @return List of departments
     */
    List<Department> findDepartmentsByLevels(Integer level);
}
```
### application.yml
```yaml
server:
  port: 8080
  servlet:
    context-path: /demo
spring:
  datasource:
    jdbc-url: jdbc:mysql://127.0.0.1:3306/spring-boot-demo?useUnicode=true&characterEncoding=UTF-8&useSSL=false&autoReconnect=true&failOverReadOnly=false&serverTimezone= GMT%2B8
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
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: validate
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
### UserDaoTest.java
```java
/**
 * <p>
 * jpa test class
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-07 14:09
 */
@Slf4j
public class UserDaoTest extends SpringBootDemoOrmJpaApplicationTests {
    @Autowired
    private UserDao userDao;

    /**
     * Test save
     */
    @Test
    public void testSave() {
        String salt = IdUtil.fastSimpleUUID();
        User testSave3 = User.builder().name("testSave3").password(SecureUtil.md5("123456" + salt)).salt(salt).email("testSave3@xkcoding.com").phoneNumber("17300000003").status(1) .lastLoginTime(new DateTime()).build();
        userDao.save(testSave3);

        Assert.assertNotNull(testSave3.getId());
        Optional<User> byId = userDao.findById(testSave3.getId());
        Assert.assertTrue(byId.isPresent());
        log.debug("【byId】= {}", byId.get());
    }

    /**
     * Test removal
     */
    @Test
    public void testDelete() {
        long count = userDao.count();
        userDao.deleteById(1L);
        long left = userDao.count();
        Assert.assertEquals(count - 1, left);
    }

    /**
     * Test modifications
     */
    @Test
    public void testUpdate() {
        userDao.findById(1L).ifPresent(user -> {
            user.setName ("JPA Modified Name");
            userDao.save(user);
        });
        Assert.assertEquals ("JPA modified name", userDao.findById(1L).get().getName());
    }

    /**
     * Test query single
     */
    @Test
    public void testQueryOne() {
        Optional<User> byId = userDao.findById(1L);
        Assert.assertTrue(byId.isPresent());
        log.debug("【byId】= {}", byId.get());
    }

    /**
     * Test query all
     */
    @Test
    public void testQueryAll() {
        List<User> users = userDao.findAll();
        Assert.assertNotEquals(0, users.size());
        log.debug("【users】= {}", users);
    }

    /**
     * Test paginated sort query
     */
    @Test
    public void testQueryPage() {
        Initialize the data
        initData();
        JPA pagination when the starting page is page number minus 1
        Integer currentPage = 0;
        Integer pageSize = 5;
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest pageRequest = PageRequest.of(currentPage, pageSize, sort);
        Page<User> userPage = userDao.findAll(pageRequest);

        Assert.assertEquals(5, userPage.getSize());
        Assert.assertEquals(userDao.count(), userPage.getTotalElements());
        log.debug("【id】= {}", userPage.getContent().stream().map(User::getId).collect(Collectors.toList()));
    }

    /**
     * Initialize 10 pieces of data
     */
    private void initData() {
        List<User> userList = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            String salt = IdUtil.fastSimpleUUID();
            int index = 3 + i;
            User user = User.builder().name("testSave" + index).password(SecureUtil.md5("123456" + salt)).salt(salt).email("testSave" + index + "@xkcoding.com").phoneNumber("1730000000" + index).status(1).lastLoginTime(new DateTime()).build();
            userList.add(user);
        }
        userDao.saveAll(userList);
    }

}
```
### DepartmentDaoTest.java
```java
/**
 * <p>
 * jpa test class
 * </p>
 *
 * @author 76peter
 * @date Created in 2018-11-07 14:09
 */
@Slf4j
public class DepartmentDaoTest extends SpringBootDemoOrmJpaApplicationTests {
    @Autowired
    private DepartmentDao departmentDao;
    @Autowired
    private UserDao userDao;

    /**
     * Test save, root node
     */
    @Test
    @Transactional
    public void testSave() {
        Collection<Department> departmentList = departmentDao.findDepartmentsByLevels(0);

        if (departmentList.size() == 0) {
            Department testSave1 = Department.builder().name("testSave1").orderNo(0).levels(0).superior(null).build();
            Department testSave1_1 = Department.builder().name("testSave1_1").orderNo(0).levels(1).superior(testSave1).build();
            Department testSave1_2 = Department.builder().name("testSave1_2").orderNo(0).levels(1).superior(testSave1).build();
            Department testSave1_1_1 = Department.builder().name("testSave1_1_1").orderNo(0).levels(2).superior(testSave1_1).build();
            departmentList.add(testSave1);
            departmentList.add(testSave1_1);
            departmentList.add(testSave1_2);
            departmentList.add(testSave1_1_1);
            departmentDao.saveAll(departmentList);

            Collection<Department> deptall = departmentDao.findAll();
            log.debug("[Department] = {}", JSONArray.toJSONString((List) deptall));
        }


        userDao.findById(1L).ifPresent(user -> {
            user.setName ("Add Department");
            Department dept = departmentDao.findById(2L).get();
            user.setDepartmentList(departmentList);
            userDao.save(user);
        });

        log.debug("User department={}", JSONUtil.toJsonStr(userDao.findById(1L).get().getDepartmentList()));


        departmentDao.findById(2L).ifPresent(dept -> {
            Collection<User> userlist = dept.getUserList();
            The association relationship is maintained by the user intermediate table, the department userlist does not change, and the query method can be added to handle the override getUserList method
            log.debug("Under the department user={}", JSONUtil.toJsonStr(userlist));
        });


        userDao.findById(1L).ifPresent(user -> {
            user.setName ("Empty Sector");
            user.setDepartmentList(null);
            userDao.save(user);
        });
        log.debug("User department={}", userDao.findById(1L).get().getDepartmentList());

    }
}
```

### The rest of the code and SQL can be found in this demo

## Reference

- Spring Data JPA Official Documentation: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/
