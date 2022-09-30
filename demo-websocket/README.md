# spring-boot-demo-websocket

> This demo mainly demonstrates how Spring Boot integrates WebSocket to enable the backend to actively push data to the front end. Most examples of websockets on the web are chat rooms, and this example mainly pushes server status information. The front-end page is based on the vue and element-ui implementations.

## 1. code

### 1.1. pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>spring-boot-demo-websocket</artifactId>
    <version>1.0.0-SNAPSHOT</version>

    <name>spring-boot-demo-websocket</name>
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
        <oshi.version>3.9.1</oshi.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-websocket</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>com.github.oshi</groupId>
            <artifactId>oshi-core</artifactId>
            <version>${oshi.version}</version>
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
        <finalName>spring-boot-demo-websocket</finalName>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

### 1.2. WebSocketConfig.java

```java
/**
 * <p>
 * WebSocket configuration
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-14 15:58
 */
@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        Registers a /notification endpoint through which the front end connects
        registry.addEndpoint("/notification")
                Resolve cross-domain issues
                .setAllowedOrigins("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        Defines the prefix information of a client subscription address, that is, the prefix information of the message sent by the client receiving the server
        registry.enableSimpleBroker("/topic");
    }

}
```

### 1.3. Server-related entities

> This part of the entity See package path [com.xkcoding.websocket.model](./src/main/java/com/xkcoding/websocket/model)

### 1.4. ServerTask.java

```java
/**
 * <p>
 * Server timing push task
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-14 16:04
 */
@Slf4j
@Component
public class ServerTask {
    @Autowired
    private SimpMessagingTemplate wsTemplate;

    /**
     * Performed every 2s in standard time
     */
    @Scheduled(cron = "0/2 * * * * ?")
    public void websocket() throws Exception {
        log.info ("[push message] start execution :{}", DateUtil.formatDateTime(new Date()));
        Query the server status
        Server server = new Server();
        server.copyTo();
        ServerVO serverVO = ServerUtil.wrapServerVO(server);
        Dict dict = ServerUtil.wrapServerDict(serverVO);
        wsTemplate.convertAndSend(WebSocketConsts.PUSH_SERVER, JSONUtil.toJsonStr(dict));
        log.info ("[push message] execution end:{}", DateUtil.formatDateTime(new Date()));
    }
}
```

### 1.5. server.html

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Server information</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/element-ui/2.4.11/theme-chalk/index.css" rel="stylesheet">
    <style>
        .el-row {
            margin-bottom: 20px;
        }

        .el-row:last-child {
            margin-bottom: 0;
        }

        .sysFile {
            margin-bottom: 5px;
        }

        .sysFile:last-child {
            margin-bottom: 0;
        }
    </style>
</head>
<body>
<div id="app">
    <el-container>
        <el-header>
            <el-button @click="_initSockJs" type="primary" :d isabled="isConnected"> manual connection</el-button>
            <el-button @click="_destroySockJs" type="danger" :d isabled="!isConnected"> disconnected</el-button>
        </el-header>
        <el-main>
            <el-row :gutter="20">
                <el-col :span="12">
                    <el-card>
                        <div slot="header">
                            <span>CPU information</span>
                        </div>
                        <el-table size="small" border :data="server.cpu" style="width: 100%">
                            <el-table-column prop="key" label="property">
                            </el-table-column>
                            <el-table-column prop="value" label="value">
                            </el-table-column>
                        </el-table>
                    </el-card>
                </el-col>
                <el-col :span="12">
                    <el-card>
                        <div slot="header">
                            <span>Memory information</span>
                        </div>
                        <el-table size="small" border :data="server.mem" style="width: 100%">
                            <el-table-column prop="key" label="property">
                            </el-table-column>
                            <el-table-column prop="value" label="value">
                            </el-table-column>
                        </el-table>
                    </el-card>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="24">
                    <el-card>
                        <div slot="header">
                            <span>Server information</span>
                        </div>
                        <el-table size="small" border :data="server.sys" style="width: 100%">
                            <el-table-column prop="key" label="property">
                            </el-table-column>
                            <el-table-column prop="value" label="value">
                            </el-table-column>
                        </el-table>
                    </el-card>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="24">
                    <el-card>
                        <div slot="header">
                            <span>Java Virtual Machine Information</span>
                        </div>
                        <el-table size="small" border :data="server.jvm" style="width: 100%">
                            <el-table-column prop="key" label="property">
                            </el-table-column>
                            <el-table-column prop="value" label="value">
                            </el-table-column>
                        </el-table>
                    </el-card>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="24">
                    <el-card>
                        <div slot="header">
                            <span>Disk status</span>
                        </div>
                        <div class="sysFile" v-for="(item,index) in server.sysFile" :key="index">
                            <el-table size="small" border :data="item" style="width: 100%">
                                <el-table-column prop="key" label="property">
                                </el-table-column>
                                <el-table-column prop="value" label="value">
                                </el-table-column>
                            </el-table>
                        </div>
                    </el-card>
                </el-col>
            </el-row>
        </el-main>
    </el-container>
</div>
</body>
<script src="js/sockjs.min.js"></script>
<script src="js/stomp.js"></script>
<script src="https://cdn.bootcss.com/vue/2.5.21/vue.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/element-ui/2.4.11/index.js"></script>
<script src="https://cdn.bootcss.com/axios/0.19.0-beta.1/axios.min.js"></script>
<script>
    const wsHost = "http://localhost:8080/demo/notification";
    const wsTopic = "/topic/server";

    const app = new Vue({
        el: '#app',
        data: function () {
            return {
                isConnected: false,
                stompClient: {},
                socket: {},
                server: {
                    cpu: [],
                    mem: [],
                    jvm: [],
                    sys: [],
                    sysFile: []
                }
            }
        },
        methods: {
            _getServerInfo() {
                axios.get('/demo/server')
                    .then((response) => {
                        this.server = response.data
                    });
            },
            _initSockJs() {
                this._getServerInfo();
                this.socket = new SockJS(wsHost);
                this.stompClient = Stomp.over(this.socket);

                this.stompClient.connect({}, (frame) => {
                    console.log ('websocket connection successful:' + frame);
                    this.isConnected = true;
                    this.$message ('websocket server connection successful');

                    Also register for a message push
                    this.stompClient.subscribe(wsTopic, (response) => {
                        this.server = JSON.parse(response.body);
                    });
                });
            },
            _destroySockJs() {
                if (this.stompClient != null) {
                    this.stompClient.disconnect();
                    this.socket.onclose;
                    this.socket.close();
                    this.stompClient = {};
                    this.socket = {};
                    this.isConnected = false;
                    this.server.cpu = [];
                    this.server.mem = [];
                    this.server.jvm = [];
                    this.server.sys = [];
                    this.server.sysFile = [];
                }
                console.log ('websocket disconnected successful!) ');
                this.$message.error('websocket disconnected successful!) ');
            }
        },
        mounted() {
            this._initSockJs();
        },
        beforeDestroy() {
            this._destroySockJs();
        }

    })
</script>
</html>
```

## 2. Operates in

1. Start 'SpringBootDemoWebsocketApplication.java'
2. Access http://localhost:8080/demo/server.html

## 3. Run the effect

! [image-20181217110240322] (http://static.xkcoding.com/spring-boot-demo/websocket/064107.jpg)

! [image-20181217110304065] (http://static.xkcoding.com/spring-boot-demo/websocket/064108.jpg)

! [image-20181217110328810] (http://static.xkcoding.com/spring-boot-demo/websocket/064109.jpg)

! [image-20181217110336017] (http://static.xkcoding.com/spring-boot-demo/websocket/064109-1.jpg)

## 4. reference

### 4.1. back end

1. Spring Boot integrates Websocket official documentation: https://docs.spring.io/spring/docs/5.1.2.RELEASE/spring-framework-reference/web.html#websocket
2. Server Information Collection oshi Usage: https://github.com/oshi/oshi

### 4.2. Front

1. vue.js Syntax:https://cn.vuejs.org/v2/guide/
2. element-ui usage: http://element-cn.eleme.io/#/zh-CN
3. stomp.js Usage: https://github.com/jmesnil/stomp-websocket
4. Sockjs usage: https://github.com/sockjs/sockjs-client
5. axios.js Usage: https://github.com/axios/axios#example
