<h1 align="center"><a href="https://github.com/xkcoding" target="_blank">Spring Boot Demo</a></h1>
<p align="center">
  <a href="https://travis-ci.com/xkcoding/spring-boot-demo"><img alt="Travis-CI" src="https://travis-ci.com/xkcoding/spring-boot-demo.svg?branch=master"/></a>
  <a href=" https://www.codacy.com/app/xkcoding/spring-boot-demo?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=xkcoding/spring-boot-demo&amp;utm_campaign=Badge_Grade "><img alt="Codacy" src="https://api.codacy.com/project/badge/Grade/1f2e3d437b174bfc943dae1600332ec1"/></a>
  <a href="https://xkcoding.com"><img alt="author" src="https://img.shields.io/badge/author-Yangkai.Shen-blue.svg"/></a>
  <a href="https://www.oracle.com/technetwork/java/javase/downloads/index.html"><img alt="JDK" src="https://img.shields.io/badge/JDK-1.8.0_162-orange.svg"/></a>
  <a href="https://docs.spring.io/spring-boot/docs/2.1.0.RELEASE/reference/html/"><img alt="Spring Boot" src="https://img.shields.io/badge/Spring Boot-2.1.0.RELEASE-brightgreen.svg"/></a>
  <a href="https://github.com/xkcoding/spring-boot-demo/blob/master/LICENSE"><img alt="LICENSE" src="https://img.shields.io/github/license/xkcoding/spring-boot-demo.svg"/></a>
</p>

<p align="center">
  <a href="https://github.com/xkcoding/spring-boot-demo/stargazers"><img alt="star" src="https://img.shields.io/github/stars/xkcoding/spring-boot-demo.svg?label=Stars&style=social"/></a>
  <a href="https://github.com/xkcoding/spring-boot-demo/network/members"><img alt="star" src="https://img.shields.io/github/forks/xkcoding/spring-boot-demo.svg?label=Fork&style=social"/></a>
  <a href="https://github.com/xkcoding/spring-boot-demo/watchers"><img alt="star" src="https://img.shields.io/github/watchers/xkcoding/spring-boot-demo.svg?label=Watch&style=social"/></a>
</p>

<p align="center">
  <span>Chinese | <a href="./README.en.md">English</a></span>
</p>

## Project Introduction

'Spring boot demo' is a project for deep learning and practical 'spring boot', currently containing a total of **'66'** integrated demos, has completed **'55'**.

The project has successfully integrated actuator ('monitoring'), admin ('visual monitoring'), logback ('logging'), aopLog ('logging web request logs via AOP'), unified exception handling ('json level and page level'), freemarker ('template engine'), thymeleaf ('template engine'), Beetl ('template engine'), Enjoy ('template engine'), JdbcTemplate ('Generic JDBC Operations Database'), JPA ('Powerful ORM Framework'), mybatis ('Powerful ORM Framework'), Universal Mapper ('Quick Manipulation Mybatis'), PageHelper ('Generic Mybatis Paging Plugin'), mybatis-plus ('Quick Manipulation Mybatis'), BeetlSQL ('Powerful ORRM Framework'), upload('Local File Upload and Qiniu Cloud File Upload'), redis ('Cache'), ehcache ('Cache'), email ('Send various types of email'), task ('Basic Scheduled Task'), quartz ('Dynamic Management Scheduled Task'), xxl-job ('Distributed Scheduled Task'), swagger ('API interface management test'), security ('RBAC-based dynamic permission authentication') ), SpringSession ('Session Sharing'), Zookeeper ('Combined with AOP for Distributed Locks'), RabbitMQ ('Message Queuing'), Kafka ('Message Queuing'), websocket ('Server Push Monitoring Server Operation Information'), socket.io ('Chat Room'), uReport2 ('Chinese-style Report'), packaged into 'war' files, integration ElasticSearch ('Basic Operations and Advanced Queries'), Async ('Asynchronous Tasks'), Integrated Dubbo ('Adopt Official Starter'), MongoDB ('Document Database'), neo4j ('Graph Database'), docker ('Containerized'), 'JPA Multiple Data Source', 'Mybatis Multidata Source', 'Code Generator', GrayLog ('Log Collection'), JustAuth ('Third Party Login'), LDAP ('Add, Delete, Delete'), 'Dynamically Add/Switch Data Sources', Stand-Alone Throttling ('AOP + Guava RateLimiter'), Distributed Throttling ('AOP + Redis + Lua'), ElasticSearch 7.x ('Using the Official Rest High Level Client''), HTTPS, Flyway ('Database Initialization'), UReport2(' Chinese-style complex reports').

> If you still have a demo that you want to integrate, you can also mention the requirements in [issue](https://github.com/xkcoding/spring-boot-demo/issues/new). I'll add it to the [TODO](./TODO.md) list. ✊

## Branch introduction

- Master branch: Based on Spring Boot version '2.1.0.RELEASE', the parent of each module depends on the pom .xml in the root directory, which is mainly used to manage the common dependent version of each module for everyone to learn.
- v-1.5.x branch: Based on Spring Boot version '1.5.8.RELEASE', each module depends on spring-boot-demo-parent, there are a lot of students who reflect that this method is not very friendly to novices, it is difficult to run, so *** This branch (v-1.5.x) will stop development and maintenance***, all content will slowly synchronize in the form of master branch, this branch is not completed Students who study in this branch can still learn in this branch, but it is recommended to switch to the master branch later, it will be easier, after all, the official has upgraded Spring Boot to version 2.x. 🙂

## Development environment

- **JDK 1.8 +**
- **Maven 3.5 +**
- **IntelliJ IDEA ULTIMATE 2018.2 +** (*Note: Be sure to use IDEA development while ensuring that the 'lombok' plugin is installed*)
- **Mysql 5.7+** (*Try to use version 5.7 or above, as version 5.7 adds some new features and is not backwards compatible.) This demo will try to avoid such incompatibilities, but it is recommended to ensure that version 5.7 or above is still possible*)

## How it works

> Tip: If you are a friend of the fork, please refer to the synchronization code: https://xkcoding.com/2018/09/18/how-to-update-the-fork-project.html

1. `git clone https://github.com/xkcoding/spring-boot-demo.git`
2. Use IDEA to open the project under clone
3. Import the 'pom.xml' file in the Maven Projects panel in IDEA
4. For children's shoes that Maven Projects can't find, you can check the View -> Tool Buttons on the toolbar at the top of IDEA, and then the Maven Projects panel will appear on the right side of IDEA
5. Find the Application class of each module to run each demo
6. **'Note: Each demo has a detailed README package, remember to look at the demo before eating Oh~'**
7. **'Note: Before running each demo, some need to initialize the database data in advance, don't forget Oh~'**

## Project trends

[! [Stargazers over time] (https://starchart.cc/xkcoding/spring-boot-demo.svg)] (https://starchart.cc/xkcoding/spring-boot-demo)

## Other

### Team renewal

Recruitment in the group, HC Juduo, Base Hangzhou, interested partners, view [job details](./jd.md)

### Open source recommended

! [11628591293_.pic_hd] (https://static.aliyun.xkcoding.com/2021/08/14/11628591293pichd.jpg?x-oss-process=style/tag_compress)

- 'JustAuth': The most comprehensive open source library in history that integrates third-party logins, https://github.com/justauth/JustAuth
- 'Mica': SpringBoot microservices efficient development toolset, https://github.com/lets-mica/mica
- `awesome-collector`：https://github.com/P-P-X/awesome-collector
- 'SpringBlade': Complete online solution (essential for enterprise development), https://github.com/chillzhuang/SpringBlade
- 'Pig': The universe's strongest microservices certification authorization scaffolding (essential for architects), https://github.com/pigxcloud/pig

### Development plan

View the [TODO](./TODO.md) file

### Introduction to each module

| The module name | Module introduces |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| [demo-helloworld] (./demo-helloworld)                         | A helloworld | of spring-boot
| [demo-properties] (./demo-properties)                         | spring-boot reads the contents of the configuration file |
| [demo-actuator] (./demo-actuator)                             | spring-boot integrated spring-boot-starter-actuator is used to monitor the startup and operational status of spring-boot |
| [demo-admin-client] (./demo-admin/admin-client)               | spring-boot integrates spring-boot-admin to visually monitor the running status of spring-boot programs and can be used with actuator, client examples |
| [demo-admin-server] (./demo-admin/admin-server)               | Spring-boot integrates spring-boot-admin to visually monitor the running status of spring-boot programs and can be used with actuator, server-side examples |
| [demo-logback] (./demo-logback)                               | spring-boot integrates logback logs |
| [demo-log-aop] (./demo-log-aop)                               | Spring-boot logs web request logs using AOP |
| [demo-exception-handler] (./demo-exception-handler)           | Spring-boot unified exception handling, including 2 kinds, the first one returns a unified json format, and the second unified jump to the exception page |
| [demo-template-freemarker] (./demo-template-freemarker)       | spring-boot integrates with the Freemarker template engine |
| [demo-template-thymeleaf] (./demo-template-thymeleaf)         | spring-boot integrates with the Thymeleaf template engine |
| [demo-template-beetl] (./demo-template-beetl)                 | spring-boot integrates the Beetl template engine |
| [demo-template-enjoy] (./demo-template-enjoy)                 | spring-boot integrates the Enjoy template engine |
| [demo-orm-jdbctemplate] (./demo-orm-jdbctemplate)             | spring-boot integrates with the Jdbc Template operations database and easily encapsulates the common Dao layer |
| [demo-orm-jpa] (./demo-orm-jpa)                               | spring-boot integrates spring-boot-starter-data-jpa operations database |
| [demo-orm-mybatis] (./demo-orm-mybatis)                       | spring-boot integrates native mybatis and integrates https://github.com/mybatis/spring using [mybatis-spring-boot-starter](|-boot-starter).
| [demo-orm-mybatis-mapper-page] (./demo-orm-mybatis-mapper-page) | spring-boot integrates [Universal Mapper] (https://github.com/abel533/Mapper) and [PageHelper] (https://github.com/pagehelper/Mybatis-PageHelper) to use  [mapper-spring-boot-starter] (https://github.com/abel533/Mapper/tree/master/spring-boot-starter) and [pagehelper-spring-boot-starter](https://github.com/pagehelper/pagehelper-spring-boot) integrate |
| [demo-orm-mybatis-plus] (./demo-orm-mybatis-plus)             | spring-boot integrates with [mybatis-plus](https://mybatis.plus/) and integrates with [mybatis-plus-boot-starter](http://mp.baomidou.com/) to integrate BaseMapper, BaseService, ActiveRecord operational databases, |
| [demo-orm-beetlsql] (./demo-orm-beetlsql)                     | spring-boot integrates [beetl-sql](http://ibeetl.com/guide/#beetlsql) and integrates http://ibeetl.com/guide/#beetlsql using the [beetl-framework-starter](|).
| [demo-upload] (./demo-upload)                                 | Spring-boot file upload example, including local file upload and Qiniu Cloud file upload |
| [demo-cache-redis] (./demo-cache-redis)                       | Spring-boot consolidates redis, manipulates data in redis, and caches data using redis |
| [demo-cache-ehcache] (./demo-cache-ehcache)                   | Spring-boot consolidates ehcache, using ehcache to cache data |
| [demo-email] (./demo-email)                                   | spring-boot integrates email, including sending simple text emails, HTML messages (including template HTML messages), attachment emails, static resource messages, |
| [demo-task] (./demo-task)                                     | spring-boot quickly implements timed task |
| [demo-task-quartz] (./demo-task-quartz)                       | spring-boot integrates quartz and implements the management of scheduled tasks, including adding new scheduled tasks, deleting scheduled tasks, pausing scheduled tasks, resuming scheduled tasks, modifying scheduled task start time, and scheduled task list query, 'providing front-end pages' |
| [demo-task-xxl-job] (./demo-task-xxl-job)                     | spring-boot integrates [xxl-job](http://www.xuxueli.com/xxl-job/en/#/) and provides a way to bypass the management of scheduled tasks by 'xxl-job-admin', including scheduled task lists, trigger lists, adding scheduled tasks, deleting scheduled tasks, stopping scheduled tasks, starting scheduled tasks, modifying scheduled tasks, manually triggering scheduled tasks|
| [demo-swagger] (./demo-swagger)                               | Spring-boot integrates native 'swagger' for unified management, testing of API interfaces, |
| [demo-swagger-beauty] (./demo-swagger-beauty)                 | spring-boot integrates third-party 'swagger' [swagger-bootstrap-ui] (https://github.com/xiaoymin/Swagger-Bootstrap-UI) to beautify API document styles for unified management and testing of API interface |
| [demo-rbac-security] (./demo-rbac-security)                   | spring-boot integrates spring security to complete the rights management based on the RBAC permission model, supports custom filtering requests, dynamic permission authentication, uses JWT security authentication, supports online number of people counting, manually kicks out users and other operations |
| [demo-rbac-shiro] (./demo-rbac-shiro)                         | The spring-boot integration shiro implements permission management <br/> <span style="color:pink;"> to be completed</span> |
| [demo-session] (./demo-session)                               | Spring-boot integrates Spring Session to implement Session sharing, restart program Session without invalidating |
| [demo-oauth] (./demo-oauth)                                   | spring-boot implements the oauth server function and implements the authorization code mechanism<br /> <span style="color:pink;"> to be completed</span> |
| [demo-social] (./demo-social)                                 | spring-boot integrates third-party login, integrates 'justauth-spring-boot-starter' to implement QQ login, GitHub login, WeChat login, Google login, Microsoft login, Xiaomi login, enterprise WeChat login. |
| [demo-zookeeper] (./demo-zookeeper)                           | Spring-boot integrated Zookeeper combined with AOP for distributed lock |
| [demo-mq-rabbitmq] (./demo-mq-rabbitmq)                       | spring-boot integrated RabbitMQ implements message sending and receiving | based on direct queue mode, column mode, topic mode, and delay queue
| [demo-mq-rocketmq] (./demo-mq-rocketmq)                       | spring-boot integrates with RocketMQ to enable sending and receiving messages <br/> <span style="color:pink;"> to be completed</span> |
| [demo-mq-kafka] (./demo-mq-kafka)                             | Spring-boot integrates kafka to enable the sending and receiving of messages |
| [demo-websocket] (./demo-websocket)                           | Spring-boot integrates websockets, and the backend actively pushes the front-end server operation information |
| [demo-websocket-socketio] (./demo-websocket-socketio)         | Spring-boot uses netty-socketio to integrate websockets to implement a simple chat room |
| [demo-ureport2] (./demo-ureport2)                             | spring-boot integrates ureport2 to implement complex custom Chinese-style reports <br/> <span style="color:pink;"> to be completed</span> |
| [demo-uflo] (./demo-uflo)                                     | Spring-boot integrates with uflo to quickly implement a lightweight process engine<br/> <span style="color:pink;"> to be completed</span> |
| [demo-urule] (./demo-urule)                                   | spring-boot integrates with urule to quickly implement the rules engine <br/> <span style="color:pink;"> to be completed</span> |
| [demo-activiti] (./demo-activiti)                             | spring-boot integrates the activiti 7 process engine <br/> <span style="color:pink;"> to be completed</span> |
| [demo-async] (./demo-async)                                   | Spring-boot implements the | of executing tasks asynchronously using natively provided asynchronous task support
| [demo-war] (./demo-war)                                       | Spring-boot is played as the configuration of the war package |
| [demo-elasticsearch] (./demo-elasticsearch)                   | spring-boot integrates with ElasticSearch and integrates 'spring-boot-starter-data-elasticsearch' to complete advanced usage techniques for ElasticSearch, including creating indexes, configuring mappings, dropping indexes, adding, deleting, correcting basic operations, complex queries, advanced queries, aggregate queries, and other |
| [demo-dubbo] (./demo-dubbo)                                   | spring-boot integrates Dubbo, which is the common module 'spring-boot-demo-dubbo-common', the service provider 'spring-boot-demo-dubbo-provider', the service caller 'spring-boot-demo-dubbo-consumer' |
| [demo-mongodb] (./demo-mongodb)                               | Spring-boot integrates with MongoDB and uses the official starter to implement add, delete, and check |
| [demo-neo4j] (./demo-neo4j)                                   | Spring-boot integrates with the Neo4j graph database to implement a demo | of a campus character network
| [demo-docker] (./demo-docker)                                 | spring-boot containerizes |
| [demo-multi-datasource-jpa] (./demo-multi-datasource-jpa)     | Spring-boot integrates multiple data sources using JPA |
| [demo-multi-datasource-mybatis] (./demo-multi-datasource-mybatis) | Spring-boot uses Mybatis to integrate multiple data sources and | using an open source solution provided by Mybatis-Plus
| [demo-sharding-jdbc] (./demo-sharding-jdbc)                   | spring-boot uses 'sharding-jdbc' to implement database sharding, while ORM uses Mybatis-Plus |
| [demo-tio] (./demo-tio)                                       | spring-boot integrates the tio network programming framework<br/> <span style="color:pink;"> to be completed</span> |
| demo-grpc                                                    | Spring-boot integrates grpc, configures tls/ssl, see [ISSUE#5](https://github.com/xkcoding/spring-boot-demo/issues/5)<br /> <span style="color:pink;"> to be completed</span> |
| [demo-codegen] (./demo-codegen)                               | Spring-boot integrates a code generator implemented by velocity template technology to simplify development |
| [demo-graylog] (./demo-graylog)                               | Spring-boot integrates graylog to implement unified log collection |
| demo-sso                                                     | spring-boot integrates SSO single sign-on, see [ISSUE#12](https://github.com/xkcoding/spring-boot-demo/issues/12)<br /> <span style="color:pink;"> to be completed</span> |
| [demo-ldap] (./demo-ldap)                                     | spring-boot integrates LDAP, integrates 'spring-boot-starter-data-ldap' to complete the basic CURD operation on Ldap, and gives an example of the API to log in as a practice, see [ISSUE#23](https://github.com/xkcoding/spring-boot-demo/issues/23), thanks to [@fxbin]( https://github.com/fxbin) |
| [demo-dynamic-datasource] (./demo-dynamic-datasource)         | spring-boot Dynamically add data sources, dynamically switch data sources, |
| [demo-ratelimit-guava] (./demo-ratelimit-guava)               | spring-boot uses Guava RateLimiter to implement stand-alone flow throttling to protect API |
| [demo-ratelimit-redis] (./demo-ratelimit-redis)               | spring-boot uses Redis + Lua scripts to implement distributed throttling and protect API |
| [demo-https] (./demo-https)                                   | spring-boot integrates HTTPS |
| [demo-elasticsearch-rest-high-level-client] (./demo-elasticsearch-rest-high-level-client) | Spring boot integrates with ElasticSearch version 7.x and operates ES data | using the official Rest High Level Client
| [demo-flyway] (./demo-flyway)                                 | Spring boot integrates with Flyway, initializes the database table structure at project startup, and supports database script versioning |
| [demo-ureport2] (./demo-ureport2)                             | Spring boot integrates with Ureport2 to achieve Chinese-style complex report design |

### Thanks

- <a href="https://www.jetbrains.com/?from=spring-boot-demo"><img src="http://static.xkcoding.com/spring-boot-demo/064312.jpg" width="100px" alt="jetbrains">** thanks Free and open source license from JetBrains**</a>

- [Thanks to the permanent activation code provided by MyBatisCodeHelper-Pro, the greatest code generation plugin ever] (https://gejun123456.github.io/MyBatisCodeHelper-Pro/#/?id=mybatiscodehelper-pro)

### License

[MIT] (http://opensource.org/licenses/MIT)

Copyright (c) 2018 Yangkai.Shen
