# spring-boot-demo-orm-jdbctemplate
> This demo demonstrates how Spring Boot can use JdbcTemplate to manipulate databases and easily encapsulates a common Dao layer, including additions, deletions, and corrections.

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
   <modelVersion>4.0.0</modelVersion>

   <artifactId>spring-boot-demo-orm-jdbctemplate</artifactId>
   <version>1.0.0-SNAPSHOT</version>
   <packaging>jar</packaging>

   <name>spring-boot-demo-orm-jdbctemplate</name>
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
         <artifactId>spring-boot-starter-jdbc</artifactId>
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
         <groupId>mysql</groupId>
         <artifactId>mysql-connector-java</artifactId>
      </dependency>

      <dependency>
         <groupId>cn.hutool</groupId>
         <artifactId>hutool-all</artifactId>
      </dependency>

      <dependency>
         <groupId>org.projectlombok</groupId>
         <artifactId>lombok</artifactId>
         <optional>true</optional>
      </dependency>
   </dependencies>

   <build>
      <finalName>spring-boot-demo-orm-jdbctemplate</finalName>
      <plugins>
         <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
         </plugin>
      </plugins>
   </build>

</project>
```

## BaseDao.java

```java
/**
 * <p>
 * Dao base class
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-10-15 11:28
 */
@Slf4j
public class BaseDao<T, P> {
   private JdbcTemplate jdbcTemplate;
   private Class<T> clazz;

   @SuppressWarnings(value = "unchecked")
   public BaseDao(JdbcTemplate jdbcTemplate) {
      this.jdbcTemplate = jdbcTemplate;
      clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
   }

   /**
    * Generic insertion, self-incrementing column requires the addition of {@link Pk} annotations
    *
    * @param t object
    * @param ignoreNull ignoreNull ignores null values
    * @return number of rows of the operation
    */
   protected Integer insert(T t, Boolean ignoreNull) {
      String table = getTableName(t);

      List<Field> filterField = getField(t, ignoreNull);

      List<String> columnList = getColumns(filterField);

      String columns = StrUtil.join(Const.SEPARATOR_COMMA, columnList);

      Constructs a placeholder
      String params = StrUtil.repeatAndJoin("?", columnList.size(), Const.SEPARATOR_COMMA);

      Construct the value
      Object[] values = filterField.stream().map(field -> ReflectUtil.getFieldValue(t, field)).toArray();

      String sql = StrUtil.format("INSERT INTO {table} ({columns}) VALUES ({params})", Dict.create().set("table", table).set("columns", columns).set("params", params));
      log.debug("[execute SQL]SQL:{}", sql);
      log.debug("[execute SQL] parameter:{}", JSONUtil.toJsonStr(values));
      return jdbcTemplate.update(sql, values);
   }

   /**
    * Universal is deleted based on primary key
    *
    * @param pk primary key
    * @return Affects the number of rows
    */
   protected Integer deleteById(P pk) {
      String tableName = getTableName();
      String sql = StrUtil.format("DELETE FROM {table} where id = ?", Dict.create().set("table", tableName));
      log.debug("[execute SQL]SQL:{}", sql);
      log.debug("[execute SQL] parameter:{}", JSONUtil.toJsonStr(pk));
      return jdbcTemplate.update(sql, pk);
   }

   /**
    * Generally updated according to the primary key, self-incremented columns need to add {@link Pk} annotations
    *
    * @param t object
    * @param pk primary key
    * @param ignoreNull ignoreNull ignores null values
    * @return number of rows of the operation
    */
   protected Integer updateById(T t, P pk, Boolean ignoreNull) {
      String tableName = getTableName(t);

      List<Field> filterField = getField(t, ignoreNull);

      List<String> columnList = getColumns(filterField);

      List<String> columns = columnList.stream().map(s -> StrUtil.appendIfMissing(s, " = ?")). collect(Collectors.toList());
      String params = StrUtil.join(Const.SEPARATOR_COMMA, columns);

      Construct the value
      List<Object> valueList = filterField.stream().map(field -> ReflectUtil.getFieldValue(t, field)).collect(Collectors.toList());
      valueList.add(pk);

      Object[] values = ArrayUtil.toArray(valueList, Object.class);

      String sql = StrUtil.format("UPDATE {table} SET {params} where id = ?", Dict.create().set("table", tableName).set("params", params));
      log.debug("[execute SQL]SQL:{}", sql);
      log.debug("[execute SQL] parameter:{}", JSONUtil.toJsonStr(values));
      return jdbcTemplate.update(sql, values);
   }

   /**
    * Universal query for single records based on primary key
    *
    * @param pk primary key
    * @return Single record
    */
   public T findOneById(P pk) {
      String tableName = getTableName();
      String sql = StrUtil.format("SELECT * FROM {table} where id = ?", Dict.create().set("table", tableName));
      RowMapper<T> rowMapper = new BeanPropertyRowMapper<>(clazz);
      log.debug("[execute SQL]SQL:{}", sql);
      log.debug("[execute SQL] parameter:{}", JSONUtil.toJsonStr(pk));
      return jdbcTemplate.queryForObject(sql, new Object[]{pk}, rowMapper);
   }

   /**
    * Query based on object
    *
    * @param t query criteria
    * @return Object list
    */
   public List<T> findByExample(T t) {
      String tableName = getTableName(t);
      List<Field> filterField = getField(t, true);
      List<String> columnList = getColumns(filterField);

      List<String> columns = columnList.stream().map(s -> " and " + s + " = ? "). collect(Collectors.toList());

      String where = StrUtil.join(" ", columns);
      Construct the value
      Object[] values = filterField.stream().map(field -> ReflectUtil.getFieldValue(t, field)).toArray();

      String sql = StrUtil.format("SELECT * FROM {table} where 1=1 {where}", Dict.create().set("table", tableName).set("where", StrUtil.isBlank(where) ? "" : where));
      log.debug("[execute SQL]SQL:{}", sql);
      log.debug("[execute SQL] parameter:{}", JSONUtil.toJsonStr(values));
      List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql, values);
      List<T> ret = CollUtil.newArrayList();
      maps.forEach(map -> ret.add(BeanUtil.fillBeanWithMap(map, ReflectUtil.newInstance(clazz), true, false)));
      return ret;
   }

   /**
    * Get the table name
    *
    * @param t object
    * @return Table name
    */
   private String getTableName(T t) {
      Table tableAnnotation = t.getClass().getAnnotation(Table.class);
      if (ObjectUtil.isNotNull(tableAnnotation)) {
         return StrUtil.format("`{}`", tableAnnotation.name());
      } else {
         return StrUtil.format("`{}`", t.getClass().getName().toLowerCase());
      }
   }

   /**
    * Get the table name
    *
    * @return Table name
    */
   private String getTableName() {
      Table tableAnnotation = clazz.getAnnotation(Table.class);
      if (ObjectUtil.isNotNull(tableAnnotation)) {
         return StrUtil.format("`{}`", tableAnnotation.name());
      } else {
         return StrUtil.format("`{}`", clazz.getName().toLowerCase());
      }
   }

   /**
    * Get columns
    *
    * @param fieldList field list
    * @return column information list
    */
   private List<String> getColumns(List<Field> fieldList) {
      Construct the column
      List<String> columnList = CollUtil.newArrayList();
      for (Field field : fieldList) {
         Column columnAnnotation = field.getAnnotation(Column.class);
         String columnName;
         if (ObjectUtil.isNotNull(columnAnnotation)) {
            columnName = columnAnnotation.name();
         } else {
            columnName = field.getName();
         }
         columnList.add(StrUtil.format("`{}`", columnName));
      }
      return columnList;
   }

   /**
    * Get a list of fields {@code filter fields that do not exist in the database, as well as self-incrementing columns}
    *
    * @param t object
    * @param ignoreNull ignoreNull ignores null values
    * @return Field list
    */
   private List<Field> getField(T t, Boolean ignoreNull) {
      Gets all fields, including the fields in the parent class
      Field[] fields = ReflectUtil.getFields(t.getClass());

      Filter fields that do not exist in the database, as well as self-incrementing columns
      List<Field> filterField;
      Stream<Field> fieldStream = CollUtil.toList(fields).stream().filter(field -> ObjectUtil.isNull(field.getAnnotation(Ignore.class)) || ObjectUtil.isNull(field.getAnnotation(Pk.class)));

      Whether to filter fields with a null field value
      if (ignoreNull) {
         filterField = fieldStream.filter(field -> ObjectUtil.isNotNull(ReflectUtil.getFieldValue(t, field))).collect(Collectors.toList());
      } else {
         filterField = fieldStream.collect(Collectors.toList());
      }
      return filterField;
   }

}
```

## application.yml

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
    type: com.zaxxer.hikari.HikariDataSource
    initialization-mode: always
    continue-on-error: true
    schema:
    - "classpath:db/schema.sql"
    data:
    - "classpath:db/data.sql"
    hikari:
      minimum-idle: 5
      connection-test-query: SELECT 1 FROM DUAL
      maximum-pool-size: 20
      auto-commit: true
      idle-timeout: 30000
      pool-name: SpringBootDemoHikariCP
      max-lifetime: 60000
      connection-timeout: 30000
logging:
  level:
    com.xkcoding: debug
```

## Notes

For the rest of the detailed code, see demo
