# spring-boot-demo-mq-rabbitmq

> This demo demonstrates how Spring Boot integrates with RabbitMQ and demonstrates the sending and receiving of messages based on direct queue mode, column mode, topic mode, and delay queue.

## Note

When the author wrote this demo, the RabbitMQ version uses '3.7.7-management' and runs using docker, here are all the steps:

1. Download the image: 'docker pull rabbitmq:3.7.7-management'

2. Run container: 'docker run -d -p 5671:5617 -p 5672:5672 -p 4369:4369 -p 15671:15671 -p 15672:15672 -p 25672:25672 --name rabbit-3.7.7 rabbitmq:3.7.7-management'

3. Enter the container: 'docker exec -it rabbit-3.7.7 /bin/bash'

4. Install the container Download tool wget:'apt-get install -y wget'

5. Download the plugin package as our 'RabbitMQ' version is '3.7.7' so we install the '3.7.x' version of the Delay Queue plugin

   ```bash
   root@f72ac937f2be:/plugins# wget https://dl.bintray.com/rabbitmq/community-plugins/3.7.x/rabbitmq_delayed_message_exchange/rabbitmq_delayed_message_exchange-20171201-3.7.x.zip
   ```

6. Install the unzipping tool unzip for the container: 'apt-get install -y unzip'

7. Unzip the plugin package

   ```bash
   root@f72ac937f2be:/plugins# unzip rabbitmq_delayed_message_exchange-20171201-3.7.x.zip
   Archive:  rabbitmq_delayed_message_exchange-20171201-3.7.x.zip
     inflating: rabbitmq_delayed_message_exchange-20171201-3.7.x.ez
   ```

8. Start the Delay Queue plug-in

   ```yaml
   root@f72ac937f2be:/plugins# rabbitmq-plugins enable rabbitmq_delayed_message_exchange
   The following plugins have been configured:
     rabbitmq_delayed_message_exchange
     rabbitmq_management
     rabbitmq_management_agent
     rabbitmq_web_dispatch
   Applying plugin configuration to rabbit@f72ac937f2be...
   The following plugins have been enabled:
     rabbitmq_delayed_message_exchange

   started 1 plugins.
   ```

9. Exit the container: 'exit'

10. Stop Container: 'docker stop rabbit-3.7.7'

11. Start the container: 'docker start rabbit-3.7.7'

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>spring-boot-demo-mq-rabbitmq</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>spring-boot-demo-mq-rabbitmq</name>
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
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
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
        <finalName>spring-boot-demo-mq-rabbitmq</finalName>
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
server:
  port: 8080
  servlet:
    context-path: /demo
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
    virtual-host: /
    # Submit the message manually
    listener:
      simple:
        acknowledge-mode: manual
      direct:
        acknowledge-mode: manual
```

## RabbitConsts.java

```java
/**
 * <p>
 * RabbitMQ constant pool
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-29 17:08
 */
public interface RabbitConsts {
    /**
     * Direct mode 1
     */
    String DIRECT_MODE_QUEUE_ONE = "queue.direct.1";

    /**
     * Queue 2
     */
    String QUEUE_TWO = "queue.2";

    /**
     * Queue 3
     */
    String QUEUE_THREE = "3.queue";

    /**
     * Column mode
     */
    String FANOUT_MODE_QUEUE = "fanout.mode";

    /**
     * Theme mode
     */
    String TOPIC_MODE_QUEUE = "topic.mode";

    /**
     * Route 1
     */
    String TOPIC_ROUTING_KEY_ONE = "queue.#";

    /**
     * Route 2
     */
    String TOPIC_ROUTING_KEY_TWO = "*.queue";

    /**
     * Route 3
     */
    String TOPIC_ROUTING_KEY_THREE = "3.queue";

    /**
     * Delay queue
     */
    String DELAY_QUEUE = "delay.queue";

    /**
     * Delay queue switcher
     */
    String DELAY_MODE_QUEUE = "delay.mode";
}
```

## RabbitMqConfig.java

> RoutingKey rules
>
> - The routing format must be separated by '.', such as 'user.email' or 'user.aaa.email'
> - The wildcard character '*' represents a placeholder, or a word, such as 'user.*' for a route, then **'user.email' can match, but *'user.aaa.email'* does not
> - The wildcard character '#' represents one or more placeholders, or one or more words, such as 'user.#', if the route is 'user.#', then **'user.email' can be matched, and **'user.aaa.email' '** can also be matched

```java
/**
 * <p>
 * RabbitMQ configuration, mainly configuration queue, if the queue exists in advance, you can omit this configuration class
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-29 17:03
 */
@Slf4j
@Configuration
public class RabbitMqConfig {

    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory connectionFactory) {
        connectionFactory.setPublisherConfirms(true);
        connectionFactory.setPublisherReturns(true);
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> log.info ("Message sent successfully:correlationData({}),ack({}),cause({})", correlationData, ack, cause));
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> log.info("Message lost:exchange({}),route({}),replyCode({}),replyText({}),message:{}", exchange, routingKey, replyCode, replyText, message));
        return rabbitTemplate;
    }

    /**
     * Direct mode queue 1
     */
    @Bean
    public Queue directOneQueue() {
        return new Queue(RabbitConsts.DIRECT_MODE_QUEUE_ONE);
    }

    /**
     * Queue 2
     */
    @Bean
    public Queue queueTwo() {
        return new Queue(RabbitConsts.QUEUE_TWO);
    }

    /**
     * Queue 3
     */
    @Bean
    public Queue queueThree() {
        return new Queue(RabbitConsts.QUEUE_THREE);
    }

    /**
     * Column mode queue
     */
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(RabbitConsts.FANOUT_MODE_QUEUE);
    }

    /**
     * Column mode bind queue 1
     *
     * @param directOneQueue bound queue 1
     * @param fanoutExchange Split Mode Switch
     */
    @Bean
    public Binding fanoutBinding1(Queue directOneQueue, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(directOneQueue).to(fanoutExchange);
    }

    /**
     * Column mode bind queue 2
     *
     * @param queueTwo bound queue 2
     * @param fanoutExchange Split Mode Switch
     */
    @Bean
    public Binding fanoutBinding2(Queue queueTwo, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(queueTwo).to(fanoutExchange);
    }

    /**
     * Topic mode queues
     * <li>The routing format must be separated by . , such as user.email or user.aaa.email</li>
     * <li>wildcard character * , represents a placeholder, or a word, such as the route is user.*, then the user.email can match, but user.aaa.email can not match</li>
     * <li>The wildcard character # represents one or more placeholders, or one or more words, such as a route as user.#, then user.email can match, user.aaa.email can also match</li>
     */
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(RabbitConsts.TOPIC_MODE_QUEUE);
    }


    /**
     * Theme mode binding column mode
     *
     * @param fanoutExchange Split Mode Switch
     * @param topicExchange topic mode switcher
     */
    @Bean
    public Binding topicBinding1(FanoutExchange fanoutExchange, TopicExchange topicExchange) {
        return BindingBuilder.bind(fanoutExchange).to(topicExchange).with(RabbitConsts.TOPIC_ROUTING_KEY_ONE);
    }

    /**
     * Topic mode binding queue 2
     *
     * @param queueTwo queue 2
     * @param topicExchange topic mode switcher
     */
    @Bean
    public Binding topicBinding2(Queue queueTwo, TopicExchange topicExchange) {
        return BindingBuilder.bind(queueTwo).to(topicExchange).with(RabbitConsts.TOPIC_ROUTING_KEY_TWO);
    }

    /**
     * Topic mode binding queue 3
     *
     * @param queue three queue 3
     * @param topicExchange topic mode switcher
     */
    @Bean
    public Binding topicBinding3(Queue queueThree, TopicExchange topicExchange) {
        return BindingBuilder.bind(queueThree).to(topicExchange).with(RabbitConsts.TOPIC_ROUTING_KEY_THREE);
    }

    /**
     * Delay queue
     */
    @Bean
    public Queue delayQueue() {
        return new Queue(RabbitConsts.DELAY_QUEUE, true);
    }

    /**
     * Delay queue switchers, x-delayed-type and x-delayed-message fixed
     */
    @Bean
    public CustomExchange delayExchange() {
        Map<String, Object> args = Maps.newHashMap();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(RabbitConsts.DELAY_MODE_QUEUE, "x-delayed-message", true, false, args);
    }

    /**
     * Delay queue binding custom switch
     *
     * @param delayQueue queue
     * @param delayExchange delay switcher
     */
    @Bean
    public Binding delayBinding(Queue delayQueue, CustomExchange delayExchange) {
        return BindingBuilder.bind(delayQueue).to(delayExchange).with(RabbitConsts.DELAY_QUEUE).noargs();
    }

}
```

## Message processor

> only shows the message processing of the direct queue mode, and the rest of the modes can be found in the source code
>
> Note: If 'spring.rabbitmq.listener.direct.acknowledge-mode', it is automatically acked, otherwise a manual ack is required

### DirectQueueOneHandler.java

```java
/**
 * <p>
 * Direct queue 1 processor
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-01-04 15:42
 */
@Slf4j
@RabbitListener(queues = RabbitConsts.DIRECT_MODE_QUEUE_ONE)
@Component
public class DirectQueueOneHandler {

    /**
     * If spring.rabbitmq.listener.direct.acknowledge-mode: auto, you can use this way, automatically ack
     */
     @RabbitHandler
    public void directHandlerAutoAck(MessageStruct message) {
        log.info ("Direct queue processor, receiving message: {}", JSONUtil.toJsonStr(message));
    }

    @RabbitHandler
    public void directHandlerManualAck(MessageStruct messageStruct, Message message, Channel channel) {
        If you acknowledge manually, the message will be listened to for consumption, but the message will still exist in the queue, and if acknowledge-mode is not configured, the default is that the ACK will be automatically dropped after consumption
        final long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            log.info ("Direct queue 1, manual ACK, receive message: {}", JSONUtil.toJsonStr(messageStruct));
            Notifying the MQ message has been successfully consumed and can ACK away
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            try {
                Processing failed, re-press MQ
                channel.basicRecover();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
```

## SpringBootDemoMqRabbitmqApplicationTests.java

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootDemoMqRabbitmqApplicationTests {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * Test direct mode sending
     */
    @Test
    public void sendDirect() {
        rabbitTemplate.convertAndSend(RabbitConsts.DIRECT_MODE_QUEUE_ONE, new MessageStruct("direct message"));
    }

    /**
     * Test column mode sending
     */
    @Test
    public void sendFanout() {
        rabbitTemplate.convertAndSend(RabbitConsts.FANOUT_MODE_QUEUE, "", new MessageStruct("fanout message"));
    }

    /**
     * Test theme mode send 1
     */
    @Test
    public void sendTopic1() {
        rabbitTemplate.convertAndSend(RabbitConsts.TOPIC_MODE_QUEUE, "queue.aaa.bbb", new MessageStruct("topic message"));
    }

    /**
     * Test theme mode send 2
     */
    @Test
    public void sendTopic2() {
        rabbitTemplate.convertAndSend(RabbitConsts.TOPIC_MODE_QUEUE, "ccc.queue", new MessageStruct("topic message"));
    }

    /**
     * Test theme mode send 3
     */
    @Test
    public void sendTopic3() {
        rabbitTemplate.convertAndSend(RabbitConsts.TOPIC_MODE_QUEUE, "3.queue", new MessageStruct("topic message"));
    }

    /**
     * Test delay queue sending
     */
    @Test
    public void sendDelay() {
        rabbitTemplate.convertAndSend(RabbitConsts.DELAY_MODE_QUEUE, RabbitConsts.DELAY_QUEUE, new MessageStruct("delay message, delay 5s, " + DateUtil
                .date()), message -> {
            message.getMessageProperties().setHeader("x-delay", 5000);
            return message;
        });
        rabbitTemplate.convertAndSend(RabbitConsts.DELAY_MODE_QUEUE, RabbitConsts.DELAY_QUEUE, new MessageStruct("delay message,  delay 2s, " + DateUtil
                .date()), message -> {
            message.getMessageProperties().setHeader("x-delay", 2000);
            return message;
        });
        rabbitTemplate.convertAndSend(RabbitConsts.DELAY_MODE_QUEUE, RabbitConsts.DELAY_QUEUE, new MessageStruct("delay message,  delay 8s, " + DateUtil
                .date()), message -> {
            message.getMessageProperties().setHeader("x-delay", 8000);
            return message;
        });
    }

}
```

## Run effect

### Direct mode

! [image-20190107103229408] (http://static.xkcoding.com/spring-boot-demo/mq/rabbitmq/063315-1.jpg)

### Column mode

! [image-20190107103258291] (http://static.xkcoding.com/spring-boot-demo/mq/rabbitmq/063315.jpg)

### Theme mode

#### RoutingKey：`queue.#`

! [image-20190107103358744] (http://static.xkcoding.com/spring-boot-demo/mq/rabbitmq/063316.jpg)

#### RoutingKey：`*.queue`

! [image-20190107103429430] (http://static.xkcoding.com/spring-boot-demo/mq/rabbitmq/063312.jpg)

#### RoutingKey：`3.queue`

! [image-20190107103451240] (http://static.xkcoding.com/spring-boot-demo/mq/rabbitmq/063313.jpg)

### Delay queue

! [image-20190107103509943] (http://static.xkcoding.com/spring-boot-demo/mq/rabbitmq/063314.jpg)

## Reference

1. SpringQP Official Documentation: https://docs.spring.io/spring-amqp/docs/2.1.0.RELEASE/reference/html/
2. RabbitMQ official website: http://www.rabbitmq.com/
3. RabbitMQ delay queue: https://www.cnblogs.com/vipstone/p/9967649.html
