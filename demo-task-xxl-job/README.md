# spring-boot-demo-task-xxl-job

> This demo mainly demonstrates how Spring Boot integrates XXL-JOB to implement distributed scheduled tasks, and provides a way to bypass the management of scheduled tasks by 'xxl-job-admin', including scheduled task list, trigger list, new scheduled task, delete scheduled task, stop scheduled task, start scheduled task, modify scheduled task, manually trigger scheduled task.

## 1. xxl-job-admin dispatch center

> https://github.com/xuxueli/xxl-job.git

Clone the dispatch center code

```bash
$ git clone https://github.com/xuxueli/xxl-job.git
```

### 1.1. Create a table structure for the dispatch center

Database script address: '/xxl-job/doc/db/tables_xxl_job.sql'

### 1.2. Modify application.properties

```properties
server.port=18080

spring.datasource.url=jdbc:mysql://127.0.0.1:3306/xxl_job? Unicode=true&characterEncoding=UTF-8&useSSL=false
spring.datasource.username=root
spring.datasource.password=root
```

### 1.3. Modify the log configuration file logback.xml

```xml
<property name="log.path" value="logs/xxl-job/xxl-job-admin.log"/>
```

### 1.4. Start the dispatch center

Run `XxlJobAdminApplication`

Default username password: admin/admin

! [image-20190808105554414] (https://static.xkcoding.com/spring-boot-demo/2019-08-08-025555.png)

! [image-20190808105628852] (https://static.xkcoding.com/spring-boot-demo/2019-08-08-025629.png)

## 2. Write an executor project

### 2.1. pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <artifactId>spring-boot-demo-task-xxl-job</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <packaging>jar</packaging>

  <name>spring-boot-demo-task-xxl-job</name>
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
    <xxl-job.version>2.1.0</xxl-job.version>
  </properties>

  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-configuration-processor</artifactId>
      <optional>true</optional>
    </dependency>

    <!-- xxl-job-core -->
    <dependency>
      <groupId>com.xuxueli</groupId>
      <artifactId>xxl-job-core</artifactId>
      <version>${xxl-job.version}</version>
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
    <finalName>spring-boot-demo-task-xxl-job</finalName>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
      </plugin>
    </plugins>
  </build>

</project>
```

### 2.2. Write the configuration class XxlJobProps.java

```java
/**
 * <p>
 * xxl-job configuration
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-08-07 10:25
 */
@Data
@ConfigurationProperties(prefix = "xxl.job")
public class XxlJobProps {
    /**
     * Dispatch center configuration
     */
    private XxlJobAdminProps admin;

    /**
     * Actuator configuration
     */
    private XxlJobExecutorProps executor;

    /**
     * Access token for interacting with the dispatch center
     */
    private String accessToken;

    @Data
    public static class XxlJobAdminProps {
        /**
         * Dispatch center address
         */
        private String address;
    }

    @Data
    public static class XxlJobExecutorProps {
        /**
         * Actuator name
         */
        private String appName;

        /**
         * Actuator IP
         */
        private String ip;

        /**
         * Actuator port
         */
        private int port;

        /**
         * Actuator logs
         */
        private String logPath;

        /**
         * Number of days executor log retention, -1
         */
        private int logRetentionDays;
    }
}
```

### 2.3. Write the configuration file application.yml

```yaml
server:
  port: 8080
  servlet:
    context-path: /demo
xxl:
  job:
    # Executor communication TOKEN [optional]: Enabled when not empty;
    access-token:
    admin:
      # Dispatch center deployment and address [optional]: If there are multiple addresses in the dispatch center cluster deployment, separate them with commas. The executor will use this address for "Actuator Heartbeat Registration" and "Task Result Callback"; Empty to turn off auto-registration;
      address: http://localhost:18080/xxl-job-admin
    executor:
      # Actuator AppName [optional]: Actuator heartbeat registration group by; Empty turns off auto-registration
      app-name: spring-boot-demo-task-xxl-job-executor
      # Actuator IP [optional]: The default is empty to indicate that the IP is automatically obtained, and the specified IP can be manually set when multiple network cards, and the IP will not be bound to Host only as a communication utility; Address information is used for "executor registration" and "dispatch center requests and triggers tasks";
      ip:
      # Actuator port number [optional]: less than or equal to 0 will be obtained automatically; The default port is 9999, when deploying multiple actuators on a single machine, pay attention to configuring different actuator ports;
      port: 9999
      # Executor running log file storage disk path [optional]: You need to have read and write permissions to this path; Null uses the default path;
      log-path: logs/spring-boot-demo-task-xxl-job/task-log
      # The number of days that the executor log is saved [optional]: It takes effect when the value is greater than 3, and the function of periodic cleaning of the executor log file is enabled, otherwise it will not take effect;
      log-retention-days: -1
```

### 2.4. Write the autoassembly class XxlConfig.java

```java
/**
 * <p>
 * xxl-job automatic assembly
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-08-07 10:20
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(XxlJobProps.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class XxlJobConfig {
    private final XxlJobProps xxlJobProps;

    @Bean(initMethod = "start", destroyMethod = "destroy")
    public XxlJobSpringExecutor xxlJobExecutor() {
        log.info(">>>>>>>>>>> xxl-job config init.");
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(xxlJobProps.getAdmin().getAddress());
        xxlJobSpringExecutor.setAccessToken(xxlJobProps.getAccessToken());
        xxlJobSpringExecutor.setAppName(xxlJobProps.getExecutor().getAppName());
        xxlJobSpringExecutor.setIp(xxlJobProps.getExecutor().getIp());
        xxlJobSpringExecutor.setPort(xxlJobProps.getExecutor().getPort());
        xxlJobSpringExecutor.setLogPath(xxlJobProps.getExecutor().getLogPath());
        xxlJobSpringExecutor.setLogRetentionDays(xxlJobProps.getExecutor().getLogRetentionDays());

        return xxlJobSpringExecutor;
    }

}
```

### 2.5. Write specific timing logic .java DemoTask

```java
/**
 * <p>
 * Test scheduled tasks
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-08-07 10:15
 */
@Slf4j
@Component
@JobHandler("demoTask")
public class DemoTask extends IJobHandler {

    /**
     * execute handler, invoked when executor receives a scheduling request
     *
     * @param param timing task parameters
     * @return Execution status
     * @throws Exception task exception
     */
    @Override
    public ReturnT<String> execute(String param) throws Exception {
        You can dynamically get the parameters passed over, depending on the parameters, the currently scheduled task is different
        log.info("【param】= {}", param);
        XxlJobLogger.log("demo task run at : {}", DateUtil.now());
        return RandomUtil.randomInt(1, 11) % 2 == 0 ? SUCCESS : FAIL;
    }
}
```

### 2.6. Start the executor

Run `SpringBootDemoTaskXxlJobApplication`

## 3. Configure scheduled tasks

### 3.1. Add the started executor to the dispatch center

Actuator Management - Added executors

! [image-20190808105910203] (https://static.xkcoding.com/spring-boot-demo/2019-08-08-025910.png)

### 3.2. Add a scheduled task

Task Management - New - Save

! [image-20190808110127956] (https://static.xkcoding.com/spring-boot-demo/2019-08-08-030128.png)

### 3.3. Start and stop scheduled tasks

The Actions column of the task list, which has the following actions: Execute, Start/Stop, Log, Edit, Delete

Execution: Triggers the task once without affecting the timing logic

Start: Starts the scheduled task

Stop: Stops the scheduled task

Log: View the current task execution log

Edit: Updates the scheduled task

Delete: Deletes the scheduled task

## 4. Use the API to add scheduled tasks

> the actual scenario, if you add a scheduled task needs to manually operate in xxl-job-admin, this may be more troublesome, users prefer to add timing task parameters, timing scheduling expressions on their own pages, and then add timing tasks through APIs

### 4.1. Transform xxl-job-admin

#### 4.1.1. Modify the JobGroupController .java

```java
...
Add a list of executors
@RequestMapping("/list")
@ResponseBody
Remove the permission check
@PermissionLimit(limit = false)
public ReturnT<List<XxlJobGroup>> list(){
		return  new ReturnT<>(xxlJobGroupDao.findAll());
}
...
```

#### 4.1.2. Modify the JobInfoController .java

```java
Add annotations to pageList, add, update, remove, pause, start, and triggerJob methods to remove the permission check
@PermissionLimit(limit = false)
```

### 4.2. Retrofit actuator projects

#### 4.2.1. Add a manual trigger class

```java
/**
 * <p>
 * Manual operation xxl-job
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-08-07 14:58
 */
@Slf4j
@RestController
@RequestMapping("/xxl-job")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ManualOperateController {
    private final static String baseUri = "http://127.0.0.1:18080/xxl-job-admin";
    private final static String JOB_INFO_URI = "/jobinfo";
    private final static String JOB_GROUP_URI = "/jobgroup";

    /**
     * Task group list, xxl-job is called trigger list
     */
    @GetMapping("/group")
    public String xxlJobGroup() {
        HttpResponse execute = HttpUtil.createGet(baseUri + JOB_GROUP_URI + "/list").execute();
        log.info("【execute】= {}", execute);
        return execute.body();
    }

    /**
     * Pagination task list
     *
     * @param page current page, first page -> 0
     * @param size per page, default 10
     * @return Pagination task list
     */
    @GetMapping("/list")
    public String xxlJobList(Integer page, Integer size) {
        Map<String, Object> jobInfo = Maps.newHashMap();
        jobInfo.put("start", page != null ? page : 0);
        jobInfo.put("length", size != null ? size : 10);
        jobInfo.put("jobGroup", 2);
        jobInfo.put("triggerStatus", -1);

        HttpResponse execute = HttpUtil.createGet(baseUri + JOB_INFO_URI + "/pageList").form(jobInfo).execute();
        log.info("【execute】= {}", execute);
        return execute.body();
    }

    /**
     * Test saving tasks manually
     */
    @GetMapping("/add")
    public String xxlJobAdd() {
        Map<String, Object> jobInfo = Maps.newHashMap();
        jobInfo.put("jobGroup", 2);
        jobInfo.put("jobCron", "0 0/1 * * * ? *");
        jobInfo.put("jobDesc", "manually added tasks");
        jobInfo.put("author", "admin");
        jobInfo.put("executorRouteStrategy", "ROUND");
        jobInfo.put("executorHandler", "demoTask");
        jobInfo.put("executorParam", "Manually added parameters for tasks");
        jobInfo.put("executorBlockStrategy", ExecutorBlockStrategyEnum.SERIAL_EXECUTION);
        jobInfo.put("glueType", GlueTypeEnum.BEAN);

        HttpResponse execute = HttpUtil.createGet(baseUri + JOB_INFO_URI + "/add").form(jobInfo).execute();
        log.info("【execute】= {}", execute);
        return execute.body();
    }

    /**
     * Test manually triggering a task
     */
    @GetMapping("/trigger")
    public String xxlJobTrigger() {
        Map<String, Object> jobInfo = Maps.newHashMap();
        jobInfo.put("id", 4);
        jobInfo.put("executorParam", JSONUtil.toJsonStr(jobInfo));

        HttpResponse execute = HttpUtil.createGet(baseUri + JOB_INFO_URI + "/trigger").form(jobInfo).execute();
        log.info("【execute】= {}", execute);
        return execute.body();
    }

    /**
     * Test manual deletion of tasks
     */
    @GetMapping("/remove")
    public String xxlJobRemove() {
        Map<String, Object> jobInfo = Maps.newHashMap();
        jobInfo.put("id", 4);

        HttpResponse execute = HttpUtil.createGet(baseUri + JOB_INFO_URI + "/remove").form(jobInfo).execute();
        log.info("【execute】= {}", execute);
        return execute.body();
    }

    /**
     * Test manually stop the task
     */
    @GetMapping("/stop")
    public String xxlJobStop() {
        Map<String, Object> jobInfo = Maps.newHashMap();
        jobInfo.put("id", 4);

        HttpResponse execute = HttpUtil.createGet(baseUri + JOB_INFO_URI + "/stop").form(jobInfo).execute();
        log.info("【execute】= {}", execute);
        return execute.body();
    }

    /**
     * Test manual start task
     */
    @GetMapping("/start")
    public String xxlJobStart() {
        Map<String, Object> jobInfo = Maps.newHashMap();
        jobInfo.put("id", 4);

        HttpResponse execute = HttpUtil.createGet(baseUri + JOB_INFO_URI + "/start").form(jobInfo).execute();
        log.info("【execute】= {}", execute);
        return execute.body();
    }

}
```

> the rest of the code of the backend, please clone this project, to see the specific code

## Reference

- [Distributed Task Scheduling Platform xxl-job] (http://www.xuxueli.com/xxl-job/#/)

