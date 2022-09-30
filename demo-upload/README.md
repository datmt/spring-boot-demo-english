# spring-boot-demo-upload

> This demo demonstrates how Spring Boot can upload files locally and how to upload files to the Qiniu Cloud platform. The front-end uses vue and iview to implement uploading pages.

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>spring-boot-demo-upload</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>spring-boot-demo-upload</name>
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
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
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
            <groupId>com.qiniu</groupId>
            <artifactId>qiniu-java-sdk</artifactId>
            <version>[7.2.0, 7.2.99]</version>
        </dependency>
    </dependencies>

    <build>
        <finalName>spring-boot-demo-upload</finalName>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

## UploadConfig.java

```java
/**
 * <p>
 * Upload configuration
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-10-23 14:09
 */
@Configuration
@ConditionalOnClass({Servlet.class, StandardServletMultipartResolver.class, MultipartConfigElement.class})
@ConditionalOnProperty(prefix = "spring.http.multipart", name = "enabled", matchIfMissing = true)
@EnableConfigurationProperties(MultipartProperties.class)
public class UploadConfig {
   @Value("${qiniu.accessKey}")
   private String accessKey;

   @Value("${qiniu.secretKey}")
   private String secretKey;

   private final MultipartProperties multipartProperties;

   @Autowired
   public UploadConfig(MultipartProperties multipartProperties) {
      this.multipartProperties = multipartProperties;
   }

   /**
    * Upload configuration
    */
   @Bean
   @ConditionalOnMissingBean
   public MultipartConfigElement multipartConfigElement() {
      return this.multipartProperties.createMultipartConfig();
   }

   /**
    * Register the parser
    */
   @Bean(name = DispatcherServlet.MULTIPART_RESOLVER_BEAN_NAME)
   @ConditionalOnMissingBean(MultipartResolver.class)
   public StandardServletMultipartResolver multipartResolver() {
      StandardServletMultipartResolver multipartResolver = new StandardServletMultipartResolver();
      multipartResolver.setResolveLazily(this.multipartProperties.isResolveLazily());
      return multipartResolver;
   }

   /**
    * East China computer room
    */
   @Bean
   public com.qiniu.storage.Configuration qiniuConfig() {
      return new com.qiniu.storage.Configuration(Zone.zone0());
   }

   /**
    * Build an instance of the Seven Bulls Upload Tool
    */
   @Bean
   public UploadManager uploadManager() {
      return new UploadManager(qiniuConfig());
   }

   /**
    * Authentication information instance
    */
   @Bean
   public Auth auth() {
      return Auth.create(accessKey, secretKey);
   }

   /**
    * Build a Seven Bulls Space Management instance
    */
   @Bean
   public BucketManager bucketManager() {
      return new BucketManager(auth(), qiniuConfig());
   }
}
```

## UploadController.java

```java
/**
 * <p>
 * File upload Controller
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-06 16:33
 */
@RestController
@Slf4j
@RequestMapping("/upload")
public class UploadController {
   @Value("${spring.servlet.multipart.location}")
   private String fileTempPath;

   @Value("${qiniu.prefix}")
   private String prefix;

   private final IQiNiuService qiNiuService;

   @Autowired
   public UploadController(IQiNiuService qiNiuService) {
      this.qiNiuService = qiNiuService;
   }

   @PostMapping(value = "/local", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
   public Dict local(@RequestParam("file") MultipartFile file) {
      if (file.isEmpty()) {
         return Dict.create().set("code", 400).set("message", "file content is empty");
      }
      String fileName = file.getOriginalFilename();
      String rawFileName = StrUtil.subBefore(fileName, ".", true);
      String fileType = StrUtil.subAfter(fileName, ".", true);
      String localFilePath = StrUtil.appendIfMissing(fileTempPath, "/") + rawFileName + "-" + DateUtil.current(false) + "." + fileType;
      try {
         file.transferTo(new File(localFilePath));
      } catch (IOException e) {
         log.error("[File uploaded to local] failed, absolute path: {}", localFilePath);
         return Dict.create().set("code", 500).set("message", "File upload failed");
      }

      log.info ("[File uploaded to local] absolute path: {}", localFilePath);
      return Dict.create().set("code", 200).set("message", "upload successful").set("data", Dict.create().set("fileName", fileName).set("filePath", localFilePath));
   }

   @PostMapping(value = "/yun", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
   public Dict yun(@RequestParam("file") MultipartFile file) {
      if (file.isEmpty()) {
         return Dict.create().set("code", 400).set("message", "file content is empty");
      }
      String fileName = file.getOriginalFilename();
      String rawFileName = StrUtil.subBefore(fileName, ".", true);
      String fileType = StrUtil.subAfter(fileName, ".", true);
      String localFilePath = StrUtil.appendIfMissing(fileTempPath, "/") + rawFileName + "-" + DateUtil.current(false) + "." + fileType;
      try {
         file.transferTo(new File(localFilePath));
         Response response = qiNiuService.uploadFile(new File(localFilePath));
         if (response.isOK()) {
            JSONObject jsonObject = JSONUtil.parseObj(response.bodyString());

            String yunFileName = jsonObject.getStr("key");
            String yunFilePath = StrUtil.appendIfMissing(prefix, "/") + yunFileName;

            FileUtil.del(new File(localFilePath));

            log.info ("[File uploaded to Qiniu Yun] absolute path: {}", yunFilePath);
            return Dict.create().set("code", 200).set("message", "upload successful").set("data", Dict.create().set("fileName", yunFileName).set("filePath", yunFilePath));
         } else {
            log.error("[File uploaded to Qiniu Yun] failed,{}", JSONUtil.toJsonStr(response));
            FileUtil.del(new File(localFilePath));
            return Dict.create().set("code", 500).set("message", "File upload failed");
         }
      } catch (IOException e) {
         log.error("[File uploaded to Qiniu Cloud] failed, absolute path: {}", localFilePath);
         return Dict.create().set("code", 500).set("message", "File upload failed");
      }
   }
}
```

## QiNiuServiceImpl.java

```java
/**
 * <p>
 * Seven Cow Cloud Upload Service
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-06 17:22
 */
@Service
@Slf4j
public class QiNiuServiceImpl implements IQiNiuService, InitializingBean {
   private final UploadManager uploadManager;

   private final Auth auth;

   @Value("${qiniu.bucket}")
   private String bucket;

   private StringMap putPolicy;

   @Autowired
   public QiNiuServiceImpl(UploadManager uploadManager, Auth auth) {
      this.uploadManager = uploadManager;
      this.auth = auth;
   }

   /**
    * Seven Cow Cloud upload files
    *
    * @param file file
    * @return Seven Bulls upload Response
    * @throws QiniuException Seven Bulls Exception
    */
   @Override
   public Response uploadFile(File file) throws QiniuException {
      Response response = this.uploadManager.put(file, file.getName(), getUploadToken());
      int retry = 0;
      while (response.needRetry() && retry < 3) {
         response = this.uploadManager.put(file, file.getName(), getUploadToken());
         retry++;
      }
      return response;
   }

   @Override
   public void afterPropertiesSet() {
      this.putPolicy = new StringMap();
      putPolicy.put("returnBody", "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"bucket\":\"$(bucket)\",\"width\":$(imageInfo.width), \"height\":${imageInfo.height}}");
   }

   /**
    * Get upload credentials
    *
    * @return Upload credentials
    */
   private String getUploadToken() {
      return this.auth.uploadToken(bucket, null, 3600, putPolicy);
   }
}
```

## index.html

```html
<!doctype html>
<html lang="en">
<head>
   <meta charset="UTF-8">
   <meta name="viewport"
         content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
   <meta http-equiv="X-UA-Compatible" content="ie=edge">
   <title>spring-boot-demo-upload</title>
   <!-- import Vue.js -->
   <script src="https://cdn.bootcss.com/vue/2.5.17/vue.min.js"></script>
   <!-- import stylesheet -->
   <link href="https://cdn.bootcss.com/iview/3.1.4/styles/iview.css" rel="stylesheet">
   <!-- import iView -->
   <script src="https://cdn.bootcss.com/iview/3.1.4/iview.min.js"></script>
</head>
<body>
<div id="app">
   <Row :gutter="16" style="background:#eee;padding:10%">
      <i-col span="12">
         <Card style="height: 300px">
            <p slot="title">
               <Icon type="ios-cloud-upload"></Icon>
               Upload locally
            </p>
            <div style="text-align: center;">
               <Upload
                     :before-upload="handleLocalUpload"
                     action="/demo/upload/local"
                     ref="localUploadRef"
                     :on-success="handleLocalSuccess"
                     :on-error="handleLocalError"
               >
                  <i-button icon="ios-cloud-upload-outline"> select the file</i-button>
               </Upload>
               <i-button
                     type="primary"
                     @click="localUpload"
                     :loading="local.loadingStatus"
                     :disabled="!local.file">
                  {{ local.loadingStatus ? 'Local file upload' : 'Local upload' }}
               </i-button>
            </div>
            <div>
               <div v-if="local.log.status != 0"> status: {{local.log.message}}</div>
               <div v-if="local.log.status === 200">filename:{{local.log.fileName}}</div>
               <div v-if="local.log.status === 200">file path: {{local.log.filePath}}</div>
            </div>
         </Card>
      </i-col>
      <i-col span="12">
         <Card style="height: 300px;">
            <p slot="title">
               <Icon type="md-cloud-upload"></Icon>
               Seven cow cloud upload
            </p>
            <div style="text-align: center;">
               <Upload
                     :before-upload="handleYunUpload"
                     action="/demo/upload/yun"
                     ref="yunUploadRef"
                     :on-success="handleYunSuccess"
                     :on-error="handleYunError"
               >
                  <i-button icon="ios-cloud-upload-outline"> select the file</i-button>
               </Upload>
               <i-button
                     type="primary"
                     @click="yunUpload"
                     :loading="yun.loadingStatus"
                     :disabled="!yun.file">
                  {{ yun.loadingStatus ? 'Seven Cow Cloud File Upload' : 'Seven Cow Cloud Upload' }}
               </i-button>
            </div>
            <div>
               <div v-if="yun.log.status != 0"> status: {{yun.log.message}}</div>
               <div v-if="yun.log.status === 200">filename:{{yun.log.fileName}}</div>
               <div v-if="yun.log.status === 200"> file path: {{yun.log.filePath}}</div>
            </div>
         </Card>
      </i-col>
   </Row>
</div>
<script>
   new Vue({
      el: '#app',
      data: {
         local: {
            After selecting the file, save the file returned by beforeUpload here, which will be used later
            file: null,
            Mark the upload status
            loadingStatus: false,
            log: {
               status: 0,
               message: "",
               fileName: "",
               filePath: ""
            }
         },
         yun: {
            After selecting the file, save the file returned by beforeUpload here, which will be used later
            file: null,
            Mark the upload status
            loadingStatus: false,
            log: {
               status: 0,
               message: "",
               fileName: "",
               filePath: ""
            }
         }
      },
      methods: {
         beforeUpload will stop automatic upload when false or promise is returned, here we will select the good file file in data and return false
         handleLocalUpload(file) {
            this.local.file = file;
            return false;
         },
         Here is a manual upload, get the Upload instance through $refs, and then call the private method .post() to upload the file saved in data.
         The Upload component of iView continues to upload when it calls the .post() method.
         localUpload() {
            this.local.loadingStatus = true;  Mark the upload status
            this.$refs.localUploadRef.post(this.local.file);
         },
         After the upload is successful, clear the file in data and modify the upload status
         handleLocalSuccess(response) {
            this.local.file = null;
            this.local.loadingStatus = false;
            if (response.code === 200) {
               this.$Message.success(response.message);
               this.local.log.status = response.code;
               this.local.log.message = response.message;
               this.local.log.fileName = response.data.fileName;
               this.local.log.filePath = response.data.filePath;
               this.$refs.localUploadRef.clearFiles();
            } else {
               this.$Message.error(response.message);
               this.local.log.status = response.code;
               this.local.log.message = response.message;
            }
         },
         After the upload fails, empty the file in data and modify the upload status
         handleLocalError() {
            this.local.file = null;
            this.local.loadingStatus = false;
            this.$Message.error;
         },
         beforeUpload will stop automatic upload when false or promise is returned, here we will select the good file file in data and return false
         handleYunUpload(file) {
            this.yun.file = file;
            return false;
         },
         Here is a manual upload, get the Upload instance through $refs, and then call the private method .post() to upload the file saved in data.
         The Upload component of iView continues to upload when it calls the .post() method.
         yunUpload() {
            this.yun.loadingStatus = true;  Mark the upload status
            this.$refs.yunUploadRef.post(this.yun.file);
         },
         After the upload is successful, clear the file in data and modify the upload status
         handleYunSuccess(response) {
            this.yun.file = null;
            this.yun.loadingStatus = false;
            if (response.code === 200) {
               this.$Message.success(response.message);
               this.yun.log.status = response.code;
               this.yun.log.message = response.message;
               this.yun.log.fileName = response.data.fileName;
               this.yun.log.filePath = response.data.filePath;
               this.$refs.yunUploadRef.clearFiles();
            } else {
               this.$Message.error(response.message);
               this.yun.log.status = response.code;
               this.yun.log.message = response.message;
            }
         },
         After the upload fails, empty the file in data and modify the upload status
         handleYunError() {
            this.yun.file = null;
            this.yun.loadingStatus = false;
            this.$Message.error;
         }
      }
   })
</script>
</body>
</html>
```

## Reference

1. Spring Official Documentation: https://docs.spring.io/spring-boot/docs/2.1.0.RELEASE/reference/htmlsingle/#howto-multipart-file-upload-configuration
2. Qiniu Cloud Official Document: https://developer.qiniu.com/kodo/sdk/1239/java#5

