package com.xkcoding.websocket.socketio.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.xkcoding.websocket.socketio.handler.MessageEventHandler;
import com.xkcoding.websocket.socketio.payload.BroadcastMessageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;

/**
 * <p>
 * Messages sent to the Controller
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-18 19:50
 */
@RestController
@RequestMapping("/send")
@Slf4j
public class MessageController {
    @Autowired
    private MessageEventHandler messageHandler;

    @PostMapping("/broadcast")
    public Dict broadcast(@RequestBody BroadcastMessageRequest message) {
        if (isBlank(message)) {
            return Dict.create().set("flag", false).set("code", 400).set("message", "参数为空");
        }
        messageHandler.sendToBroadcast(message);
        return Dict.create().set("flag", true).set("code", 200).set("message", "发送成功");
    }

    /**
     * Determine whether the bean is an empty object or a blank string, the empty object represents itself to be null or<code></code> all properties are <code>null</code>
     *
     * @param bean bean object
     * @return Whether it is empty, <code>true</code> - empty / <code>false</code> - non-empty
     */
    private boolean isBlank(Object bean) {
        if (null != bean) {
            for (Field field : ReflectUtil.getFields(bean.getClass())) {
                Object fieldValue = ReflectUtil.getFieldValue(bean, field);
                if (null != fieldValue) {
                    if (fieldValue instanceof String && StrUtil.isNotBlank((String) fieldValue)) {
                        return false;
                    } else if (!(fieldValue instanceof String)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
