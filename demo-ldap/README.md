# spring-boot-demo-ldap

> This demo mainly demonstrates how Spring Boot integrates 'spring-boot-starter-data-ldap' to complete basic curd operations on LDAP, and gives examples of APIs that are practical for login

## docker openldap installation steps

> Reference: https://github.com/osixia/docker-openldap
1. Download image: 'docker pull osixia/openldap:1.2.5'

2. Run the container: 'docker run -p 389:389 -p 636:636 --name my-openldap --detach osixia/openldap:1.2.5'

3. Add an administrator: 'docker exec my-openldap ldapsearch -x -H ldap://localhost -b dc=example,dc=org -D "cn=admin,dc=example,dc=org" -w admin'

4. Stop container: 'docker stop my-openldap'

5. Start the container: 'docker start my-openldap'


## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <artifactId>spring-boot-demo-ldap</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <packaging>jar</packaging>

  <name>spring-boot-demo-ldap</name>
  <description>Demo project for Spring Boot</description>

  <parent>
    <artifactId>spring-boot-demo</artifactId>
    <groupId>com.xkcoding</groupId>
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
      <artifactId>spring-boot-starter-data-ldap</artifactId>
    </dependency>

    <!-- test -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-test</artifactId>
      <scope>test</scope>
    </dependency>

    <!-- lombok -->
    <dependency>
      <groupId>org.projectlombok</groupId>
      <artifactId>lombok</artifactId>
      <optional>true</optional>
      <scope>provided</scope>
    </dependency>
  </dependencies>

</project>
```

## application.yml

```yaml
spring:
  ldap:
    urls: ldap://localhost:389
    base: dc=example,dc=org
    username: cn=admin,dc=example,dc=org
    password: admin
```

## Person.java

> Entity classes
> @Entry annotations map ldap object relationships
```java
/**
 * People
 *
 * @author fxbin
 * @version v1.0
 * @since 2019-08-26 0:51
 */
@Data
@Entry(
    base = "ou=people",
    objectClasses = {"posixAccount", "inetOrgPerson", "top"}
)
public class Person implements Serializable {

    private static final long serialVersionUID = -7946768337975852352L;

    @Id
    private Name id;

    private String uidNumber;

    private String gidNumber;

    /**
     * Username
     */
    @DnAttribute(value = "uid", index = 1)
    private String uid;

    /**
     * Name
     */
    @Attribute(name = "cn")
    private String personName;

    /**
     * Password
     */
    private String userPassword;

    /**
     * First name
     */
    private String givenName;

    /**
     * Last name
     */
    @Attribute(name = "sn")
    private String surname;

    /**
     * Email
     */
    private String mail;

    /**
     * Position
     */
    private String title;

    /**
     * Root directory
     */
    private String homeDirectory;

    /**
     * loginShell
     */
    private String loginShell;
}
```

## PersonRepository.java
> person data persistence layer
```java
/**
 * PersonRepository
 *
 * @author fxbin
 * @version v1.0
 * @since 2019-08-26 1:02
 */
@Repository
public interface PersonRepository extends CrudRepository<Person, Name> {

    /**
     * Find based on username
     *
     * @param uid username
     * @return com.xkcoding.ldap.entity.Person
     */
    Person findByUid(String uid);
}
```

## PersonService.java
> Data Manipulation Service
```java
/**
 * PersonService
 *
 * @author fxbin
 * @version v1.0
 * @since 2019-08-26 1:05
 */
public interface PersonService {

    /**
     * Login
     *
     * @param request {@link LoginRequest}
     * @return {@link Result}
     */
    Result login(LoginRequest request);

    /**
     * Enquire all
     *
     * @return {@link Result}
     */
    Result listAllPerson();

    /**
     * Save
     *
     * @param person {@link Person}
     */
    void save(Person person);

    /**
     * Delete
     *
     * @param person {@link Person}
     */
    void delete(Person person);

}
```

## PersonServiceImpl.java
> personData Manipulation Service specific logic implementation class

```java
/**
 * PersonServiceImpl
 *
 * @author fxbin
 * @version v1.0
 * @since 2019-08-26 1:05
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepository;

    /**
     * Login
     *
     * @param request {@link LoginRequest}
     * @return {@link Result}
     */
    @Override
    public Result login(LoginRequest request) {
        log.info("IN LDAP auth");

        Person user = personRepository.findByUid(request.getUsername());

        try {
            if (ObjectUtils.isEmpty(user)) {
                throw new ServiceException ("Username or password error, please try again");
            } else {
                user.setUserPassword(LdapUtils.asciiToString(user.getUserPassword()));
                if (! LdapUtils.verify(user.getUserPassword(), request.getPassword())) {
                    throw new ServiceException ("Username or password error, please try again");
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        log.info("user info:{}", user);
        return Result.success(user);
    }

    /**
     * Enquire all
     *
     * @return {@link Result}
     */
    @Override
    public Result listAllPerson() {
        Iterable<Person> personList = personRepository.findAll();
        personList.forEach(person -> person.setUserPassword(LdapUtils.asciiToString(person.getUserPassword())));
        return Result.success(personList);
    }

    /**
     * Save
     *
     * @param person {@link Person}
     */
    @Override
    public void save(Person person) {
        Person p = personRepository.save(person);
        log.info ("User {} saved successfully", p.getUid());
    }

    /**
     * Delete
     *
     * @param person {@link Person}
     */
    @Override
    public void delete(Person person) {
        personRepository.delete(person);
        log.info ("Delete user {} successful", person.getUid());
    }

}
```

## LdapDemoApplicationTests.java
> Testing
```java
/**
 * LdapDemoApplicationTest
 *
 * @author fxbin
 * @version v1.0
 * @since 2019-08-26 1:06
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class LdapDemoApplicationTests {

    @Resource
    private PersonService personService;

    @Test
    public void contextLoads() {
    }

    /**
     * Test query single
     */
    @Test
    public void loginTest() {
        LoginRequest loginRequest = LoginRequest.builder().username("wangwu").password("123456").build();
        Result login = personService.login(loginRequest);
        System.out.println(login);
    }

    /**
     * Test query list
     */
    @Test
    public void listAllPersonTest() {
        Result result = personService.listAllPerson();
        System.out.println(result);
    }

    /**
     * Test save
     */
    @Test
    public void saveTest() {
        Person person = new Person();

        person.setUid("zhaosi");

        person.setSurname ("Zhao");
        person.setGivenName ("four");
        person.setUserPassword("123456");

         required field
        person.setPersonName ("Zhao Si");
        person.setUidNumber("666");
        person.setGidNumber("666");
        person.setHomeDirectory("/home/zhaosi");
        person.setLoginShell("/bin/bash");

        personService.save(person);
    }

    /**
     * Test removal
     */
    @Test
    public void deleteTest() {
        Person person = new Person();
        person.setUid("zhaosi");

        personService.delete(person);
    }

}
```

## See this demo for the rest of the code

## Reference

spring-data-ldap official documentation: https://docs.spring.io/spring-data/ldap/docs/2.1.10.RELEASE/reference/html/
