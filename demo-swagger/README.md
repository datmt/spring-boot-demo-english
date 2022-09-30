# spring-boot-demo-swagger

> This demo demonstrates how Spring Boot integrates with native swagger to automatically generate API documentation.
>
> Start Project, Access Address: http://localhost:8080/demo/swagger-ui.html#/

# pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>spring-boot-demo-swagger</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>spring-boot-demo-swagger</name>
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
        <swagger.version>2.9.2</swagger.version>
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
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger2</artifactId>
            <version>${swagger.version}</version>
        </dependency>

        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger-ui</artifactId>
            <version>${swagger.version}</version>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>

    <build>
        <finalName>spring-boot-demo-swagger</finalName>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

## Swagger2Config.java

```java
/**
 * <p>
 * Swagger2 configuration
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-29 11:14
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.xkcoding.swagger.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("spring-boot-demo")
                .description ("This is a simple Swagger API demo")
                .contact(new Contact("Yangkai.Shen", "http://xkcoding.com", "237497819@qq.com"))
                .version("1.0.0-SNAPSHOT")
                .build();
    }

}
```

## UserController.java

> mainly demonstrates the annotations of the API layer.

```java
/**
 * <p>
 * User Controller
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-29 11:30
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
        return ApiResponse. <User>builder().code(200)
                .message ("operation succeeded")
                .data(new User(1, username, "JAVA"))
                .build();
    }

    @GetMapping("/{id}")
    @ApiOperation (value = "Primary Key Query (DONE)", notes = "Comment")
    @ApiImplicitParams ({@ApiImplicitParam(name = "id", value = "user number", dataType = DataType.INT, paramType = ParamType.PATH)})
    public ApiResponse<User> get(@PathVariable Integer id) {
        log.info ("@ApiImplicitParam for a single parameter");
        return ApiResponse. <User>builder().code(200)
                .message ("operation succeeded")
                .data(new User(id, "u1", "p1"))
                .build();
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

## ApiResponse.java

> mainly demonstrates the annotation of entity classes.

```java
/**
 * <p>
 * Generic API interface returns
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-29 11:30
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

## Reference

1. Swagger official website: https://swagger.io/

2. Swagger Official Documentation: https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Getting-started

3. Swagger Common Note: https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Annotations
