package com.xkcoding.websocket.socketio.payload;

import lombok.Data;

/**
 * <p>
 * Broadcast message load
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-18 20:01
 */
@Data
public class BroadcastMessageRequest {
    /**
     * Message content
     */
    private String message;
}
