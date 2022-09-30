package com.xkcoding.websocket.socketio.payload;

import lombok.Data;

/**
 * <p>
 * Private chat message payload
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-18 17:02
 */
@Data
public class SingleMessageRequest {
    /**
     * Message sender user ID
     */
    private String fromUid;

    /**
     * Message receiver user ID
     */
    private String toUid;

    /**
     * Message content
     */
    private String message;
}
