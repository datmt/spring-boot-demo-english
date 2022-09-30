package com.xkcoding.cache.ehcache.service.impl;

import com.google.common.collect.Maps;
import com.xkcoding.cache.ehcache.entity.User;
import com.xkcoding.cache.ehcache.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <p>
 * UserService
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-16 16:54
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    /**
     * Simulated database
     */
    private static final Map<Long, User> DATABASES = Maps.newConcurrentMap();

    /**
     * Initialize data
     */
    static {
        DATABASES.put(1L, new User(1L, "user1"));
        DATABASES.put(2L, new User(2L, "user2"));
        DATABASES.put(3L, new User(3L, "user3"));
    }

    /**
     * Save or modify users
     *
     * @param user user object
     * @return Operation result
     */
    @CachePut(value = "user", key = "#user.id")
    @Override
    public User saveOrUpdate(User user) {
        DATABASES.put(user.getId(), user);
        log.info("保存用户【user】= {}", user);
        return user;
    }

    /**
     * Get users
     *
     * @param id key value
     * @return Returns results
     */
    @Cacheable(value = "user", key = "#id")
    @Override
    public User get(Long id) {
        We assume that a read is made from a database
        log.info("查询用户【id】= {}", id);
        return DATABASES.get(id);
    }

    /**
     * Delete
     *
     * @param id key value
     */
    @CacheEvict(value = "user", key = "#id")
    @Override
    public void delete(Long id) {
        DATABASES.remove(id);
        log.info("删除用户【id】= {}", id);
    }
}
