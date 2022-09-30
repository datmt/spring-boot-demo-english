package com.xkcoding.mq.rabbitmq.constants;

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
