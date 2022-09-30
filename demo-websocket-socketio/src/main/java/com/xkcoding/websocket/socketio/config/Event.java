package com.xkcoding.websocket.socketio.config;

/**
 * <p>
 * Event constants
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-18 19:36
 */
public interface Event {
    /**
     * Chat events
     */
    String CHAT = "chat";

    /**
     * Broadcast messages
     */
    String BROADCAST = "broadcast";

    /**
     * Group chat
     */
    String GROUP = "group";

    /**
     * Join a group chat
     */
    String JOIN = "join";

}
