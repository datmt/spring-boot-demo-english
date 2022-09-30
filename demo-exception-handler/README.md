# spring-boot-demo-exception-handler

> This demo demonstrates how to unify exception handling in Spring Boot, including two ways of handling: the first is exception handling for interfaces in the form of common APIs, and the unified encapsulation return format is used; The second is exception handling of template page requests and unified handling of error pages.

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<artifactId>spring-boot-demo-exception-handler</artifactId>
	<version>1.0.0-SNAPSHOT</version>
	<packaging>jar</packaging>

	<name>spring-boot-demo-exception-handler</name>
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
			<artifactId>spring-boot-starter-thymeleaf</artifactId>
		</dependency>

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
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<optional>true</optional>
		</dependency>
	</dependencies>

	<build>
		<finalName>spring-boot-demo-exception-handler</finalName>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>

</project>
```

## ApiResponse.java

> unified API format returns the encapsulation, which involves the two classes of 'BaseException' and 'Status', see demo for specific code.

```java
/**
 * <p>
 * Generic API interface encapsulation
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-10-02 20:57
 */
@Data
public class ApiResponse {
	/**
	 * Status code
	 */
	private Integer code;

	/**
	 * Back to content
	 */
	private String message;

	/**
	 * Returns data
	 */
	private Object data;

	/**
	 * No parameter constructor
	 */
	private ApiResponse() {

	}

	/**
	 * Full parameter constructor
	 *
	 * @param code status code
	 * @param message returns content
	 * @param data returns data
	 */
	private ApiResponse(Integer code, String message, Object data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}

	/**
	 * Construct a custom API to return
	 *
	 * @param code status code
	 * @param message returns content
	 * @param data returns data
	 * @return ApiResponse
	 */
	public static ApiResponse of(Integer code, String message, Object data) {
		return new ApiResponse(code, message, data);
	}

	/**
	 * Construct a successful API return with data
	 *
	 * @param data returns data
	 * @return ApiResponse
	 */
	public static ApiResponse ofSuccess(Object data) {
		return ofStatus(Status.OK, data);
	}

	/**
	 * Construct an API return for a successful and custom message
	 *
	 * @param message returns content
	 * @return ApiResponse
	 */
	public static ApiResponse ofMessage(String message) {
		return of(Status.OK.getCode(), message, null);
	}

	/**
	 * Construct a stateful API to return
	 *
	 * @param status status {@link Status}
	 * @return ApiResponse
	 */
	public static ApiResponse ofStatus(Status status) {
		return ofStatus(status, null);
	}

	/**
	 * Construct a stateful API return with data
	 *
	 * @param status status {@link Status}
	 * @param data returns data
	 * @return ApiResponse
	 */
	public static ApiResponse ofStatus(Status status, Object data) {
		return of(status.getCode(), status.getMessage(), data);
	}

	/**
	 * Construct an exception with data returned by the API
	 *
	 * @param t exception
	 * @param data returns data
	 * @param <T> a subclass of {@link BaseException}
	 * @return ApiResponse
	 */
	public static <T extends BaseException> ApiResponse ofException(T t, Object data) {
		return of(t.getCode(), t.getMessage(), data);
	}

	/**
	 * Construct an exception with data returned by the API
	 *
	 * @param t exception
	 * @param <T> a subclass of {@link BaseException}
	 * @return ApiResponse
	 */
	public static <T extends BaseException> ApiResponse ofException(T t) {
		return ofException(t, null);
	}
}
```

## DemoExceptionHandler.java

```java
/**
 * <p>
 * Unified exception handling
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-10-02 21:26
 */
@ControllerAdvice
@Slf4j
public class DemoExceptionHandler {
	private static final String DEFAULT_ERROR_VIEW = "error";

	/**
	 * Unified json exception handling
	 *
	 * @param exception JsonException
	 * @return Returns json format uniformly
	 */
	@ExceptionHandler(value = JsonException.class)
	@ResponseBody
	public ApiResponse jsonErrorHandler(JsonException exception) {
		log.error("【JsonException】:{}", exception.getMessage());
		return ApiResponse.ofException(exception);
	}

	/**
	 * Unified page exception handling
	 *
	 * @param exception PageException
	 * @return Unified jump to exception page
	 */
	@ExceptionHandler(value = PageException.class)
	public ModelAndView pageErrorHandler(PageException exception) {
		log.error("【DemoPageException】:{}", exception.getMessage());
		ModelAndView view = new ModelAndView();
		view.addObject("message", exception.getMessage());
		view.setViewName(DEFAULT_ERROR_VIEW);
		return view;
	}
}
```

## error.html

> is located in the 'src/main/resources/template' directory

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head lang="en">
	<meta charset="UTF-8"/>
	<title>Unified page exception handling</title>
</head>
<body>
<h1>Unified page exception handling</h1>
<div th:text="${message}"></div>
</body>
</html>
```

