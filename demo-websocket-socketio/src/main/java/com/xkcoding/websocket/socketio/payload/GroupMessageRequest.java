package com.xkcoding.websocket.socketio.payload;

import lombok.Data;

/**
 * <p>
 * Group chat message payload
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-18 16:59
 */
@Data
public class GroupMessageRequest {
    /**
     * Message sender user ID
     */
    private String fromUid;

    /**
     * Group ID
     */
    private String groupId;

    /**
     * Message content
     */
    private String message;
}
