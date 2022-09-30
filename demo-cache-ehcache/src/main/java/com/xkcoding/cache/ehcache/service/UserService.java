package com.xkcoding.cache.ehcache.service;

import com.xkcoding.cache.ehcache.entity.User;

/**
 * <p>
 * UserService
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-16 16:53
 */
public interface UserService {
    /**
     * Save or modify users
     *
     * @param user user object
     * @return Operation result
     */
    User saveOrUpdate(User user);

    /**
     * Get users
     *
     * @param id key value
     * @return Returns results
     */
    User get(Long id);

    /**
     * Delete
     *
     * @param id key value
     */
    void delete(Long id);
}
