# spring-boot-demo-elasticsearch-rest-high-level-client

> This demo demonstrates how Spring Boot integrates 'elasticsearch-rest-high-level-client' to complete basic CURD operations on the 'ElasticSearch 7.x' release

## Elasticsearch upgrade

First upgrade to 6.8, index creation, set mapping and other operations plus parameters: include_type_name=true, and then scroll to upgrade to 7, the old index can be accessed with type. For details, please refer to:

https://www.elastic.co/cn/blog/moving-from-types-to-typeless-apis-in-elasticsearch-7-0

https://www.elastic.co/guide/en/elasticsearch/reference/7.0/removal-of-types.html

## Note

When the author wrote this demo, the version of ElasticSearch was '7.3.0' and it ran using docker, here are all the steps:

1. Download the image: 'docker pull elasticsearch:7.3.0'

2. Download and install 'docker-compose', reference document: https://docs.docker.com/compose/install/

```bash
sudo curl -L "https://github.com/docker/compose/releases/download/1.24.1/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
```

3. Write a docker-compose file

```yaml
version: "3"

services:
  es7:
    hostname: es7
    container_name: es7
    image: elasticsearch:7.3.0
    volumes:
      - "/data/es7/logs:/usr/share/es7/logs:rw"
      - "/data/es7/data:/usr/share/es7/data:rw"
    restart: on-failure
    ports:
      - "9200:9200"
      - "9300:9300"
    environment:
      cluster.name: elasticsearch
      discovery.type: single-node
    logging:
      driver: "json-file"
      options:
        max-size: "50m"

```
4.Launch: 'docker-compose -f elasticsearch.yaml up -d'

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <parent>
    <artifactId>spring-boot-demo</artifactId>
    <groupId>com.xkcoding</groupId>
    <version>1.0.0-SNAPSHOT</version>
  </parent>

  <artifactId>spring-boot-demo-elasticsearch-rest-high-level-client</artifactId>
  <name>spring-boot-demo-elasticsearch-rest-high-level-client</name>
  <description>Demo project for Spring Boot</description>

  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    <java.version>1.8</java.version>
  </properties>

  <dependencies>

    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter</artifactId>
    </dependency>

    <!-- test -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-test</artifactId>
      <scope>test</scope>
    </dependency>

    <!-- validator -->
    <dependency>
      <groupId>org.hibernate.validator</groupId>
      <artifactId>hibernate-validator</artifactId>
      <scope>compile</scope>
    </dependency>

    <!--
        You can easily generate your own configuration metadata file from items annotated with
        @ConfigurationProperties by using the spring-boot-configuration-processor jar.
        -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-configuration-processor</artifactId>
    </dependency>

    <!-- Tool Class -->
    <dependency>
      <groupId>cn.hutool</groupId>
      <artifactId>hutool-all</artifactId>
    </dependency>

    <!-- elasticsearch -->
    <dependency>
      <groupId>org.elasticsearch</groupId>
      <artifactId>elasticsearch</artifactId>
      <version>7.3.0</version>
    </dependency>

    <!-- elasticsearch-rest-client -->
    <dependency>
      <groupId>org.elasticsearch.client</groupId>
      <artifactId>elasticsearch-rest-client</artifactId>
      <version>7.3.0</version>
    </dependency>

    <!-- elasticsearch-rest-high-level-client -->
    <dependency>
      <groupId>org.elasticsearch.client</groupId>
      <artifactId>elasticsearch-rest-high-level-client</artifactId>
      <version>7.3.0</version>
      <exclusions>
        <exclusion>
          <groupId>org.elasticsearch.client</groupId>
          <artifactId>elasticsearch-rest-client</artifactId>
        </exclusion>
        <exclusion>
          <groupId>org.elasticsearch</groupId>
          <artifactId>elasticsearch</artifactId>
        </exclusion>
      </exclusions>
    </dependency>

    <!-- lombok -->
    <dependency>
      <groupId>org.projectlombok</groupId>
      <artifactId>lombok</artifactId>
      <optional>true</optional>
    </dependency>

  </dependencies>

  <build>
    <finalName>spring-boot-demo-elasticsearch-rest-high-level-client</finalName>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
      </plugin>
    </plugins>
  </build>

</project>
```

## Person.java

> Entity classes
>

```java
package com.xkcoding.elasticsearch.model;

import lombok. AllArgsConstructor;
import lombok. Builder;
import lombok. Data;
import lombok. NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * Person
 *
 * @author fxbin
 * @version v1.0
 * @since 2019-09-15 23:04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person implements Serializable {

    private static final long serialVersionUID = 8510634155374943623L;

    /**
     * Primary key
     */
    private Long id;

    /**
     * First name
     */
    private String name;

    /**
     * Country
     */
    private String country;

    /**
     * Age
     */
    private Integer age;

    /**
     * Birthday
     */
    private Date birthday;

    /**
     * Introduction
     */
    private String remark;

}
```

## PersonService.java

```java
package com.xkcoding.elasticsearch.service;

import com.xkcoding.elasticsearch.model.Person;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * PersonService
 *
 * @author fxbin
 * @version v1.0
 * @since 2019-09-15 23:07
 */
public interface PersonService {

    /**
     * create Index
     *
     * @author fxbin
     * @param index elasticsearch index name
     */
    void createIndex(String index);

    /**
     * delete Index
     *
     * @author fxbin
     * @param index elasticsearch index name
     */
    void deleteIndex(String index);

    /**
     * insert document source
     *
     * @author fxbin
     * @param index elasticsearch index name
     * @param list data source
     */
    void insert(String index, List<Person> list);

    /**
     * update document source
     *
     * @author fxbin
     * @param index elasticsearch index name
     * @param list data source
     */
    void update(String index, List<Person> list);

    /**
     * delete document source
     *
     * @author fxbin
     * @param person delete data source and allow null object
     */
    void delete(String index, @Nullable Person person);

    /**
     * search all doc records
     *
     * @author fxbin
     * @param index elasticsearch index name
     * @return person list
     */
    List<Person> searchList(String index);

}
```

## PersonServiceImpl.java

> service implementation type, basic curd operation

```java
package com.xkcoding.elasticsearch.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.xkcoding.elasticsearch.model.Person;
import com.xkcoding.elasticsearch.service.base.BaseElasticsearchService;
import com.xkcoding.elasticsearch.service.PersonService;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * PersonServiceImpl
 *
 * @author fxbin
 * @version v1.0
 * @since 2019-09-15 23:08
 */
@Service
public class PersonServiceImpl extends BaseElasticsearchService implements PersonService {

    @Override
    public void createIndex(String index) {
        createIndexRequest(index);
    }

    @Override
    public void deleteIndex(String index) {
        deleteIndexRequest(index);
    }

    @Override
    public void insert(String index, List<Person> list) {

        try {
            list.forEach(person -> {
                IndexRequest request = buildIndexRequest(index, String.valueOf(person.getId()), person);
                try {
                    client.index(request, COMMON_OPTIONS);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void update(String index, List<Person> list) {
        list.forEach(person -> {
            updateRequest(index, String.valueOf(person.getId()), person);
        });
    }

    @Override
    public void delete(String index, Person person) {
        if (ObjectUtils.isEmpty(person)) {
            If the person object is empty, the full amount is deleted
            searchList(index).forEach(p -> {
                deleteRequest(index, String.valueOf(p.getId()));
            });
        }
        deleteRequest(index, String.valueOf(person.getId()));
    }

    @Override
    public List<Person> searchList(String index) {
        SearchResponse searchResponse = search(index);
        SearchHit[] hits = searchResponse.getHits().getHits();
        List<Person> personList = new ArrayList<>();
        Arrays.stream(hits).forEach(hit -> {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            Person person = BeanUtil.mapToBean(sourceAsMap, Person.class, true);
            personList.add(person);
        });
        return personList;
    }
}
```


## ElasticsearchApplicationTests.java

> the main functional test, see the service comment

```java
package com.xkcoding.elasticsearch;

import com.xkcoding.elasticsearch.contants.ElasticsearchConstant;
import com.xkcoding.elasticsearch.model.Person;
import com.xkcoding.elasticsearch.service.PersonService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ElasticsearchApplicationTests {

    @Autowired
    private PersonService personService;

    /**
     * Test to drop indexes
     */
    @Test
    public void deleteIndexTest() {
        personService.deleteIndex(ElasticsearchConstant.INDEX_NAME);
    }

    /**
     * Test index creation
     */
    @Test
    public void createIndexTest() {
        personService.createIndex(ElasticsearchConstant.INDEX_NAME);
    }

    /**
     * New to testing
     */
    @Test
    public void insertTest() {
        List<Person> list = new ArrayList<>();
        list.add(Person.builder().age(11).birthday(new Date()).country("CN").id(1L).name("haha").remark("test1").build());
        list.add(Person.builder().age(22).birthday(new Date()).country("US").id(2L).name("hiahia").remark("test2").build());
        list.add(Person.builder().age(33).birthday(new Date()).country("ID").id(3L).name("Oh").remark("test3").build());

        personService.insert(ElasticsearchConstant.INDEX_NAME, list);
    }

    /**
     * Test updates
     */
    @Test
    public void updateTest() {
        Person person = Person.builder().age(33).birthday(new Date()).country("ID_update").id(3L).name("Oh update").remark("test3_update").build();
        List<Person> list = new ArrayList<>();
        list.add(person);
        personService.update(ElasticsearchConstant.INDEX_NAME, list);
    }

    /**
     * Test removal
     */
    @Test
    public void deleteTest() {
        personService.delete(ElasticsearchConstant.INDEX_NAME, Person.builder().id(1L).build());
    }

    /**
     * Test query
     */
    @Test
    public void searchListTest() {
        List<Person> personList = personService.searchList(ElasticsearchConstant.INDEX_NAME);
        System.out.println(personList);
    }

}
```

## Reference

- Official ElasticSearch documentation: https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html

- Java High Level REST Client：https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.3/java-rest-high.html

