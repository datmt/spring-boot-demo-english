# spring-boot-demo-email

> This demo demonstrates how Spring Boot integrates mail functionality, including sending simple text messages, HTML messages (including template HTML messages), attachment emails, and static resource messages.

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <artifactId>spring-boot-demo-email</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <packaging>jar</packaging>

  <name>spring-boot-demo-email</name>
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
    <jasypt.version>2.1.1</jasypt.version>
  </properties>

  <dependencies>
    <!-- Spring Boot mail relies on -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-mail</artifactId>
    </dependency>

    <!-- jasypt configuration file encryption and decryption-->
    <dependency>
      <groupId>com.github.ulisesbocchio</groupId>
      <artifactId>jasypt-spring-boot-starter</artifactId>
      <version>${jasypt.version}</version>
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

    <!-- Spring Boot template depends on -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
  </dependencies>

  <build>
    <finalName>spring-boot-demo-email</finalName>
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
spring:
  mail:
    host: smtp.mxhichina.com
    port: 465
    username: spring-boot-demo@xkcoding.com
    # Use jasypt to encrypt password, use com.xkcoding.email.PasswordTest.testGeneratePassword to generate an encrypted password, replace ENC (encrypted password)
    password: ENC(OT0qGOpXrr1Iog1W+fjOiIDCJdBjHyhy)
    protocol: smtp
    test-connection: true
    default-encoding: UTF-8
    properties:
      mail.smtp.auth: true
      mail.smtp.starttls.enable: true
      mail.smtp.starttls.required: true
      mail.smtp.ssl.enable: true
      mail.display.sendmail: spring-boot-demo
# Configure the decryption key for jasypt
jasypt:
  encryptor:
    password: spring-boot-demo

```

## MailService.java

```java
/**
 * <p>
 * Mail interface
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-21 11:16
 */
public interface MailService {
    /**
     * Send text messages
     *
     * @param to recipient address
     * @param subject message subject
     * @param content email content
     * @param cc cc to the address
     */
    void sendSimpleMail(String to, String subject, String content, String... cc);

    /**
     * Send HTML emails
     *
     * @param to recipient address
     * @param subject message subject
     * @param content email content
     * @param cc cc to the address
     * @throws MessagingException message sent exception
     */
    void sendHtmlMail(String to, String subject, String content, String... cc) throws MessagingException;

    /**
     * Send messages with attachments
     *
     * @param to recipient address
     * @param subject message subject
     * @param content email content
     * @param filePath attachment address
     * @param cc cc to the address
     * @throws MessagingException message sent exception
     */
    void sendAttachmentsMail(String to, String subject, String content, String filePath, String... cc) throws MessagingException;

    /**
     * Send messages with static resources in the body
     *
     * @param to recipient address
     * @param subject message subject
     * @param content email content
     * @param rscPath static resource address
     * @param rscId static resource ID
     * @param cc cc to the address
     * @throws MessagingException message sent exception
     */
    void sendResourceMail(String to, String subject, String content, String rscPath, String rscId, String... cc) throws MessagingException;

}
```

## MailServiceImpl.java

```java
/**
 * <p>
 * Mail interface
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-21 13:49
 */
@Service
public class MailServiceImpl implements MailService {
    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String from;

    /**
     * Send text messages
     *
     * @param to recipient address
     * @param subject message subject
     * @param content email content
     * @param cc cc to the address
     */
    @Override
    public void sendSimpleMail(String to, String subject, String content, String... cc) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        if (ArrayUtil.isNotEmpty(cc)) {
            message.setCc(cc);
        }
        mailSender.send(message);
    }

    /**
     * Send HTML emails
     *
     * @param to recipient address
     * @param subject message subject
     * @param content email content
     * @param cc cc to the address
     * @throws MessagingException message sent exception
     */
    @Override
    public void sendHtmlMail(String to, String subject, String content, String... cc) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);
        if (ArrayUtil.isNotEmpty(cc)) {
            helper.setCc(cc);
        }
        mailSender.send(message);
    }

    /**
     * Send messages with attachments
     *
     * @param to recipient address
     * @param subject message subject
     * @param content email content
     * @param filePath attachment address
     * @param cc cc to the address
     * @throws MessagingException message sent exception
     */
    @Override
    public void sendAttachmentsMail(String to, String subject, String content, String filePath, String... cc) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);
        if (ArrayUtil.isNotEmpty(cc)) {
            helper.setCc(cc);
        }
        FileSystemResource file = new FileSystemResource(new File(filePath));
        String fileName = filePath.substring(filePath.lastIndexOf(File.separator));
        helper.addAttachment(fileName, file);

        mailSender.send(message);
    }

    /**
     * Send messages with static resources in the body
     *
     * @param to recipient address
     * @param subject message subject
     * @param content email content
     * @param rscPath static resource address
     * @param rscId static resource ID
     * @param cc cc to the address
     * @throws MessagingException message sent exception
     */
    @Override
    public void sendResourceMail(String to, String subject, String content, String rscPath, String rscId, String... cc) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);
        if (ArrayUtil.isNotEmpty(cc)) {
            helper.setCc(cc);
        }
        FileSystemResource res = new FileSystemResource(new File(rscPath));
        helper.addInline(rscId, res);

        mailSender.send(message);
    }
}
```

## MailServiceTest.java

```java
/**
 * <p>
 * Mail test
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-21 13:49
 */
public class MailServiceTest extends SpringBootDemoEmailApplicationTests {
    @Autowired
    private MailService mailService;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private ApplicationContext context;

    /**
     * Test simple mail
     */
    @Test
    public void sendSimpleMail() {
        mailService.sendSimpleMail ("237497819@qq.com", "This is a simple email", "This is a normal SpringBoot test email");
    }

    /**
     * Test HTML emails
     *
     * @throws MessagingException Email Exception
     */
    @Test
    public void sendHtmlMail() throws MessagingException {
        Context context = new Context();
        context.setVariable("project", "Spring Boot Demo");
        context.setVariable("author", "Yangkai.Shen");
        context.setVariable("url", "https://github.com/xkcoding/spring-boot-demo");

        String emailTemplate = templateEngine.process("welcome", context);
        mailService.sendHtmlMail ("237497819@qq.com", "This is a template HTML email", emailTemplate);
    }

    /**
     * Test HTML mail, custom template directory
     *
     * @throws MessagingException Email Exception
     */
    @Test
    public void sendHtmlMail2() throws MessagingException {

        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext(context);
        templateResolver.setCacheable(false);
        templateResolver.setPrefix("classpath:/email/");
        templateResolver.setSuffix(".html");

        templateEngine.setTemplateResolver(templateResolver);

        Context context = new Context();
        context.setVariable("project", "Spring Boot Demo");
        context.setVariable("author", "Yangkai.Shen");
        context.setVariable("url", "https://github.com/xkcoding/spring-boot-demo");

        String emailTemplate = templateEngine.process("test", context);
        mailService.sendHtmlMail ("237497819@qq.com", "This is a template HTML email", emailTemplate);
    }

    /**
     * Test attachment mail
     *
     * @throws MessagingException Email Exception
     */
    @Test
    public void sendAttachmentsMail() throws MessagingException {
        URL resource = ResourceUtil.getResource("static/xkcoding.png");
        mailService.sendAttachmentsMail("237497819@qq.com", "This is a message with an attachment", "There are attachments in the message, please check it!" , resource.getPath());
    }

    /**
     * Test static resource mail
     *
     * @throws MessagingException Email Exception
     */
    @Test
    public void sendResourceMail() throws MessagingException {
        String rscId = "xkcoding";
        String content = "<html><body>This is a message<br/> with static resources<img src=\'cid:" + rscId + "\' >"</body></html>;
        URL resource = ResourceUtil.getResource("static/xkcoding.png");
        mailService.sendResourceMail("237497819@qq.com", "This is a message with static resources", content, resource.getPath(), rscId);
    }
}
```

## welcome.html

> This file is a mail template located in the resources/templates directory

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>SpringBootDemo (the preferred demo for getting started with SpringBoot</title>).
    <style>
        body {
            text-align: center;
            margin-left: auto;
            margin-right: auto;
        }

        #welcome {
            text-align: center;
        }
    </style>
</head>
<body>
<div id="welcome">
    <h3>Welcome to</span> <span th:text="${project}"> - Powered By <span th:text="${author}"></span></h3>
    <span th:text="${url}"></span>
    <div style="text-align: center; padding: 10px">
        <a style="text-decoration: none;" href="#" th:href="@{${url}}" target="_bank">
            <strong>spring-boot-demo, the premier demo for getting started with Spring Boot! :)</strong>
        </a>
    </div>
    <div style="text-align: center; padding: 4px">
        If it helps you, please feel free to tip
    </div>
    <div style="width: 100%;height: 100%;text-align: center;display: flex">
        <div style="flex: 1;"></div>
        <div style="display: flex;width: 400px;">
            <div style="flex: 1;text-align: center;">
                <div>
                    <img width="180px" height="180px" src="http://xkcoding.com/resources/wechat-reward-image.png">
                </div>
                <div>WeChat tips</div>
            </div>
            <div style="flex: 1;text-align: center;">
                <div><img width="180px" height="180px" src="http://xkcoding.com/resources/alipay-reward-image.png">
                </div>
                <div>Alipay tips</div>
            </div>
        </div>
        <div style="flex: 1;"></div>
    </div>

</div>
</body>
</html>
```

## Reference

- Spring Boott Official Documentation: https://docs.spring.io/spring-boot/docs/2.1.0.RELEASE/reference/htmlsingle/#boot-features-email
- Spring Boott Official Documentation: https://docs.spring.io/spring/docs/5.1.2.RELEASE/spring-framework-reference/integration.html#mail
