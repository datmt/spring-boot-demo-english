# spring-boot-demo-neo4j

> This demo demonstrates how Spring Boot integrates with the Neo4j Operational Graph database to implement a campus network of people.

## Note

When the author wrote this demo, the Neo4j version is '3.5.0' and runs using docker, here are all the steps:

1. Download the image: 'docker pull neo4j:3.5.0'
2. Run the container: 'docker run -d -p 7474:7474 -p 7687:7687 --name neo4j-3.5.0 neo4j:3.5.0'
3. Stop container: 'docker stop neo4j-3.5.0'
4. Start container: 'docker start neo4j-3.5.0'
5. Browser http://localhost:7474/ access neo4j management background, initial account / password neo4j/neo4j, will ask to modify the initialization password, we modify it to neo4j/admin

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>spring-boot-demo-neo4j</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>spring-boot-demo-neo4j</name>
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
            <artifactId>spring-boot-starter-data-neo4j</artifactId>
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
        <finalName>spring-boot-demo-neo4j</finalName>
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
    neo4j:
      uri: bolt://localhost
      username: neo4j
      password: admin
      open-in-view: false
```

## CustomIdStrategy.java

```java
/**
 * <p>
 * Custom primary key policy
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-24 14:40
 */
public class CustomIdStrategy implements IdStrategy {
    @Override
    public Object generateId(Object o) {
        return IdUtil.fastUUID();
    }
}
```

## Part of the Model code

### Student.java

```java
/**
 * <p>
 * Student node
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-24 14:38
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor(staticName = "of")
@AllArgsConstructor
@Builder
@NodeEntity
public class Student {
    /**
     * Primary key, custom primary key strategy, generated using UUID
     */
    @Id
    @GeneratedValue(strategy = CustomIdStrategy.class)
    private String id;

    /**
     * Student name
     */
    @NonNull
    private String name;

    /**
     * All courses selected by the student
     */
    @Relationship(NeoConsts.R_LESSON_OF_STUDENT)
    @NonNull
    private List<Lesson> lessons;

    /**
     * The student's class
     */
    @Relationship(NeoConsts.R_STUDENT_OF_CLASS)
    @NonNull
    private Class clazz;

}
```

## Part of the Repository code

### StudentRepository.java

```java
/**
 * <p>
 * Student node Repository
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-24 15:05
 */
public interface StudentRepository extends Neo4jRepository<Student, String> {
    /**
     * Find students by name
     *
     * @param name name
     * @param depth depth
     * @return Student information
     */
    Optional<Student> findByName(String name, @Depth int depth);

    /**
     * Check class size by class
     *
     * @param className class name
     * @return Class size
     */
    @Query("MATCH (s:Student)-[r:R_STUDENT_OF_CLASS]->(c:Class{name:{className}}) return count(s)")
    Long countByClassName(@Param("className") String className);


    /**
     * Enquire about students who meet the (student)-[course selection relationship]-(course)-[course selection relationship]-(student) relationship
     *
     * @return Return to classmate relationships
     */
    @Query("match (s:Student)-[:R_LESSON_OF_STUDENT]->(l:Lesson)<-[:R_LESSON_OF_STUDENT]-(:Student) with l.name as lessonName,collect(distinct s) as students return lessonName, students")
    List<ClassmateInfoGroupByLesson> findByClassmateGroupByLesson();

    /**
     * Enquire about teacher-student relationship, (student)-[class-student relationship]-(class)-[class teacher relationship]-(teacher)
     *
     * @return Return to teacher-student relationship
     */
    @Query("match (s:Student)-[:R_STUDENT_OF_CLASS]->(:Class)-[:R_BOSS_OF_CLASS]->(t:Teacher) with t.name as teacherName,collect(distinct s) as students return teacherName,students ")
    List<TeacherStudent> findTeacherStudentByClass();

    /**
     * Enquire about teacher-student relationship, (student)-[course selection relationship]-(curriculum)-[teacher-teacher relationship]-(teacher)
     *
     * @return Return to teacher-student relationship
     */
    @Query("match ((s:Student)-[:R_LESSON_OF_STUDENT]->(:Lesson)-[:R_TEACHER_OF_LESSON]->(t:Teacher))with t.name as teacherName,collect(distinct s) as students return teacherName, students")
    List<TeacherStudent> findTeacherStudentByLesson();
}
```

## Neo4jTest.java

```java
/**
 * <p>
 * Test Neo4j
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-24 15:17
 */
@Slf4j
public class Neo4jTest extends SpringBootDemoNeo4jApplicationTests {
    @Autowired
    private NeoService neoService;

    /**
     * Test save
     */
    @Test
    public void testSave() {
        neoService.initData();
    }

    /**
     * Test removal
     */
    @Test
    public void testDelete() {
        neoService.delete();
    }

    /**
     * Test to find out which courses Naruto has taken
     */
    @Test
    public void testFindLessonsByStudent() {
        If the depth is 1, the attribute of the teacher of the course is null
        A depth of 2 assigns a value to the instructor of the course
        List<Lesson> lessons = neoService.findLessonsFromStudent ("Naruto Uzumaki", 2);

        lessons.forEach(lesson -> log.info("【lesson】= {}", JSONUtil.toJsonStr(lesson)));
    }

    /**
     * Test check class size
     */
    @Test
    public void testCountStudent() {
        Long all = neoService.studentCount(null);
        log.info ("[Total School Attendance] = {}", all);
        Long seven = neoService.studentCount ("Class VII");
        log.info ("[Seventh Class] = {}", seven);
    }

    /**
     * Test to query classmate relationships according to the course
     */
    @Test
    public void testFindClassmates() {
        Map<String, List<Student>> classmates = neoService.findClassmatesGroupByLesson();
        classmates.forEach((k, v) -> log.info("Because we took [{}] class together, the relationship that became classmates was:{}", k, JSONUtil.toJsonStr(v.stream()
                .map(Student::getName)
                .collect(Collectors.toList()))));
    }

    /**
     * Enquire about all teacher-student relationships, including homeroom teacher/student, classroom teacher/student
     */
    @Test
    public void testFindTeacherStudent() {
        Map<String, Set<Student>> teacherStudent = neoService.findTeacherStudent();
        teacherStudent.forEach((k, v) -> log.info("[{}]Students taught have {}", k, JSONUtil.toJsonStr(v.stream()
                .map(Student::getName)
                .collect(Collectors.toList()))));
    }
}
```

## Screenshots

After running the test class, you can view all the nodes and relationships in the neo by visiting the http://localhost:7474 

! [image-20181225150513101] (http://static.xkcoding.com/spring-boot-demo/neo4j/063605.jpg)



## Reference

- spring-data-neo4j Official Document: https://docs.spring.io/spring-data/neo4j/docs/5.1.2.RELEASE/reference/html/
- neo4j Official Documentation: https://neo4j.com/docs/getting-started/3.5/
