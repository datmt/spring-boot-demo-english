# spring-boot-demo-template-enjoy

> This demo demonstrates how the Spring Boot project integrates with the enjoy template engine.

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<artifactId>spring-boot-demo-template-enjoy</artifactId>
	<version>1.0.0-SNAPSHOT</version>
	<packaging>jar</packaging>

	<name>spring-boot-demo-template-enjoy</name>
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
		<enjoy.version>3.5</enjoy.version>
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
			<groupId>com.jfinal</groupId>
			<artifactId>enjoy</artifactId>
			<version>${enjoy.version}</version>
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
	</dependencies>

	<build>
		<finalName>spring-boot-demo-template-enjoy</finalName>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>

</project>
```

## EnjoyConfig.java

```java
/**
 * <p>
 * Enjoy template configuration class
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-10-11 14:06
 */
@Configuration
public class EnjoyConfig {
   @Bean(name = "jfinalViewResolver")
   public JFinalViewResolver getJFinalViewResolver() {
      JFinalViewResolver jfr = new JFinalViewResolver();
      The setDevMode configuration is placed first
      jfr.setDevMode(true);
      Use ClassPathSourceFactory to load template files from class path and jar packages
      jfr.setSourceFactory(new ClassPathSourceFactory());
      Use setBaseTemplatePath when using ClassPathSourceFactory
      instead of jfr.setPrefix("/view/")
      JFinalViewResolver.engine.setBaseTemplatePath("/templates/");

      jfr.setSessionInView(true);
      jfr.setSuffix(".html");
      jfr.setContentType("text/html; charset=UTF-8");
      jfr.setOrder(0);
      return jfr;
   }
}
```

## IndexController.java

```java
/**
 * <p>
 * Homepage
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-10-11 14:22
 */
@Controller
@Slf4j
public class IndexController {

	@GetMapping(value = {"", "/"})
	public ModelAndView index(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();

		User user = (User) request.getSession().getAttribute("user");
		if (ObjectUtil.isNull(user)) {
			mv.setViewName("redirect:/user/login");
		} else {
			mv.setViewName("page/index");
			mv.addObject(user);
		}

		return mv;
	}
}
```

## UserController.java

```java
/**
 * <p>
 * User page
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-10-11 14:24
 */
@Controller
@RequestMapping("/user")
@Slf4j
public class UserController {
	@PostMapping("/login")
	public ModelAndView login(User user, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();

		mv.addObject(user);
		mv.setViewName("redirect:/");

		request.getSession().setAttribute("user", user);
		return mv;
	}

	@GetMapping("/login")
	public ModelAndView login() {
		return new ModelAndView("page/login");
	}
}
```

## index.html

```jsp
<!doctype html>
<html lang="en">
#include("/common/head.html")
<body>
<div id="app" style="margin: 20px 20%">
	Welcome to log in, #(user.name)!
</div>
</body>
</html>
```

## login.html

```jsp
<!doctype html>
<html lang="en">
#include("/common/head.html")
<body>
<div id="app" style="margin: 20px 20%">
	<form action="/demo/user/login" method="post">
		username<input type="text" name="name" placeholder="username"/>
		password<input type="password" name="password" placeholder="password"/>
		<input type="submit" value="Login" >
	</form>
</div>
</body>
</html>
```

## application.yml

```yaml
server:
  port: 8080
  servlet:
    context-path: /demo
```

## Enjoy the syntax sugar learning documentation

http://www.jfinal.com/doc/6-1



