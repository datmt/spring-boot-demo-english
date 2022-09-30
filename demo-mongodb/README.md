# spring-boot-demo-mongodb

> This demo mainly demonstrates how Spring Boot integrates with MongoDB and uses the official starter to implement additions, deletions, and changes.

## Note

When the author wrote this demo, the latest version of MongoDB was '4.1', running with docker, here are all the steps:

1. Download the image: 'docker pull mongo:4.1'
2. Run the container: 'docker run -d -p 27017:27017 -v /Users/yangkai.shen/docker/mongo/data:/data/db --name mongo-4.1 mongo:4.1'
3. Stop container: 'docker stop mongo-4.1'
4. Start the container: 'docker start mongo-4.1'

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>spring-boot-demo-mongodb</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>spring-boot-demo-mongodb</name>
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
            <artifactId>spring-boot-starter</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-mongodb</artifactId>
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
        <finalName>spring-boot-demo-mongodb</finalName>
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
  data:
    mongodb:
      host: localhost
      port: 27017
      database: article_db
logging:
  level:
    org.springframework.data.mongodb.core: debug
```

## Article.java

```java
/**
 * <p>
 * Article entity class
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-28 16:21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Article {
    /**
     * Article id
     */
    @Id
    private Long id;

    /**
     * Article title
     */
    private String title;

    /**
     * Article content
     */
    private String content;

    /**
     * Creation time
     */
    private Date createTime;

    /**
     * Update time
     */
    private Date updateTime;

    /**
     * Number of likes
     */
    private Long thumbUp;

    /**
     * Number of visitors
     */
    private Long visits;

}
```

## ArticleRepository.java

```java
/**
 * <p>
 * Article Dao
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-28 16:30
 */
public interface ArticleRepository extends MongoRepository<Article, Long> {
    /**
     * Fuzzy query based on title
     *
     * @param title title
     * @return list of articles that meet the criteria
     */
    List<Article> findByTitleLike(String title);
}
```

## ArticleRepositoryTest.java

```java
/**
 * <p>
 * Test operation MongoDb
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-28 16:35
 */
@Slf4j
public class ArticleRepositoryTest extends SpringBootDemoMongodbApplicationTests {
    @Autowired
    private ArticleRepository articleRepo;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private Snowflake snowflake;

    /**
     * New to testing
     */
    @Test
    public void testSave() {
        Article article = new Article(1L, RandomUtil.randomString(20), RandomUtil.randomString(150), DateUtil.date(), DateUtil
                .date(), 0L, 0L);
        articleRepo.save(article);
        log.info("【article】= {}", JSONUtil.toJsonStr(article));
    }

    /**
     * Test new list
     */
    @Test
    public void testSaveList() {
        List<Article> articles = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            articles.add(new Article(snowflake.nextId(), RandomUtil.randomString(20), RandomUtil.randomString(150), DateUtil
                    .date(), DateUtil.date(), 0L, 0L));
        }
        articleRepo.saveAll(articles);

        log.info("【articles】= {}", JSONUtil.toJsonStr(articles.stream()
                .map(Article::getId)
                .collect(Collectors.toList())));
    }

    /**
     * Test updates
     */
    @Test
    public void testUpdate() {
        articleRepo.findById(1L).ifPresent(article -> {
            article.setTitle(article.getTitle() + "Updated title");
            article.setUpdateTime(DateUtil.date());
            articleRepo.save(article);
            log.info("【article】= {}", JSONUtil.toJsonStr(article));
        });
    }

    /**
     * Test removal
     */
    @Test
    public void testDelete() {
        Delete based on the primary key
        articleRepo.deleteById(1L);

        Delete All
        articleRepo.deleteAll();
    }

    /**
     * Test the number of likes and visitors, and use save to update likes and visitors
     */
    @Test
    public void testThumbUp() {
        articleRepo.findById(1L).ifPresent(article -> {
            article.setThumbUp(article.getThumbUp() + 1);
            article.setVisits(article.getVisits() + 1);
            articleRepo.save(article);
            log.info("[title] = {}【Likes】= {}【Number of visitors】= {}", article.getTitle(), article.getThumbUp(), article.getVisits());
        });
    }

    /**
     * Test likes, visitors, and update likes and visitors in a more elegant/efficient way
     */
    @Test
    public void testThumbUp2() {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(1L));
        Update update = new Update();
        update.inc("thumbUp", 1L);
        update.inc("visits", 1L);
        mongoTemplate.updateFirst(query, update, "article");

        articleRepo.findById(1L)
                .ifPresent(article-> log.info("[title]={}【Likes】= {}【Number of visitors】= {}", article.getTitle(), article.getThumbUp(), article
                        .getVisits()));
    }

    /**
     * Test paginated sort query
     */
    @Test
    public void testQuery() {
        Sort sort = Sort.by("thumbUp", "updateTime").descending();
        PageRequest pageRequest = PageRequest.of(0, 5, sort);
        Page<Article> all = articleRepo.findAll(pageRequest);
        log.info("[Total pages] = {}", all.getTotalPages());
        log.info("[Total Items] = {}", all.getTotalElements());
        log.info("[Current page data] = {}", JSONUtil.toJsonStr(all.getContent()
                .stream()
                .map(article -> "Article title:" + article.getTitle() + "Likes:" + article.getThumbUp() + "Update time:" + article.getUpdateTime())
                .collect(Collectors.toList())));
    }

    /**
     * Test fuzzy query based on title
     */
    @Test
    public void testFindByTitleLike() {
        List<Article> articles = articleRepo.findByTitleLike ("Update");
        log.info("【articles】= {}", JSONUtil.toJsonStr(articles));
    }

}
```

## Reference

1. Spring Data MongoDB Official Documentation: https://docs.spring.io/spring-data/mongodb/docs/2.1.2.RELEASE/reference/html/
2. MongoDB official image address: https://hub.docker.com/_/mongo
3. MongoDB Official Quickstart: https://docs.mongodb.com/manual/tutorial/getting-started/
4. MongoDB Official Documentation: https://docs.mongodb.com/manual/
