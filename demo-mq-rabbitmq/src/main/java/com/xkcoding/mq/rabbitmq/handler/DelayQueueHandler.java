package com.xkcoding.mq.rabbitmq.handler;

import cn.hutool.json.JSONUtil;
import com.rabbitmq.client.Channel;
import com.xkcoding.mq.rabbitmq.constants.RabbitConsts;
import com.xkcoding.mq.rabbitmq.message.MessageStruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * <p>
 * Delay queue processor
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-01-04 17:42
 */
@Slf4j
@Component
@RabbitListener(queues = RabbitConsts.DELAY_QUEUE)
public class DelayQueueHandler {

    @RabbitHandler
    public void directHandlerManualAck(MessageStruct messageStruct, Message message, Channel channel) {
        If you acknowledge manually, the message will be listened to for consumption, but the message will still exist in the queue, and if acknowledge-mode is not configured, the default is that the ACK will be automatically dropped after consumption
        final long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            log.info("延迟队列，手动ACK，接收消息：{}", JSONUtil.toJsonStr(messageStruct));
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
