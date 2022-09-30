# spring-boot-demo-flyway

> This demo demonstrates how Spring Boot can use Flyway to initialize the project database and support versioning of database scripts.

## 1. Add dependencies

- Flyway dependency

```xml
<!-- add flyway dependencies -->
<dependency>
  <groupId>org.flywaydb</groupId>
  <artifactId>flyway-core</artifactId>
</dependency>
```

- Initialize the table structure, which requires manipulation of the database, so the introduction of database drivers and data source dependencies (in this case, spring-boot-starter-data-jdbc)

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-jdbc</artifactId>
</dependency>

<dependency>
  <groupId>mysql</groupId>
  <artifactId>mysql-connector-java</artifactId>
  <scope>runtime</scope>
</dependency>
```

## 2. Flyway knowledge supplement

1. Flyway will read 'classpath:db/migration' by default, you can specify a custom path through 'spring.flyway.locations', multiple paths are separated by commas in half width, internal resources use 'classpath:', external resources use 'file:'

2. If there is no database file at the beginning of the project, but Flyway is referenced, then when the project starts, Flyway will check whether there is a SQL file, at which point you need to turn this check off, 'spring.flyway.check-location = false'

3. Flyway creates a table called 'flyway_schema_history' when the project first starts, where it records the history of database script execution, of course, you can modify this value through 'spring.flyway.table'

4. SQL scripts executed by Flyway must follow a naming convention, 'V__.sql' is first 'V' and then the version number, if the version number has multiple numbers, separated by '_', such as '1_<VERSION><NAME>0', '1_1', the version number is followed by 2 underscores, and finally the name of the SQL script.

   ** It should be noted here that the beginning of V will only be executed once, the next time the project starts will not be executed, nor can the original file be modified, otherwise the project startup will report an error, if you need to make changes to the script starting with V, you need to empty the 'flyway_schema_history' table, if there is a SQL script that needs to be executed every time it starts, then change V to start with 'R' **

5. Flyway will empty the original library by default and re-execute the SQL script, which is not advisable in production, so you need to turn this configuration off, 'spring.flyway.clean-disabled = true'

## 3. application.yml configuration

> Paste my application.yml configuration

```yaml
spring:
  flyway:
    enabled: true
    # Check if there is a problem with the SQL file before migration
    validate-on-migrate: true
    # The production environment must be shut down
    clean-disabled: true
    # Verify that a SQL file exists under the path
    check-location: false
    # When the table structure already exists at the beginning and no flyway_schema_history table exists, it needs to be set to true
    baseline-on-migrate: true
    # Base version 0
    baseline-version: 0
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/flyway-test?useSSL=false
    username: root
    password: root
    type: com.zaxxer.hikari.HikariDataSource
```

## 4. Test

### 4.1. Test version 1.0 of the SQL script

Create 'V1_0__INIT.sql' 

```mysql
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  'id' int(11) NOT NULL AUTO_INCREMENT COMMENT 'primary key',
  'username' varchar(32) NOT NULL COMMENT 'username',
  'password' varchar(32) NOT NULL COMMENT ''Password after encryption',
  'salt' varchar(32) NOT NULL COMMENT 'salt used in encryption',
  'email' varchar(32) NOT NULL COMMENT 'mailbox',
  'phone_number' varchar(15) NOT NULL COMMENT 'Mobile phone number',
  'status' int(2) NOT NULL DEFAULT '1' COMMENT 'status,-1:tombstone,0:disabled,1:enabled',
  'create_time' datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation Time',
  'last_login_time' datetime DEFAULT NULL COMMENT 'Last login time',
  'last_update_time' datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Last updated',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `phone_number` (`phone_number`)
ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='1.0-user table';
```

Start the project and you can see the log output:

```java
2020-03-05 10:48:37.799  INFO 3351 --- [           main] o.f.c.internal.license.VersionPrinter    : Flyway Community Edition 5.2.1 by Boxfuse
2020-03-05 10:48:37.802  INFO 3351 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2020-03-05 10:48:37.971  INFO 3351 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2020-03-05 10:48:37.974  INFO 3351 --- [           main] o.f.c.internal.database.DatabaseFactory  : Database: jdbc:mysql://127.0.0.1:3306/flyway-test (MySQL 5.7)
2020-03-05 10:48:38.039  INFO 3351 --- [           main] o.f.core.internal.command.DbValidate     : Successfully validated 1 migration (execution time 00:00.015s)
2020-03-05 10:48:38.083  INFO 3351 --- [           main] o.f.c.i.s.JdbcTableSchemaHistory         : Creating Schema History table: `flyway-test`.`flyway_schema_history`
2020-03-05 10:48:38.143  INFO 3351 --- [           main] o.f.core.internal.command.DbMigrate      : Current version of schema `flyway-test`: << Empty Schema >>
2020-03-05 10:48:38.144  INFO 3351 --- [           main] o.f.core.internal.command.DbMigrate      : Migrating schema `flyway-test` to version 1.0 - INIT
2020-03-05 10:48:38.156  WARN 3351 --- [           main] o.f.c.i.s.DefaultSqlScriptExecutor       : DB: Unknown table 'flyway-test.t_user' (SQL State: 42S02 - Error Code: 1051)
2020-03-05 10:48:38.183  INFO 3351 --- [           main] o.f.core.internal.command.DbMigrate      : Successfully applied 1 migration to schema `flyway-test` (execution time 00:00.100s)
```

Checking the database found that 2 tables were created, one was the history table that Flyway depended on, and the other was our 't_user' table

<img src="http://static.xkcoding.com/spring-boot-demo/flyway/062903.jpg" alt="image-20200305105632047" style="zoom:50%;" />

See the 'flyway-schema-history' table below

<img src="http://static.xkcoding.com/spring-boot-demo/flyway/062901.jpg" alt="image-20200305110147176" style="zoom:50%;" />

### 4.2. Test version 1.1 of the SQL script

Create 'V1_1__ALTER.sql' 

```mysql
ALTER TABLE t_user COMMENT = 'user v1.1';
```

Start the project and you can see the log output:

```java
2020-03-05 10:59:02.279  INFO 3536 --- [           main] o.f.c.internal.license.VersionPrinter    : Flyway Community Edition 5.2.1 by Boxfuse
2020-03-05 10:59:02.282  INFO 3536 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2020-03-05 10:59:02.442  INFO 3536 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2020-03-05 10:59:02.445  INFO 3536 --- [           main] o.f.c.internal.database.DatabaseFactory  : Database: jdbc:mysql://127.0.0.1:3306/flyway-test (MySQL 5.7)
2020-03-05 10:59:02.530  INFO 3536 --- [           main] o.f.core.internal.command.DbValidate     : Successfully validated 2 migrations (execution time 00:00.018s)
2020-03-05 10:59:02.538  INFO 3536 --- [           main] o.f.core.internal.command.DbMigrate      : Current version of schema `flyway-test`: 1.0
2020-03-05 10:59:02.538  INFO 3536 --- [           main] o.f.core.internal.command.DbMigrate      : Migrating schema `flyway-test` to version 1.1 - ALTER
2020-03-05 10:59:02.564  INFO 3536 --- [           main] o.f.core.internal.command.DbMigrate      : Successfully applied 1 migration to schema `flyway-test` (execution time 00:00.029s)
```

Check the database and see that the comments for the 't_user' table have been updated

<img src="http://static.xkcoding.com/spring-boot-demo/flyway/062851.jpg" alt="image-20200305105958181" style="zoom:50%;" />

See the 'flyway-schema-history' table below

<img src="http://static.xkcoding.com/spring-boot-demo/flyway/062908.jpg" alt="image-20200305110057768" style="zoom:50%;" />

## Reference

1. [Spring Boot Official Documentation - Migration Section] (https://docs.spring.io/spring-boot/docs/2.1.0.RELEASE/reference/htmlsingle/#howto-execute-flyway-database-migrations-on-startup)
2. [Official Flyway Documentation] (https://flywaydb.org/documentation/)
