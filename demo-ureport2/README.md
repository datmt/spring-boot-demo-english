# spring-boot-demo-ureport2

> This demo demonstrates how the Spring Boot project can quickly integrate ureport2 to implement arbitrarily complex Chinese-style reporting functions.

UReport2 is a high-performance reporting engine based on pure Java based on the architecture of Spring, and arbitrarily complex Chinese-style reports can be implemented by iterating on cells. In UReport2, a new web-based report designer is available that can run in a variety of major browsers such as Chrome, Firefox, Edge, and more (except IE). With UReport2, you can open a browser to design and produce a variety of complex reports.

## 1. Primary code

Because the official does not provide a starter package, you need to integrate yourself, here use [pig] (https://github.com/pig-mesh/pig) author [cold and cold classmate] (https://github.com/lltx) developed the starter lazy implementation, this starter not only supports the configuration of a stand-alone environment, but also supports the cluster environment.

### 1.1. Stand-alone use

#### 1.1.1. 'pom.xml' new dependencies

```xml
<dependency>
    <groupId>com.pig4cloud.plugin</groupId>
    <artifactId>ureport-spring-boot-starter</artifactId>
    <version>0.0.1</version>
</dependency>
```

#### 1.1.2. 'application.yml' modifies the configuration file

```yaml
server:
  port: 8080
  servlet:
    context-path: /demo
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/spring-boot-demo?useUnicode=true&characterEncoding=UTF-8&useSSL=false&autoReconnect=true&failOverReadOnly=false&serverTimezone=GMT%2B8
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
ureport:
  debug: false
  disableFileProvider: false
  disableHttpSessionReportCache: true
  # In stand-alone mode, the local path needs to be created in advance
  fileStoreDir: '/Users/yk.shen/Desktop/ureport2'
```
#### 1.1.3. Add an internal data source

```java
@Component
public class InnerDatasource implements BuildinDatasource {
    @Autowired
    private DataSource datasource;

    @Override
    public String name() {
        return "Internal data source";
    }

    @SneakyThrows
    @Override
    public Connection getConnection() {
        return datasource.getConnection();
    }
}
```

#### 1.1.4. Initialize the data using 'doc/sql/t_user_ureport2.sql'

```mysql
DROP TABLE IF EXISTS `t_user_ureport2`;
CREATE TABLE `t_user_ureport2` (
  'id' bigint(13) unsigned NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  'name' varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT 'name',
  'create_time' timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Creation Time',
  'status' tinyint(4) NOT NULL COMMENT 'is disabled',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

BEGIN;
INSERT INTO 't_user_ureport2' VALUES (1, 'Tester 1', '2020-10-22 09:01:58', 1);
INSERT INTO 't_user_ureport2' VALUES (2, 'Tester 2', '2020-10-22 09:02:00', 0);
INSERT INTO 't_user_ureport2' VALUES (3, 'Tester 3', '2020-10-23 03:02:00', 1);
INSERT INTO 't_user_ureport2' VALUES (4, 'Tester 4', '2020-10-23 23:02:00', 1);
INSERT INTO 't_user_ureport2' VALUES (5, 'Tester 5', '2020-10-23 23:02:00', 1);
INSERT INTO 't_user_ureport2' VALUES (6, 'Tester 6', '2020-10-24 11:02:00', 0);
INSERT INTO 't_user_ureport2' VALUES (7, 'Tester 7', '2020-10-24 20:02:00', 0);
INSERT INTO 't_user_ureport2' VALUES (8, 'Tester 8', '2020-10-25 08:02:00', 1);
INSERT INTO 't_user_ureport2' VALUES (9, 'Tester 9', '2020-10-25 09:02:00', 1);
INSERT INTO 't_user_ureport2' VALUES (10, 'Tester 10', '2020-10-25 13:02:00', 1);
INSERT INTO 't_user_ureport2' VALUES (11, 'Tester 11', '2020-10-26 21:02:00', 0);
INSERT INTO 't_user_ureport2' VALUES (12, 'Tester 12', '2020-10-26 23:02:00', 1);
INSERT INTO 't_user_ureport2' VALUES (13, 'Tester 13', '2020-10-26 23:02:00', 1);
COMMIT;
```

#### 1.1.5. Access Report Designer

http://127.0.0.1:8080/demo/ureport/designer

! [Report Design Page] (http://static.xkcoding.com/spring-boot-demo/ureport2/035330.png)

#### 1.1.6. Start designing

##### 1.1.6.1. Select a data source

Here you need to use the internal data source created in step 1.1.3 above, as shown in the figure

! [Select Data Source] (http://static.xkcoding.com/spring-boot-demo/ureport2/040032.png)

Select a data source

! [Select Data Source] (http://static.xkcoding.com/spring-boot-demo/ureport2/040117.png)

The data source appears in the list

! [Data Source List] (http://static.xkcoding.com/spring-boot-demo/ureport2/040237.png)

##### 1.1.6.2. Select the dataset

Right-click the data source you just selected and select Add Dataset

! [Right-click the selected data source] (http://static.xkcoding.com/spring-boot-demo/ureport2/063315.png)

Here select the user table initialized in step 1.1.4 above

! [Create User Report] (http://static.xkcoding.com/spring-boot-demo/ureport2/063845.png)

Preview the data for a look

! [Preview Dataset Data] (http://static.xkcoding.com/spring-boot-demo/ureport2/063955.png)

Click OK to save the dataset

! [Save Dataset] (http://static.xkcoding.com/spring-boot-demo/ureport2/064049.png)

##### 1.1.6.3. Report design

The location where the report header was created

! [Merge cells] (http://static.xkcoding.com/spring-boot-demo/ureport2/064425.png)

Header contents

! [image-20201124144752390] (http://static.xkcoding.com/spring-boot-demo/ureport2/064752.png)

After the operation is completed, grow like this~

! [Table head beautification] (http://static.xkcoding.com/spring-boot-demo/ureport2/064916.png)



Then set the header row of the data, just like the header setting, the effect is as follows

! [Header row of data] (http://static.xkcoding.com/spring-boot-demo/ureport2/065125.png)

Next set up the data

! [id field configuration] (http://static.xkcoding.com/spring-boot-demo/ureport2/065658.png)

The same is true for the other fields, which are as follows when completed

! [Data Configuration] (http://static.xkcoding.com/spring-boot-demo/ureport2/070440.png)

Now you can try previewing the data

! [Preview Data] (http://static.xkcoding.com/spring-boot-demo/ureport2/070634.png)

! [Preview Data] (http://static.xkcoding.com/spring-boot-demo/ureport2/070813.png)

Turn it off and beautify it a little

! [Preview data after beautification] (http://static.xkcoding.com/spring-boot-demo/ureport2/070910.png)

At this point, although the data is displayed normally, can the "Is it available" column display 0/1 support customization?

! [Map Dataset] (http://static.xkcoding.com/spring-boot-demo/ureport2/071352.png)

Preview it again

! [Dictionary Mapping Preview Data] (http://static.xkcoding.com/spring-boot-demo/ureport2/071428.png)

Incidentally, the data format of the creation time is also changed

! [Time Format Modification] (http://static.xkcoding.com/spring-boot-demo/ureport2/072725.png)

After modification, the preview data is as follows

! [Preview Data] (http://static.xkcoding.com/spring-boot-demo/ureport2/072753.png)

##### 1.1.6.4. Save the report design file

! [image-20201124153244035] (http://static.xkcoding.com/spring-boot-demo/ureport2/073244.png)

! [Save] (http://static.xkcoding.com/spring-boot-demo/ureport2/074228.png)

After clicking Save, a 'demo.ureport' file will appear at the address you configured locally in .xml the 'application.yml' file

Next time, you can preview the report directly from the address of http://localhost:8080/demo/ureport/preview?_u=file:demo.ureport.xml

##### 1.1.6.5. Add report query criteria

Remember when we added a dataset above, the conditions were added? Use it now

! [Query Form Designer] (http://static.xkcoding.com/spring-boot-demo/ureport2/074641.png)

Query form design

! [Drag Element Design Form Query] (http://static.xkcoding.com/spring-boot-demo/ureport2/074936.png)

Configure query parameters

! [Improve the inquiry form] (http://static.xkcoding.com/spring-boot-demo/ureport2/075248.png)

Beautify the buttons

! [Button style beautification] (http://static.xkcoding.com/spring-boot-demo/ureport2/075410.png)

In the preview ~

! [Preview Data - Query Criteria] (http://static.xkcoding.com/spring-boot-demo/ureport2/075640.png)

### 1.2. Cluster use

The template designed above is saved natively in the service and requires unified file system storage in a clustered environment.

#### 1.2.1. Added dependencies

```xml
<dependency>
  <groupId>com.pig4cloud.plugin</groupId>
  <artifactId>oss-spring-boot-starter</artifactId>
  <version>0.0.3</version>
</dependency>
```

#### 1.2.2. Only the cloud storage related parameters need to be configured, and the demonstration is minio

```yaml
oss:
  access-key: lengleng
  secret-key: lengleng
  bucket-name: lengleng
  endpoint: http://minio.pig4cloud.com
```

> Note: The public minio provided by the cold is used here, please do not mess with it, nor do you guarantee the reliability of the data, it is recommended that the small partner build a minio, or use Alibaba Cloud oss

## 2. pit

The latest version of Ureport2 is '2.2.9', which has not been updated for a long time, and there is a pit: when opening an existing report design file on the report design page, there may be a situation where it cannot be previewed, refer to ISSUE: https://github.com/youseries/ureport/issues/393

! [Unable to preview] (http://static.xkcoding.com/spring-boot-demo/ureport2/084852.png)

Workaround:

! [image-20201124164953947] (http://static.xkcoding.com/spring-boot-demo/ureport2/084954.png)

The conditional expression becomes 'undefined', and it is important to note here that our xml file is normal, except that there was an error in the ureport parsing.

! [Conditional Expressions] (http://static.xkcoding.com/spring-boot-demo/ureport2/085114.png)

Click Edit and reselect the expression to solve the problem

! [image-20201124165202295] (http://static.xkcoding.com/spring-boot-demo/ureport2/085202.png)

Try the preview again

! [Zebra Preview Data] (http://static.xkcoding.com/spring-boot-demo/ureport2/085228.png)

> Note: This possibility appears in the case of the use of conditional attributes in the report design file, the repair method is to open the file, reconfigure the condition properties, here is the pit, the small partner to use when paying attention to the good, the best way is to avoid the use of conditional properties.

## 3. thank

Thanks again to [@cold cold] (https://github.com/lltx) for providing the starter and PR, due to personal operational errors, PR has not been merged, sorry~

## 4. reference

- [ureport2 Using Documentation] (https://www.w3cschool.cn/ureport)
- [ureport-spring-boot-starter] (https://github.com/pig-mesh/ureport-spring-boot-starter) Spring boot encapsulation for UReport2
- [oss-spring-boot-starter] (https://github.com/pig-mesh/oss-spring-boot-starter) Distributed file storage system compatible with all S3 protocols

