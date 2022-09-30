# spring-boot-demo-social

> This demo mainly demonstrates how the Spring Boot project can use **[the most comprehensive third-party login tool in history - JustAuth] (https://github.com/zhangyd-c/JustAuth)** to implement third-party login, including QQ login, GitHub login, WeChat login, Google login, Microsoft login, Xiaomi login, enterprise WeChat login.
>
> Quick integration with [justauth-spring-boot-starter] (https://search.maven.org/artifact/com.xkcoding/justauth-spring-boot-starter).
>
> JustAuth, as you can see, it's just a *tool class library** for **third-party authorized logins**, which allows us to get rid of the cumbersome third-party login SDK and make login so easy!**
>
> 1. **Full**: More than ten third-party platforms have been integrated (the basic commonly used at home and abroad has been included), and there are still expansion plans in the future!
>2. **Jane**: The API is designed in the simplest way (see ['Quick Start'] (https://github.com/zhangyd-c/JustAuth#%E5%BF%AB%E9%80%9F%E5%BC%80%E5%A7%8B)) below), so that you can use it without a sense of hindrance!
>
>PS: I am very fortunate to participate in the development of this SDK, mainly developed **QQ login, WeChat login, Xiaomi login, Microsoft login, Google login** ** **5'** third-party login, as well as some BUG repair work. Thanks again to [@she-wolf] (https://github.com/zhangyd-c) for opening up this easy-to-use and comprehensive third-party login SDK.

If the technology selection is 'JFinal', check out this [**'demo'**] (https://github.com/xkcoding/jfinal-justauth-demo)

https://github.com/xkcoding/jfinal-justauth-demo

If the technology selection is 'ActFramework', check out this [**'demo'**](https://github.com/xkcoding/act-justauth-demo)

https://github.com/xkcoding/act-justauth-demo

## 1. Environment preparation

### 1.1. Prepare the public network server

First prepare a server with a public IP address, you can choose Alibaba Cloud or Tencent Cloud, if you choose to use Alibaba Cloud, you can use my [discount link] (https://chuangke.aliyun.com/invite?userCode=r8z5amhr) to buy.

### 1.2. Intranet penetration frp construction

> frp installer: https://github.com/fatedier/frp/releases

#### 1.2.1. frp server side construction

The server is built on the public network server prepared in the previous step, because the server is a centos7 x64 system, so the installation package version of [linux_amd64.27.0_linux_amd64 frp_0 downloaded here is [.tar.gz.27.0_linux_amd64 ( https://github.com/fatedier/frp/releases/download/v0.27.0/frp_0.27.0_linux_amd64.tar.gz) 。

1. Download the installation package

   ```shell
   $ wget https://github.com/fatedier/frp/releases/download/v0.27.0/frp_0.27.0_linux_amd64.tar.gz
   ```

2. Unzip the installation package

   ```shell
   $ tar -zxvf frp_0.27.0_linux_amd64.tar.gz
   ```

3. Modify the configuration file

   ```shell
   $ cd frp_0.27.0_linux_amd64
   $ vim frps.ini

   [common]
   bind_port = 7100
   vhost_http_port = 7200
   ```

4. Start the frp server

   ```shell
   $ ./frps -c frps.ini
   2019/06/15 16:42:02 [I] [service.go:139] frps tcp listen on 0.0.0.0:7100
   2019/06/15 16:42:02 [I] [service.go:181] http service listen on 0.0.0.0:7200
   2019/06/15 16:42:02 [I] [root.go:204] Start frps success
   ```

#### 1.2.2. frp client build

The client is built on a local Mac, so download the installation package version of [darwin_amd64.27.0_darwin_amd64 frp_0] (.tar.gz) https://github.com/fatedier/frp/releases/download/v0.27.0/frp_0.27.0_darwin_amd64.tar.gz.

1. Download the installation package

   ```shell
   $ wget https://github.com/fatedier/frp/releases/download/v0.27.0/frp_0.27.0_darwin_amd64.tar.gz
   ```

2. Unzip the installation package

   ```shell
   $ tar -zxvf frp_0.27.0_darwin_amd64.tar.gz
   ```

3. Modify the configuration file and configure the IP port of the server and the domain name information of the listener

   ```shell
   $ cd frp_0.27.0_darwin_amd64
   $ vim frpc.ini

   [common]
   server_addr = 120.92.169.103
   server_port = 7100

   [web]
   type = http
   local_port = 8080
   custom_domains = oauth.xkcoding.com
   ```

4. Start the frp client

   ```shell
   $ ./frpc -c frpc.ini
   2019/06/15 16:48:52 [I] [service.go:221] login to server success, get run id [8bb83bae5c58afe6], server udp port [0]
   2019/06/15 16:48:52 [I] [proxy_manager.go:137] [8bb83bae5c58afe6] proxy added: [web]
   2019/06/15 16:48:52 [I] [control.go:144] [web] start proxy success
   ```

### 1.3. Configure domain name resolution

Go to Alibaba Cloud DNS Resolution and resolve the domain name to our public network server, for example, mine will be 'oauth.xkcoding.com -> 120.92.169.103'

! [image-20190615165843639] (http://static.xkcoding.com/spring-boot-demo/social/063923.jpg)

### 1.4. nginx proxy

The building of nginx will not be repeated here, only the configuration

```nginx
server {
    listen       80;
    server_name  oauth.xkcoding.com;

    location / {
        proxy_pass http://127.0.0.1:7200;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header Host $http_host;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header   X-Real-IP        $remote_addr;
        proxy_buffering off;
        sendfile off;
        proxy_max_temp_file_size 0;
        client_max_body_size       10m;
        client_body_buffer_size    128k;
        proxy_connect_timeout      90;
        proxy_send_timeout         90;
        proxy_read_timeout         90;
        proxy_temp_file_write_size 64k;
        proxy_http_version 1.1;
        proxy_request_buffering off;
    }
}
```

Test the configuration file for problems

```shell
$ nginx -t
nginx: the configuration file /etc/nginx/nginx.conf syntax is ok
nginx: configuration file /etc/nginx/nginx.conf test is successful
```

Reload the configuration file so that it takes effect

```shell
$ nginx -s reload
```

> Now when we type 'oauth.xkcoding.com' in the browser, the network traffic actually goes through the following steps:
>
> 1. Through the DNS domain name resolution previously installed, you will access port 80 of our public network server '120.92.169.103'
> 2. After going through nginx, the proxy is on port 7200 locally
> 3. Then FRP penetrates into port 8080 of our Mac computer
> 4. At this point, 8080 is our application port

### 1.5. Third-party platform application

#### 1.5.1. QQ interconnection platform application

1. Go to https://connect.qq.com/
2. Apply for a developer
3. Application Management - > Add a website application and wait for the approval to pass

! [image-20190617144655429] (http://static.xkcoding.com/spring-boot-demo/social/063921-1.jpg)

#### 1.5.2. GitHub platform application

1. Go to https://github.com/settings/developers
2. Click the 'New OAuth App' button to create the app

! [image-20190617145839851] (http://static.xkcoding.com/spring-boot-demo/social/063919.jpg)

#### 1.5.3 WeChat Open Platform Application

Here the WeChat open platform needs to use enterprises, individuals do not have qualifications, so I rented a month's qualification in a treasure, and what I need can [poke me to rent] (https://item.taobao.com/item.htm?spm=2013.1.w4023-5034755838.13.747a61a7ccfHwS&id=554942413474)

> Disclaimer: I have no interest in the store, purely because I personally feel that it is good to use and share
>
> There are two ways to do this:
>
> 1. Store support to help you through the enterprise qualifications, here is your own open platform number is good
> 2. Temporary use can ask the store to rent for a month for development, after renting here, the store will send you the AppID and AppSecret information, and you provide the callback domain

Therefore, here I will post an address for authorization callbacks for reference.

! [image-20190617153552218] (http://static.xkcoding.com/spring-boot-demo/social/063927-1.jpg)

#### 1.5.4. Google Open Platform Application

1. Go to https://console.developers.google.com/projectcreate to create a project
2. Go to https://console.developers.google.com/apis/credentials and under the project you created in the first step, add an app

! [image-20190617151119584] (http://static.xkcoding.com/spring-boot-demo/social/063920.jpg)

! [image-20190617150903039] (http://static.xkcoding.com/spring-boot-demo/social/063922.jpg)

#### 1.5.5. Microsoft Open Platform Application

1. Go to https://portal.azure.com/#blade/Microsoft_AAD_RegisteredApps/ApplicationsListBlade to register the app
2. When registering the application, you need to fill in the callback address, of course, you can also modify it later

! [image-20190617152529449] (http://static.xkcoding.com/spring-boot-demo/social/063921.jpg)

3. The client id is here

! [image-20190617152805581] (http://static.xkcoding.com/spring-boot-demo/social/063927.jpg)

4. The client secret needs to be generated here by itself

! [image-20190617152711938] (http://static.xkcoding.com/spring-boot-demo/social/063924.jpg)

#### 1.5.6. Xiaomi open platform application

1. Apply for Xiaomi developer and pass the review
2. Go to https://dev.mi.com/passport/oauth2/applist Add oAUTH app and select 'Create Web App'
3. After filling in the basic information, go to the application information page and fill in the 'callback address'

! [image-20190617151502414] (http://static.xkcoding.com/spring-boot-demo/social/063924-1.jpg)

4. After the application is approved, you can view the AppKey and AppSecret in the 'Application Details' of the application information page, and the review speed of the Xiaomi application is particularly slow, and you need to wait patiently...

! [image-20190617151624603] (http://static.xkcoding.com/spring-boot-demo/social/063926.jpg)

#### 1.5.7. Enterprise WeChat platform application

> Reference: https://xkcoding.com/2019/08/06/use-justauth-integration-wechat-enterprise.html

## 2. Primary code

> this demo uses Redis caching state, so prepare your Redis environment, if you don't have a Redis environment, you can configure the cache of configuration files
>
> ```yaml
> justauth:
>   cache:
>     type: default
> ```

### 2.1. pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <artifactId>spring-boot-demo-social</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <packaging>jar</packaging>

  <name>spring-boot-demo-social</name>
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
    <justauth-spring-boot.version>1.1.0</justauth-spring-boot.version>
  </properties>

  <dependencies>
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
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>

    <!-- object pool, --> must be introduced when using redis
    <dependency>
      <groupId>org.apache.commons</groupId>
      <artifactId>commons-pool2</artifactId>
    </dependency>

    <!-- oauth tool class -->
    <dependency>
      <groupId>com.xkcoding</groupId>
      <artifactId>justauth-spring-boot-starter</artifactId>
      <version>${justauth-spring-boot.version}</version>
    </dependency>

    <dependency>
      <groupId>org.projectlombok</groupId>
      <artifactId>lombok</artifactId>
      <optional>true</optional>
    </dependency>

    <dependency>
      <groupId>com.google.guava</groupId>
      <artifactId>guava</artifactId>
    </dependency>

    <dependency>
      <groupId>cn.hutool</groupId>
      <artifactId>hutool-all</artifactId>
    </dependency>
  </dependencies>

  <build>
    <finalName>spring-boot-demo-social</finalName>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
      </plugin>
    </plugins>
  </build>

</project>
```

### 2.2. application.yml

```yaml
server:
  port: 8080
  servlet:
    context-path: /demo

spring:
  redis:
    host: localhost
    # Connection timeout (remember to add units, Duration)
    timeout: 10000ms
    # Redis has 16 shards by default, and here you configure the specific shards used
    # database: 0
    lettuce:
      pool:
        # The maximum number of connections in the connection pool (using a negative value to indicate no limit) defaults to 8
        max-active: 8
        # Connection pool maximum blocking wait time (using a negative value to indicate no limit) Default -1
        max-wait: -1ms
        # Maximum idle connections in connection pool Default 8
        max-idle: 8
        # Minimum idle connections in connection pool Default 0
        min-idle: 0
  cache:
    # Generally speaking, it is not configured, and Spring Cache will assemble itself according to the dependent packages
    type: redis

justauth:
  enabled: true
  type:
    qq:
      client-id: 10******85
      client-secret: 1f7d************************d629e
      redirect-uri: http://oauth.xkcoding.com/demo/oauth/qq/callback
    github:
      client-id: 2d25******d5f01086
      client-secret: 5a2919b************************d7871306d1
      redirect-uri: http://oauth.xkcoding.com/demo/oauth/github/callback
    wechat:
      client-id: wxdcb******4ff4
      client-secret: b4e9dc************************a08ed6d
      redirect-uri: http://oauth.xkcoding.com/demo/oauth/wechat/callback
    google:
      client-id: 716******17-6db******vh******ttj320i******userco******t.com
      client-secret: 9IBorn************7-E
      redirect-uri: http://oauth.xkcoding.com/demo/oauth/google/callback
    microsoft:
      client-id: 7bdce8******************e194ad76c1b
      client-secret: Iu0zZ4************************tl9PWan_.
      redirect-uri: https://oauth.xkcoding.com/demo/oauth/microsoft/callback
    mi:
      client-id: 288************2994
      client-secret: nFeTt89************************==
      redirect-uri: http://oauth.xkcoding.com/demo/oauth/mi/callback
    wechat_enterprise:
      client-id: ww58******f3************fbc
      client-secret: 8G6PCr00j************************rgk************AyzaPc78
      redirect-uri: http://oauth.xkcoding.com/demo/oauth/wechat_enterprise/callback
      agent-id: 1*******2
  cache:
    type: redis
    prefix: 'SOCIAL::STATE::'
    timeout: 1h
```

### 2.3. OauthController.java

```java
/**
 * <p>
 * A third party logs in to the Controller
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-05-17 10:07
 */
@Slf4j
@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class OauthController {
    private final AuthRequestFactory factory;

    /**
     * Login type
     */
    @GetMapping
    public Map<String, String> loginType() {
        List<String> oauthList = factory.oauthList();
        return oauthList.stream().collect(Collectors.toMap(oauth -> oauth.toLowerCase() + "Login", oauth -> "http://oauth.xkcoding.com/demo/oauth/login/" + oauth.toLowerCase()));
    }

    /**
     * Login
     *
     * @param oauthType third-party login type
     * @param response  response
     * @throws IOException
     */
    @RequestMapping("/login/{oauthType}")
    public void renderAuth(@PathVariable String oauthType, HttpServletResponse response) throws IOException {
        AuthRequest authRequest = factory.get(getAuthSource(oauthType));
        response.sendRedirect(authRequest.authorize(oauthType + "::" + AuthStateUtils.createState()));
    }

    /**
     * Callback after successful login
     *
     * @param oauthType third-party login type
     * @param callback carries back the information
     * @return Information after successful login
     */
    @RequestMapping("/{oauthType}/callback")
    public AuthResponse login(@PathVariable String oauthType, AuthCallback callback) {
        AuthRequest authRequest = factory.get(getAuthSource(oauthType));
        AuthResponse response = authRequest.login(callback);
        log.info("【response】= {}", JSONUtil.toJsonStr(response));
        return response;
    }

    private AuthSource getAuthSource(String type) {
        if (StrUtil.isNotBlank(type)) {
            return AuthSource.valueOf(type.toUpperCase());
        } else {
            throw new RuntimeException ("Unsupported Type");
        }
    }
}
```

### 2.4. If you want to customize the state cache

Please see 👉 [here] (https://github.com/justauth/justauth-spring-boot-starter#2-%E7%BC%93%E5%AD%98%E9%85%8D%E7%BD%AE)

## 3. Operates in

Open a browser, enter http://oauth.xkcoding.com/demo/oauth , and click on each login method to test yourself.

> 'Google login, it is possible that the test fails due to the strength of the home country, and solve it by itself ~' :kissing_smiling_eyes:

! [image-20190809161032422] (https://static.xkcoding.com/blog/2019-08-09-081033.png)

## Reference

1. JustAuth Project Address: https://github.com/justauth/JustAuth
2. justauth-spring-boot-starter Address: https://github.com/justauth/justauth-spring-boot-starter
3. FRP intranet penetration project address: https://github.com/fatedier/frp
4. FRP intranet penetration official Chinese document: https://github.com/fatedier/frp/blob/master/README_zh.md
5. Farp implements intranet penetration: https://zhuanlan.zhihu.com/p/45445979
6. QQ interconnection document: http://wiki.connect.qq.com/%E5%87%86%E5%A4%87%E5%B7%A5%E4%BD%9C_oauth2-0
7. WeChat Open Platform Documentation: https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419316505&token=&lang=zh_CN
8. GitHub third-party login documentation: https://developer.github.com/apps/building-oauth-apps/
9. Google Oauth2 Documentation: https://developers.google.com/identity/protocols/OpenIDConnect
10. Microsoft Oauth2 Documentation: https://docs.microsoft.com/zh-cn/graph/auth-v2-user
11. Xiaomi Open Platform Account Service Documentation: https://dev.mi.com/console/doc/detail?pId=707



