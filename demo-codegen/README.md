# spring-boot-demo-codegen

> This demo mainly demonstrates Spring Boot's use of Template Technology to generate code and provides front-end pages that generate code such as Entity/Mapper/Service/Controller.

## 1. Key features

1. Use the 'velocity' code generation
2. Code generation for mysql database is temporarily supported
3. Provide a front-end page display and download the code package

> Note: (1) use lombok in Entity to simplify code (2) Mapper and service layer integration Mybatis-Plus simplify code

## 2. run

1. Run the 'SpringBootDemoCodegenApplication' to start the project
2. Open a browser and enter http://localhost:8080/demo/index.html
3. Enter the query criteria to generate the code

## 3. Critical code

### 3.1. pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>spring-boot-demo-codegen</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>spring-boot-demo-codegen</name>
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
            <artifactId>spring-boot-starter-web</artifactId>
            <exclusions>
                <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-tomcat</artifactId>
                </exclusion>
            </exclusions>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-undertow</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>com.zaxxer</groupId>
            <artifactId>HikariCP</artifactId>
        </dependency>

        <!-- velocity code generation uses the template -->
        <dependency>
            <groupId>org.apache.velocity</groupId>
            <artifactId>velocity</artifactId>
            <version>1.7</version>
        </dependency>

        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-text</artifactId>
            <version>1.6</version>
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
        <finalName>spring-boot-demo-codegen</finalName>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

### 3.2. Code generator configuration

```properties
#代码生成器, configuration information
mainPath=com.xkcoding
#包名
package=com.xkcoding
moduleName=generator
#作者
author=Yangkai.Shen
#表前缀 (class name does not contain table prefix)
tablePrefix=tb_
#类型转换, configuration information
tinyint=Integer
smallint=Integer
mediumint=Integer
int=Integer
integer=Integer
bigint=Long
float=Float
double=Double
decimal=BigDecimal
bit=Boolean
char=String
varchar=String
tinytext=String
text=String
mediumtext=String
longtext=String
date=LocalDateTime
datetime=LocalDateTime
timestamp=LocalDateTime
```

### 3.3. CodeGenUtil.java

```java
/**
 * <p>
 * Code generator tool class
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-03-22 09:27
 */
@Slf4j
@UtilityClass
public class CodeGenUtil {

    private final String ENTITY_JAVA_VM = "Entity.java.vm";
    private final String MAPPER_JAVA_VM = "Mapper.java.vm";
    private final String SERVICE_JAVA_VM = "Service.java.vm";
    private final String SERVICE_IMPL_JAVA_VM = "ServiceImpl.java.vm";
    private final String CONTROLLER_JAVA_VM = "Controller.java.vm";
    private final String MAPPER_XML_VM = "Mapper.xml.vm";
    private final String API_JS_VM = "api.js.vm";

    private List<String> getTemplates() {
        List<String> templates = new ArrayList<>();
        templates.add("template/Entity.java.vm");
        templates.add("template/Mapper.java.vm");
        templates.add("template/Mapper.xml.vm");
        templates.add("template/Service.java.vm");
        templates.add("template/ServiceImpl.java.vm");
        templates.add("template/Controller.java.vm");

        templates.add("template/api.js.vm");
        return templates;
    }

    /**
     * Generate code
     */
    public void generatorCode(GenConfig genConfig, Entity table, List<Entity> columns, ZipOutputStream zip) {
        Configuration information
        Props props = getConfig();
        boolean hasBigDecimal = false;
        Table information
        TableEntity tableEntity = new TableEntity();
        tableEntity.setTableName(table.getStr("tableName"));

        if (StrUtil.isNotBlank(genConfig.getComments())) {
            tableEntity.setComments(genConfig.getComments());
        } else {
            tableEntity.setComments(table.getStr("tableComment"));
        }

        String tablePrefix;
        if (StrUtil.isNotBlank(genConfig.getTablePrefix())) {
            tablePrefix = genConfig.getTablePrefix();
        } else {
            tablePrefix = props.getStr("tablePrefix");
        }

        Table names are converted to Java class names
        String className = tableToJava(tableEntity.getTableName(), tablePrefix);
        tableEntity.setCaseClassName(className);
        tableEntity.setLowerClassName(StrUtil.lowerFirst(className));

        Column information
        List<ColumnEntity> columnList = Lists.newArrayList();
        for (Entity column : columns) {
            ColumnEntity columnEntity = new ColumnEntity();
            columnEntity.setColumnName(column.getStr("columnName"));
            columnEntity.setDataType(column.getStr("dataType"));
            columnEntity.setComments(column.getStr("columnComment"));
            columnEntity.setExtra(column.getStr("extra"));

            The column names are converted to Java property names
            String attrName = columnToJava(columnEntity.getColumnName());
            columnEntity.setCaseAttrName(attrName);
            columnEntity.setLowerAttrName(StrUtil.lowerFirst(attrName));

            The data type of the column, converted to Java type
            String attrType = props.getStr(columnEntity.getDataType(), "unknownType");
            columnEntity.setAttrType(attrType);
            if (!hasBigDecimal && "BigDecimal".equals(attrType)) {
                hasBigDecimal = true;
            }
            Whether the primary key
            if ("PRI".equalsIgnoreCase(column.getStr("columnKey")) && tableEntity.getPk() == null) {
                tableEntity.setPk(columnEntity);
            }

            columnList.add(columnEntity);
        }
        tableEntity.setColumns(columnList);

        If there is no primary key, the first field is the primary key
        if (tableEntity.getPk() == null) {
            tableEntity.setPk(tableEntity.getColumns().get(0));
        }

        Set the velocity resource loader
        Properties prop = new Properties();
        prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(prop);
        Encapsulate template data
        Map<String, Object> map = new HashMap<>(16);
        map.put("tableName", tableEntity.getTableName());
        map.put("pk", tableEntity.getPk());
        map.put("className", tableEntity.getCaseClassName());
        map.put("classname", tableEntity.getLowerClassName());
        map.put("pathName", tableEntity.getLowerClassName().toLowerCase());
        map.put("columns", tableEntity.getColumns());
        map.put("hasBigDecimal", hasBigDecimal);
        map.put("datetime", DateUtil.now());
        map.put("year", DateUtil.year(new Date()));

        if (StrUtil.isNotBlank(genConfig.getComments())) {
            map.put("comments", genConfig.getComments());
        } else {
            map.put("comments", tableEntity.getComments());
        }

        if (StrUtil.isNotBlank(genConfig.getAuthor())) {
            map.put("author", genConfig.getAuthor());
        } else {
            map.put("author", props.getStr("author"));
        }

        if (StrUtil.isNotBlank(genConfig.getModuleName())) {
            map.put("moduleName", genConfig.getModuleName());
        } else {
            map.put("moduleName", props.getStr("moduleName"));
        }

        if (StrUtil.isNotBlank(genConfig.getPackageName())) {
            map.put("package", genConfig.getPackageName());
            map.put("mainPath", genConfig.getPackageName());
        } else {
            map.put("package", props.getStr("package"));
            map.put("mainPath", props.getStr("mainPath"));
        }
        VelocityContext context = new VelocityContext(map);

        Gets a list of templates
        List<String> templates = getTemplates();
        for (String template : templates) {
            Render the template
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, CharsetUtil.UTF_8);
            tpl.merge(context, sw);

            try {
                Add to zip
                zip.putNextEntry(new ZipEntry(Objects.requireNonNull(getFileName(template, tableEntity.getCaseClassName(), map
                        .get("package")
                        .toString(), map.get("moduleName").toString()))));
                IoUtil.write(zip, StandardCharsets.UTF_8, false, sw.toString());
                IoUtil.close(sw);
                zip.closeEntry();
            } catch (IOException e) {
                throw new RuntimeException("Render template failed, table name:" + tableEntity.getTableName(), e);
            }
        }
    }


    /**
     * Column names are converted to Java property names
     */
    private String columnToJava(String columnName) {
        return WordUtils.capitalizeFully(columnName, new char[]{'_'}).replace("_", "");
    }

    /**
     * Table name converted to Java class name
     */
    private String tableToJava(String tableName, String tablePrefix) {
        if (StrUtil.isNotBlank(tablePrefix)) {
            tableName = tableName.replaceFirst(tablePrefix, "");
        }
        return columnToJava(tableName);
    }

    /**
     * Get configuration information
     */
    private Props getConfig() {
        Props props = new Props("generator.properties");
        props.autoLoad(true);
        return props;
    }

    /**
     * Get the file name
     */
    private String getFileName(String template, String className, String packageName, String moduleName) {
        The package path
        String packagePath = GenConstants.SIGNATURE + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator;
        The resource path
        String resourcePath = GenConstants.SIGNATURE + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator;
        APIs
        String apiPath = GenConstants.SIGNATURE + File.separator + "api" + File.separator;

        if (StrUtil.isNotBlank(packageName)) {
            packagePath += packageName.replace(".", File.separator) + File.separator + moduleName + File.separator;
        }

        if (template.contains(ENTITY_JAVA_VM)) {
            return packagePath + "entity" + File.separator + className + ".java";
        }

        if (template.contains(MAPPER_JAVA_VM)) {
            return packagePath + "mapper" + File.separator + className + "Mapper.java";
        }

        if (template.contains(SERVICE_JAVA_VM)) {
            return packagePath + "service" + File.separator + className + "Service.java";
        }

        if (template.contains(SERVICE_IMPL_JAVA_VM)) {
            return packagePath + "service" + File.separator + "impl" + File.separator + className + "ServiceImpl.java";
        }

        if (template.contains(CONTROLLER_JAVA_VM)) {
            return packagePath + "controller" + File.separator + className + "Controller.java";
        }

        if (template.contains(MAPPER_XML_VM)) {
            return resourcePath + "mapper" + File.separator + className + "Mapper.xml";
        }

        if (template.contains(API_JS_VM)) {
            return apiPath + className.toLowerCase() + ".js";
        }

        return null;
    }
}
```

### 3.4. See demo for the rest of the code

## 4. demo

<video id="video" controls="" preload="none">
      <source id="mp4" src="https://static.xkcoding.com/code/spring-boot-demo/codegen/codegen.mp4" type="video/mp4">
      <p>Your browser version is too low to support playing video presentations, you can download demo videos to watch and https://static.xkcoding.com/code/spring-boot-demo/codegen/codegen.mp4</p>
    </video>

## 5. reference

- [Open source for everyone.]  Build project _V1 automatically] ( https://qq343509740.gitee.io/2018/12/20/%E7%AC%94%E8%AE%B0/%E8%87%AA%E5%8A%A8%E6%9E%84%E5%BB%BA%E9%A1%B9%E7%9B%AE/%E5%9F%BA%E4%BA%8E%E4%BA%BA%E4%BA%BA%E5%BC%80%E6%BA%90%20%E8%87%AA%E5%8A%A8%E6%9E%84%E5%BB%BA%E9%A1%B9%E7%9B%AE_V1/)

- [Mybatis-Plus Code Generator] (https://mybatis.plus/guide/generator.html#%E6%B7%BB%E5%8A%A0%E4%BE%9D%E8%B5%96)
