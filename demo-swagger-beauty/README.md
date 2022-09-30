# spring-boot-demo-swagger-beauty

> This demo mainly demonstrates how to integrate third-party swaggers to replace native swaggers and beautify document styles. This demo uses [swagger-spring-boot-starter](https://github.com/battcn/swagger-spring-boot) integration.
>
> Start Project, Access Address: http://localhost:8080/demo/swagger-ui.html#/
>
> Username: xkcoding
>
> Password: 123456

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>spring-boot-demo-swagger-beauty</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>spring-boot-demo-swagger-beauty</name>
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
        <battcn.swagger.version>2.1.2-RELEASE</battcn.swagger.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>com.battcn</groupId>
            <artifactId>swagger-spring-boot-starter</artifactId>
            <version>${battcn.swagger.version}</version>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>

    <build>
        <finalName>spring-boot-demo-swagger-beauty</finalName>
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
server:
  port: 8080
  servlet:
    context-path: /demo
spring:
  swagger:
    enabled: true
    title: spring-boot-demo
    description: This is a simple Swagger API demo
    version: 1.0.0-SNAPSHOT
    contact:
      name: Yangkai.Shen
      email: 237497819@qq.com
      url: http://xkcoding.com
    # swagger scans the underlying package, default: full scan
    # base-package:
    # The basic URL rule that needs to be processed, default: /**
    # base-path:
    # URL rules that need to be excluded, default: empty
    # exclude-path:
    security:
      # Whether to enable swagger login verification
      filter-plugin: true
      username: xkcoding
      password: 123456
    global-response-messages:
      GET[0]:
        code: 400
        message: Bad Request, usually the request parameter is incorrect
      GET[1]:
        code: 404
        message: NOT FOUND, generally the request path is not correct
      GET[2]:
        code: 500
        message: ERROR, generally an internal error
      POST[0]:
        code: 400
        message: Bad Request, usually the request parameter is incorrect
      POST[1]:
        code: 404
        message: NOT FOUND, generally the request path is not correct
      POST[2]:
        code: 500
        message: ERROR, generally an internal error
```

## ApiResponse.java

```java
/**
 * <p>
 * Generic API interface returns
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-28 14:18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel (value = "Common PI interface return", description = "Common Api Response")
public class ApiResponse<T> implements Serializable {
    private static final long serialVersionUID = -8987146499044811408L;
    /**
     * Universal return status
     */
    @ApiModelProperty(value = "generic return status", required = true)
    private Integer code;
    /**
     * Generic return information
     */
    @ApiModelProperty (value = "generic return information", required = true)
    private String message;
    /**
     * Generic return data
     */
    @ApiModelProperty(value = "generic return data", required = true)
    private T data;
}
```

## User.java

```java
/**
 * <p>
 * User entity
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-28 14:13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel (value = "User Entity", description = "User Entity")
public class User implements Serializable {
    private static final long serialVersionUID = 5057954049311281252L;
    /**
     * Primary key id
     */
    @ApiModelProperty (value = "primary key id", required = true)
    private Integer id;
    /**
     * Username
     */
    @ApiModelProperty(value = "username", required = true)
    private String name;
    /**
     * Jobs
     */
    @ApiModelProperty (value = "job", required = true)
    private String job;
}
```

## UserController.java

```java
/**
 * <p>
 * User Controller
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-28 14:25
 */
@RestController
@RequestMapping("/user")
@Api (tags = "1.0.0-SNAPSHOT", description = "User Management", value = "User Management")
@Slf4j
public class UserController {
    @GetMapping
    @ApiOperation (value = "DONE", notes = "Notes")
    @ApiImplicitParams({@ApiImplicitParam(name = "username", value = "username", dataType = DataType.STRING, paramType = ParamType.QUERY, defaultValue = "xxx")})
    public ApiResponse<User> getByUserName(String username) {
        log.info ("multiple parameters with @ApiImplicitParams");
        return ApiResponse. builder().<User>code(200).message("Operation Successful").data(new User(1, username, "JAVA")).build();
    }

    @GetMapping("/{id}")
    @ApiOperation (value = "Primary Key Query (DONE)", notes = "Comment")
    @ApiImplicitParams ({@ApiImplicitParam(name = "id", value = "user number", dataType = DataType.INT, paramType = ParamType.PATH)})
    public ApiResponse<User> get(@PathVariable Integer id) {
        log.info ("@ApiImplicitParam for a single parameter");
        return ApiResponse. builder().<User>code(200).message("Operation Successful").data(new User(id, "u1", "p1")).build();
    }

    @DeleteMapping("/{id}")
    @ApiOperation (value = "DONE", notes = "note")
    @ApiImplicitParam(name = "id", value = "user number", dataType = DataType.INT, paramType = ParamType.PATH)
    public void delete(@PathVariable Integer id) {
        log.info ("Single parameter with ApiImplicitParam");
    }

    @PostMapping
    @ApiOperation (value = "DONE")
    public User post(@RequestBody User user) {
        log.info ("If it is a POST PUT with a @RequestBody, you can not write a @ApiImplicitParam");
        return user;
    }

    @PostMapping("/multipar")
    @ApiOperation (value = "DONE")
    public List<User> multipar(@RequestBody List<User> user) {
        log.info ("If it is a POST PUT with a @RequestBody, you can not write a @ApiImplicitParam");

        return user;
    }

    @PostMapping("/array")
    @ApiOperation (value = "DONE")
    public User[] array(@RequestBody User[] user) {
        log.info ("If it is a POST PUT with a @RequestBody, you can not write a @ApiImplicitParam");
        return user;
    }

    @PutMapping("/{id}")
    @ApiOperation (value = "DONE")
    public void put(@PathVariable Long id, @RequestBody User user) {
        log.info ("If you don't want to write @ApiImplicitParam then swagger will also use the default parameter name as the description information");
    }

    @PostMapping("/{id}/file")
    @ApiOperation (value = "File upload (DONE)")
    public String file(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        log.info(file.getContentType());
        log.info(file.getName());
        log.info(file.getOriginalFilename());
        return file.getOriginalFilename();
    }
}
```

## Reference

- https://github.com/battcn/swagger-spring-boot/blob/master/README.md
- Several of the more beautiful swagger-ui, the specific use of which can be found in the official documentation of each dependent:
  - [battcn](https://github.com/battcn) of [swagger-spring-boot-starter](https://github.com/battcn/swagger-spring-boot) Document: https://github.com/battcn/swagger-spring-boot/blob/master/README.md
  - [ swagger-ui-layer] (https://gitee.com/caspar-chen/Swagger-UI-layer) Documentation: https://gitee.com/caspar-chen/Swagger-UI-layer#%E5%A6%82%E4%BD%95%E4%BD%BF%E7%94%A8
  - [swagger-bootstrap-ui] (https://gitee.com/xiaoym/swagger-bootstrap-ui) Documentation: https://gitee.com/xiaoym/swagger-bootstrap-ui#%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E
  - [swagger-ui-themes] (https://github.com/ostranme/swagger-ui-themes) Documentation: https://github.com/ostranme/swagger-ui-themes#getting-started
