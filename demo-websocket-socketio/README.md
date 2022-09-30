# spring-boot-demo-websocket-socketio

> This demo mainly demonstrates how Spring Boot can use 'netty-socketio' to integrate WebSocket and implement a simple chat room.

## 1. code

### 1.1. pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>spring-boot-demo-websocket-socketio</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>spring-boot-demo-websocket-socketio</name>
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
        <netty-socketio.version>1.7.16</netty-socketio.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>com.corundumstudio.socketio</groupId>
            <artifactId>netty-socketio</artifactId>
            <version>${netty-socketio.version}</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
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
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>

    <build>
        <finalName>spring-boot-demo-websocket-socketio</finalName>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

### 1.2. ServerConfig.java

> websocket server configuration, including server IP, port information, and connection authentication configuration

```java
/**
 * <p>
 * Websocket server configuration
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-18 16:42
 */
@Configuration
@EnableConfigurationProperties({WsConfig.class})
public class ServerConfig {

    @Bean
    public SocketIOServer server(WsConfig wsConfig) {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname(wsConfig.getHost());
        config.setPort(wsConfig.getPort());

        This listener can be used for authentication
        config.setAuthorizationListener(data -> {
             http://localhost:8081?token=xxxxxxx
            For example, if you use the above link for connect, you can use the following code to obtain the user password information, this document does not do authentication
            String token = data.getSingleUrlParam("token");
            To verify the legitimacy of the token, the actual business needs to verify whether the token has expired, etc., refer to JwtUtil in spring-boot-demo-rbac-security
            If the authentication fails, a Socket.EVENT_CONNECT_ERROR event is returned
            return StrUtil.isNotBlank(token);
        });

        return new SocketIOServer(config);
    }

    /**
     * Spring scans custom annotations
     */
    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer server) {
        return new SpringAnnotationScanner(server);
    }
}
```

### 1.3. MessageEventHandler.java

> core event processing class that primarily handles client-initiated message events and proactively initiates events to clients

```java
/**
 * <p>
 * Message event handling
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-18 18:57
 */
@Component
@Slf4j
public class MessageEventHandler {
    @Autowired
    private SocketIOServer server;

    @Autowired
    private DbTemplate dbTemplate;

    /**
     * Add the connect event, which is called when the client initiates a connection
     *
     * @param client object
     */
    @OnConnect
    public void onConnect(SocketIOClient client) {
        if (client != null) {
            String token = client.getHandshakeData().getSingleUrlParam("token");
            The impersonated user id and token are consistent
            String userId = client.getHandshakeData().getSingleUrlParam("token");
            UUID sessionId = client.getSessionId();

            dbTemplate.save(userId, sessionId);
            log.info ("Connection successful, [token] = {}, [sessionId] = {}", token, sessionId);
        } else {
            log.error("Client is empty");
        }
    }

    /**
     * Add disconnect event, called when the client disconnects, refresh client information
     *
     * @param client object
     */
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        if (client != null) {
            String token = client.getHandshakeData().getSingleUrlParam("token");
            The impersonated user id and token are consistent
            String userId = client.getHandshakeData().getSingleUrlParam("token");
            UUID sessionId = client.getSessionId();

            dbTemplate.deleteByUserId(userId);
            log.info ("client disconnects, [token] = {}, [sessionId] = {}", token, sessionId);
            client.disconnect();
        } else {
            log.error("Client is empty");
        }
    }

    /**
     * Join a group chat
     *
     * @param client client
     * @param request request
     * @param data group chat
     */
    @OnEvent(value = Event.JOIN)
    public void onJoinEvent(SocketIOClient client, AckRequest request, JoinRequest data) {
        log.info ("User:{} Joined Group Chat:{}", data.getUserId(), data.getGroupId());
        client.joinRoom(data.getGroupId());

        server.getRoomOperations(data.getGroupId()).sendEvent(Event.JOIN, data);
    }


    @OnEvent(value = Event.CHAT)
    public void onChatEvent(SocketIOClient client, AckRequest request, SingleMessageRequest data) {
        Optional<UUID> toUser = dbTemplate.findByUserId(data.getToUid());
        if (toUser.isPresent()) {
            log.info ("User {} just sent a private message to user {}:{}", data.getFromUid(), data.getToUid(), data.getMessage());
            sendToSingle(toUser.get(), data);
            client.sendEvent(Event.CHAT_RECEIVED, "Sent successfully");
        } else {
            client.sendEvent(Event.CHAT_REFUSED, "The sending failed, the other party does not want to pay attention to you");
        }
    }

    @OnEvent(value = Event.GROUP)
    public void onGroupEvent(SocketIOClient client, AckRequest request, GroupMessageRequest data) {
        Collection<SocketIOClient> clients = server.getRoomOperations(data.getGroupId()).getClients();

        boolean inGroup = false;
        for (SocketIOClient socketIOClient : clients) {
            if (ObjectUtil.equal(socketIOClient.getSessionId(), client.getSessionId())) {
                inGroup = true;
                break;
            }
        }
        if (inGroup) {
            log.info("Group number {} received a group chat message from {}:{}", data.getGroupId(), data.getFromUid(), data.getMessage());
            sendToGroup(data);
        } else {
            request.sendAckData("Please add the group first!) );
        }
    }

    /**
     * Single chat
     */
    public void sendToSingle(UUID sessionId, SingleMessageRequest message) {
        server.getClient(sessionId).sendEvent(Event.CHAT, message);
    }

    /**
     * Broadcast
     */
    public void sendToBroadcast(BroadcastMessageRequest message) {
        log.info ("System emergency broadcast a notification: {}", message.getMessage());
        for (UUID clientId : dbTemplate.findAll()) {
            if (server.getClient(clientId) == null) {
                continue;
            }
            server.getClient(clientId).sendEvent(Event.BROADCAST, message);
        }
    }

    /**
     * Group chat
     */
    public void sendToGroup(GroupMessageRequest message) {
        server.getRoomOperations(message.getGroupId()).sendEvent(Event.GROUP, message);
    }
}
```

### 1.4. ServerRunner.java

> websocket server startup class

```java
/**
 * <p>
 * Websocket server starts
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-18 17:07
 */
@Component
@Slf4j
public class ServerRunner implements CommandLineRunner {
    @Autowired
    private SocketIOServer server;

    @Override
    public void run(String... args) {
        server.start();
        log.info ("Websocket server started successfully...) );
    }
}
```

## 2. Operates in

1. Start 'SpringBootDemoWebsocketSocketioApplication.java'
2. Use a different browser to access http://localhost:8080/demo/index.html

## 3. Run the effect

**Browser 1:**! [image-20181219152318079] (http://static.xkcoding.com/spring-boot-demo/websocket/socketio/064155.jpg)

**Browser 2:**! [image-20181219152330156] (http://static.xkcoding.com/spring-boot-demo/websocket/socketio/064154.jpg)

## 4. reference

### 4.1. back end

1. Netty-socketio official repository: https://github.com/mrniko/netty-socketio
2. SpringBoot Series - Integrated SocketIO Real-Time Communication: https://www.xncoding.com/2017/07/16/spring/sb-socketio.html
3. Spring Boot integrates with socket.io backend for real-time message communication:http://alexpdh.com/2017/09/03/springboot-socketio/
4. Spring Boot's netty-socketio implements simple chat rooms: http://blog.csdn.net/sun_t89/article/details/52060946

### 4.2. Front

1. socket.io Official Website:https://socket.io/
2. axios.js Usage: https://github.com/axios/axios#example
