# spring-boot-demo-docker

> This demo demonstrates how to containerize a Spring Boot project. Packaged into an image by 'Dockerfile'.

## Dockerfile

```dockerfile
# Base image
FROM openjdk:8-jdk-alpine

# Author information
MAINTAINER "Yangkai.Shen 237497819@qq.com"

# Add a storage space
VOLUME /tmp

# Expose port 8080
EXPOSE 8080

# Add a variable, if you use dockerfile-maven-plugin, it will automatically replace the variable content here
ARG JAR_FILE=target/spring-boot-demo-docker.jar

# Add a jar package to the container
ADD ${JAR_FILE} app.jar

# Boot image to run the program automatically
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/urandom","-jar","/app.jar"]
```

## Packaging method

### Manual packaging

1. Go to the Dockerfile directory and open Command Line Execution

   ```bash
   $ docker build -t spring-boot-demo-docker .
   ```

2. Review the generated image

   ```bash
   $ docker images
   
   REPOSITORY                                                        TAG                 IMAGE ID            CREATED             SIZE
   spring-boot-demo-docker                                           latest	      bc29a29ffca0        2 hours ago         119MB
   openjdk                                                           8-jdk-alpine        97bc1352afde        5 weeks ago         103MB
   ```

3. Run

   ```bash
   $ docker run -d -p 9090:8080 spring-boot-demo-docker
   ```

### Packaged with maven plugins

1. Add plugins to pom.xml

2. ```xml
   <properties>
       <dockerfile-version>1.4.9</dockerfile-version>
   </properties>
   
   <plugins>      
       <plugin>
           <groupId>com.spotify</groupId>
           <artifactId>dockerfile-maven-plugin</artifactId>
           <version>${dockerfile-version}</version>
           <configuration>
               <repository>${project.build.finalName}</repository>
               <tag>${project.version}</tag>
               <buildArgs>
                   <JAR_FILE>target/${project.build.finalName}.jar</JAR_FILE>
               </buildArgs>
           </configuration>
           <executions>
               <execution>
                   <id>default</id>
                   <phase>package</phase>
                   <goals>
                       <goal>build</goal>
                   </goals>
               </execution>
           </executions>
       </plugin>
   </plugins>
   ```

2. Execute the mvn packaging command, because the 'execution' node in the plugin is configured with package, so the build command will be automatically executed when packaging.

   ```bash
   $ mvn clean package -Dmaven.test.skip=true
   ```

3. View the image

   ```bash
   $ docker images
   
   REPOSITORY                                                        TAG                 IMAGE ID            CREATED             SIZE
   spring-boot-demo-docker                                           1.0.0-SNAPSHOT      bc29a29ffca0        2 hours ago         119MB
   openjdk                                                           8-jdk-alpine        97bc1352afde        5 weeks ago         103MB
   ```

4. Run

   ```bash
   $ docker run -d -p 9090:8080 spring-boot-demo-docker:1.0.0-SNAPSHOT
   ```

## Reference

- Docker Official Documentation: https://docs.docker.com/
- Dockerfile command, reference document: https://docs.docker.com/engine/reference/builder/
- Maven plugin use, reference address: https://github.com/spotify/dockerfile-maven
