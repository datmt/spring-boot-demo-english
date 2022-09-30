package com.xkcoding.websocket.socketio.payload;

import lombok.Data;

/**
 * <p>
 * Add group load
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-19 13:36
 */
@Data
public class JoinRequest {
    /**
     * User ID
     */
    private String userId;

    /**
     * Group name
     */
    private String groupId;
}
