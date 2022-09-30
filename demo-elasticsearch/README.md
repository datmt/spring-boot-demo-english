# spring-boot-demo-elasticsearch

> This demo mainly demonstrates how Spring Boot integrates 'spring-boot-starter-data-elasticsearch' to complete advanced usage techniques for ElasticSearch, including creating indexes, configuring maps, deleting indexes, adding, deleting, and correcting basic operations, complex queries, advanced queries, aggregation queries, and so on.

## Note

When the author wrote this demo, the ElasticSearch version was '6.5.3' and ran using docker, here are all the steps:

1. Download the image: 'docker pull elasticsearch:6.5.3'

2. Run the container: 'docker run -d -p 9200:9200 -p 9300:9300 --name elasticsearch-6.5.3 elasticsearch:6.5.3'

3. Enter the container: 'docker exec -it elasticsearch-6.5.3 /bin/bash'

4. Install the ik tokenizer: './bin/elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v6.5.3/elasticsearch-analysis-ik-6.5.3.zip'

5. Modify the es configuration file: 'vi ./config/elasticsearch.yml

   ```yaml
   cluster.name: "docker-cluster"
   network.host: 0.0.0.0

   # minimum_master_nodes need to be explicitly set when bound on a public IP
   # set to 1 to allow single node clusters
   # Details: https://github.com/elastic/elasticsearch/pull/17288
   discovery.zen.minimum_master_nodes: 1

   # just for elasticsearch-head plugin
   http.cors.enabled: true
   http.cors.allow-origin: "*"
   ```

6. Exit the container: 'exit'

7. Stop container: 'docker stop elasticsearch-6.5.3'

8. Start the container: 'docker start elasticsearch-6.5.3'

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>spring-boot-demo-elasticsearch</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>spring-boot-demo-elasticsearch</name>
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
            <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
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

        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
        </dependency>

        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
        </dependency>
    </dependencies>

    <build>
        <finalName>spring-boot-demo-elasticsearch</finalName>
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
> @Document annotations primarily state index names, type names, number of shards, and number of backups
>
> @Field Note The main declaration field corresponds to the type of ES

```java
/**
 * <p>
 * User entity class
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-20 17:29
 */
@Document(indexName = EsConsts.INDEX_NAME, type = EsConsts.TYPE_NAME, shards = 1, replicas = 0)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    /**
     * Primary key
     */
    @Id
    private Long id;

    /**
     * First name
     */
    @Field(type = FieldType.Keyword)
    private String name;

    /**
     * Country
     */
    @Field(type = FieldType.Keyword)
    private String country;

    /**
     * Age
     */
    @Field(type = FieldType.Integer)
    private Integer age;

    /**
     * Birthday
     */
    @Field(type = FieldType.Date)
    private Date birthday;

    /**
     * Introduction
     */
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String remark;
}
```

## PersonRepository.java

```java
/**
 * <p>
 * User persistence layer
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-20 19:00
 */
public interface PersonRepository extends ElasticsearchRepository<Person, Long> {

    /**
     * Query based on age range
     *
     * @param min minimum
     * @param max value
     * @return list of users who meet the criteria
     */
    List<Person> findByAgeBetween(Integer min, Integer max);
}
```

## TemplateTest.java

> The main test is to create indexes, map configurations, and drop indexes

```java
/**
 * <p>
 * Test the creation/deletion of ElasticTemplate
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-20 17:46
 */
public class TemplateTest extends SpringBootDemoElasticsearchApplicationTests {
    @Autowired
    private ElasticsearchTemplate esTemplate;

    /**
     * Test ElasticTemplate to create an index
     */
    @Test
    public void testCreateIndex() {
        An index is created based on the @Document annotation information of the Item class
        esTemplate.createIndex(Person.class);

        When you configure the mapping, the mapping is automatically completed according to the id, Field, and other fields in the Item class
        esTemplate.putMapping(Person.class);
    }

    /**
     * Test ElasticTemplate to remove index
     */
    @Test
    public void testDeleteIndex() {
        esTemplate.deleteIndex(Person.class);
    }
}
```

## PersonRepositoryTest.java

> main functions, see the note above the method

```java
/**
 * <p>
 * Test the Repository operation ES
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-20 19:03
 */
@Slf4j
public class PersonRepositoryTest extends SpringBootDemoElasticsearchApplicationTests {
    @Autowired
    private PersonRepository repo;

    /**
     * New to testing
     */
    @Test
    public void save() {
        Person person = new Person (1L, "Liu Bei", "Shu Guo", 18, DateUtil.parse ("1990-01-02 03:04:05"), "Liu Bei (161 – June 10, 223), also known as Emperor Zhaolie of Han (reigned 221–223), also known as Xianju, Zi Xuande, a native of Zhuo County, Youzhou (present-day Zhuozhou, Hebei Province) in the late Eastern Han Dynasty, after Liu Sheng, King Jing of Zhongshan in the Western Han Dynasty, and founding emperor of the Shu Han Dynasty during the Three Kingdoms period Statesman. Liu Bei worshiped Lu Zhi as a teacher when he was a teenager; In his early years, he was displaced, prepared for hardships, defected to many princes, and participated in the suppression of the Yellow Turban Rebellion. He successively led the army to rescue Xiangkong Rong of the North Sea, Mutaoqian of Xuzhou and so on. After Tao Qian fell ill and died, he ceded Xuzhou to Liu Bei. At the Battle of Chibi, Liu Bei and Sun Quan defeated Cao Cao and took advantage of the situation to capture Jingzhou. And then forge ahead into Yizhou. In the first year of Zhang Wu (221), he was proclaimed emperor in Chengdu, with the state name Han, and the historical name Shu or Shu Han. The Chronicle of the Three Kingdoms commented that Liu Bei's power was slightly inferior to Cao Cao's, but his magnanimity and generosity, knowing people and treating soldiers, and persevering, eventually became an emperor. Liu Bei also said that he did things "every time he rebelled, things became a thing". In the third year of Zhang Wu (223), Liu Bei died of illness in the White Emperor City at the age of sixty-three, with the title of Emperor Zhaolie, the temple name of Liezu, and the burial of Huiling. In later generations, there are many literary and artistic works with him as the protagonist, and there is a Zhaolie Temple in Chengdu Wuhou Temple as a memorial. ");
        Person save = repo.save(person);
        log.info("【save】= {}", save);
    }

    /**
     * Test batch added
     */
    @Test
    public void saveList() {
        List<Person> personList = Lists.newArrayList();
        personList.add(new Person(2L, "Cao Cao", "Wei Guo", 20, DateUtil.parse ("1988-01-02 03:04:05"), "Cao Cao (155 – March 15, 220), character Mengde, a Jili, small character Achu, Pei Guo Yu County (present-day Bozhou, Anhui). He was an outstanding politician, military man, writer and calligrapher at the end of the Eastern Han Dynasty, and the founder of the Cao Wei regime in the Three Kingdoms. Cao Cao served as the chancellor of the Eastern Han Dynasty, and was later crowned King of Wei, laying the foundation for Cao Wei's statehood. After his death, he was given the title of King of Wu. His son Cao Pi was proclaimed empress dowager and posthumously honored as Emperor Wu (武皇帝), with the temple name Taizu. In the last year of the Eastern Han Dynasty, when the world was in chaos, Cao Cao conscripted the four sides in the name of Han Tianzi, eliminated Eryuan, Lü Bu, Liu Biao, Ma Chao, Han Sui, and other separatist forces internally, surrendered to the Southern Xiongnu, Wuhuan, and Xianbei externally, unified northern China, and implemented a series of policies to restore economic production and social order, expand Tun Tian, build water conservancy, reward Nongsang, attach importance to handicraft industry, resettle the exiled population, and implement "rent and regulation", so that the society in the Central Plains gradually stabilized and the economy took a turnaround. Under Cao Cao's rule, the Yellow River Basin gradually became politically clear, the economy gradually recovered, class oppression was slightly reduced, and the social atmosphere improved. Some of the measures taken by Cao Cao in the name of the Han Dynasty had a positive effect. Cao Cao was militarily proficient in the art of war, valued the wise and loved the talents, and for this reason spared no effort to take the potential elements he valued under his command; Life is good poetry, expressing their political ambitions, and reflecting the suffering life of the people at the end of the Han Dynasty, majestic and majestic, generous and sad; The prose is also clean and tidy, which opens up and prospers Jian'an literature and leaves a valuable spiritual wealth for future generations, and Lu Xun evaluates it as "the ancestor of transforming articles". At the same time, Cao Cao was also good at calligraphy, and Zhang Huaihuan of the Tang Dynasty rated Cao Cao's chapter grass as "Miaopin" in the Book Break. "));
        personList.add(new Person(3L, "Sun Quan", "Wu Guo", 19, DateUtil.parse ("1989-01-02 03:04:05"), "Sun Quan (182 – May 21, 252), courtesy name Zhongmou, was a native of Fuchun, Wu County (present-day Fuyang District, Hangzhou, Zhejiang). Founder of Sun Wu during the Three Kingdoms period (reigned 229-252). Sun Quan's father, Sun Jian, and his elder brother Sun Ce laid the foundation of Jiangdong in the last years of the Eastern Han Dynasty. In the fifth year of Jian'an (200), Sun Ce was assassinated, and Sun Quan succeeded him as a prince of the party. In the thirteenth year of Jian'an (208), he established an alliance with Liu Bei and defeated Cao Cao at the Battle of Chibi, laying the foundation for the establishment of the Three Kingdoms. In the twenty-fourth year of Jian'an (219), Sun Quan sent Lü Meng to successfully attack Liu Bei's Jingzhou, greatly increasing the territorial area. In the first year of Huang Wu (222), Sun Quan was crowned King of Wu by the Wei emperor Cao Pi and established the state of Wu. In the same year, he defeated Liu Bei at the Battle of Yiling. In the first year of Huanglong (229), he was officially proclaimed emperor in Wuchang, with the state name Wu, and soon moved the capital to Jianye. Sun Quan became empress, set up agricultural officials, implemented tun tian, set up counties and counties, and continued to suppress Shanyue, which promoted the economic development of Jiangnan. On this basis, he sent people to the sea many times. In the second year of Huanglong (230), Sun Quan sent Wei Wen and Zhuge Zhi to Yizhou. In his later years, Sun Quan was capricious on the issue of heirs, which led to party strife among the masses and an unstable situation in the DPRK. In the first year of Taiyuan (252), he died of illness at the age of seventy-one, reigned for twenty-four years, was known as the Great Emperor and the temple name Taizu, and was buried in the Jiang Mausoleum. Sun Quan was also good at writing, and Zhang Huaihuan of the Tang Dynasty listed his calligraphy as the third class in the Book Estimate. "));
        personList.add(new Person(4L, "Zhuge Liang", "Shu Kingdom", 16, DateUtil.parse ("1992-01-02 03:04:05"), "Zhuge Liang (181 – October 8, 234), Zi Kongming, Wolong, Xuzhou Langya Yangdu (present-day Yinan County, Linyi, Shandong), Shu Dynasty during the Three Kingdoms period, outstanding statesman, military strategist, diplomat, writer, calligrapher, inventor. In his early years, he accompanied his uncle Zhuge Xuan to Jingzhou, and after Zhuge Xuan's death, Zhuge Liang lived in seclusion in Xiangyang Long. Later, Liu Beisan invited Zhuge Liang to join forces with Sun to resist Cao and defeat Cao's army at the Battle of Chibi. Forming the momentum of the Three Kingdoms, it also captured Jingzhou. In the sixteenth year of Jian'an (211), he captured Yizhou. He then defeated the Cao army and captured Hanzhong. In the first year of Shu Zhangwu (221), Liu Bei established the Shu Han government in Chengdu, and Zhuge Liang was appointed as the chancellor and presided over the imperial government. After the Shu lord Liu Chan succeeded to the throne, Zhuge Liang was given the title of Marquis of Wuxiang (武乡侯) and Lingyi Prefecture (益州牧). Be diligent and prudent, handle big and small political affairs personally, and reward and punish strictly; Alliances with Eastern Wu to improve relations with the various ethnic groups in the southwest; Implement the Tuntian policy and strengthen combat readiness. Six times before and after the Northern Expedition to the Central Plains, most of them used grain to no avail. Due to overwork, he died in the twelfth year of Shu Jianxing (234) at the age of 54 in Wuzhangyuan (in present-day Baojiqishan, Shaanxi). Liu Chan posthumously honored him as Marquis of Zhongwu, and later generations often honored him as Zhuge Liang. The Eastern Jin Dynasty posthumously honored him as the King of Wuxing because of its military prowess. Zhuge Liang's prose masterpieces include "Table of Renunciation" and "Book of Commandments". He invented the wooden cow flowing horse, Kong Ming lantern, etc., and transformed the crossbow, called the Zhuge crossbow, which can fire ten arrows with one crossbow. Zhuge Liang's life, "bowing down to exhaustion, dying and then dying", is a representative figure of loyal subjects and wise men in traditional Chinese culture. "));
        Iterable<Person> people = repo.saveAll(personList);
        log.info("【people】= {}", people);
    }

    /**
     * Test updates
     */
    @Test
    public void update() {
        repo.findById(1L).ifPresent(person -> {
            person.setRemark()(person.getRemark() + "\nUpdate Update Update Update Update Update");
            Person save = repo.save(person);
            log.info("【save】= {}", save);
        });
    }

    /**
     * Test removal
     */
    @Test
    public void delete() {
        Primary key deletion
        repo.deleteById(1L);

        The object is deleted
        repo.findById(2L).ifPresent(person -> repo.delete(person));

        Bulk delete
        repo.deleteAll(repo.findAll());
    }

    /**
     * Test normal queries in reverse order of birthdays
     */
    @Test
    public void select() {
        repo.findAll(Sort.by(Sort.Direction.DESC, "birthday"))
                .forEach(person -> log.info("{} birthday: {}", person.getName(), DateUtil.formatDateTime(person.getBirthday())));
    }

    /**
     * Custom queries, based on age range
     */
    @Test
    public void customSelectRangeOfAge() {
        repo.findByAgeBetween(18, 19).forEach(person -> log.info("{} Age: {}", person.getName(), person.getAge()));
    }

    /**
     * Advanced queries
     */
    @Test
    public void advanceSelect() {
        QueryBuilders provides a number of static methods to encapsulate most query conditions
        MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery("name", "Sun Quan");
        log.info("【queryBuilder】= {}", queryBuilder.toString());

        repo.search(queryBuilder).forEach(person -> log.info("【person】= {}", person));
    }

    /**
     * Customize advanced queries
     */
    @Test
    public void customAdvanceSelect() {
        Construct query criteria
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        Add basic word breaker conditions
        queryBuilder.withQuery (QueryBuilders.matchQuery("remark", "Eastern Han"));
        Sort criteria
        queryBuilder.withSort(SortBuilders.fieldSort("age").order(SortOrder.DESC));
        Paging conditions
        queryBuilder.withPageable(PageRequest.of(0, 2));
        Page<Person> people = repo.search(queryBuilder.build());
        log.info("[people] total number = {}", people.getTotalElements());
        log.info("[people] total pages = {}", people.getTotalPages());
        people.forEach(person -> log.info("[person] = {}, age = {}", person.getName(), person.getAge()));
    }

    /**
     * Test aggregation, test average age
     */
    @Test
    public void agg() {
        Construct query criteria
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        No results are queried
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""}, null));

        Average age
        queryBuilder.addAggregation(AggregationBuilders.avg("avg").field("age"));

        log.info("【queryBuilder】= {}", JSONUtil.toJsonStr(queryBuilder.build()));

        AggregatedPage<Person> people = (AggregatedPage<Person>) repo.search(queryBuilder.build());
        double avgAge = ((InternalAvg) people.getAggregation("avg")).getValue();
        log.info("【avgAge】= {}", avgAge);
    }

    /**
     * Test advanced aggregate queries, there are several people in each country, and what is the average age of each country
     */
    @Test
    public void advanceAgg() {
        Construct query criteria
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        No results are queried
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""}, null));

         1. Add a new aggregation with the aggregation type as terms, the aggregation name as country, and the aggregation field as age
        queryBuilder.addAggregation(AggregationBuilders.terms("country").field("country")
                 2. Nested aggregations are performed within the national aggregation bucket to find the average age
                .subAggregation(AggregationBuilders.avg("avg").field("age")));

        log.info("【queryBuilder】= {}", JSONUtil.toJsonStr(queryBuilder.build()));

         3. Inquire
        AggregatedPage<Person> people = (AggregatedPage<Person>) repo.search(queryBuilder.build());

         4. parse
         4.1. Take the aggregation named country from the result, because it is a term aggregation using a String type field, so the result is strongly converted to the StringTerm type
        StringTerms country = (StringTerms) people.getAggregation("country");
         4.2. Gets the bucket
        List<StringTerms.Bucket> buckets = country.getBuckets();
        for (StringTerms.Bucket bucket : buckets) {
             4.3. Get the key in the bucket, which is the country name 4.4. Gets the number of documents in the bucket
            log.info("{} Total has {} 人", bucket.getKeyAsString(), bucket.getDocCount());
             4.5. To get the sub-aggregate result:
            InternalAvg avg = (InternalAvg) bucket.getAggregations().asMap().get("avg");
            log.info ("mean age: {}", avg);
        }
    }

}
```

## Reference

1. Official ElasticSearch documentation: https://www.elastic.co/guide/en/elasticsearch/reference/6.x/getting-started.html
2. Spring-data-elasticsearch Official Documentation: https://docs.spring.io/spring-data/elasticsearch/docs/3.1.2.RELEASE/reference/html/

