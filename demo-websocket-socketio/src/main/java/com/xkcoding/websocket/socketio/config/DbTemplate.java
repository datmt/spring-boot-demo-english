package com.xkcoding.websocket.socketio.config;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * Simulated database
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-18 19:12
 */
@Component
public class DbTemplate {
    /**
     * The simulated database stores the relationship user_id <-> session_id
     */
    public static final ConcurrentHashMap<String, UUID> DB = new ConcurrentHashMap<>();

    /**
     * Get all SessionIds
     *
     * @return SessionId list
     */
    public List<UUID> findAll() {
        return CollUtil.newArrayList(DB.values());
    }

    /**
     * Query SessionId according to UserId
     *
     * @param userId userid
     * @return SessionId
     */
    public Optional<UUID> findByUserId(String userId) {
        return Optional.ofNullable(DB.get(userId));
    }

    /**
     * Save/update user_id <-> session_id relationships
     *
     * @param userId userid
     * @param sessionId SessionId
     */
    public void save(String userId, UUID sessionId) {
        DB.put(userId, sessionId);
    }

    /**
     * Delete the user_id <-> session_id relationship
     *
     * @param userId userid
     */
    public void deleteByUserId(String userId) {
        DB.remove(userId);
    }

}
