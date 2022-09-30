package com.xkcoding.websocket.socketio.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 * WebSocket configuration class
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-18 16:41
 */
@ConfigurationProperties(prefix = "ws.server")
@Data
public class WsConfig {
    /**
     * Port number
     */
    private Integer port;

    /**
     * host
     */
    private String host;
}
