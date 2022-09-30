package com.xkcoding.cache.ehcache.service;

import com.xkcoding.cache.ehcache.SpringBootDemoCacheEhcacheApplicationTests;
import com.xkcoding.cache.ehcache.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * ehcache test
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-16 16:58
 */
@Slf4j
public class UserServiceTest extends SpringBootDemoCacheEhcacheApplicationTests {

    @Autowired
    private UserService userService;

    /**
     * Get twice, view the log verification cache
     */
    @Test
    public void getTwice() {
        Simulates a user whose query has an id of 1
        User user1 = userService.get(1L);
        log.debug("【user1】= {}", user1);

        Query again
        User user2 = userService.get(1L);
        log.debug("【user2】= {}", user2);
        Review the log and print it only once to prove that the cache is in effect
    }

    /**
     * Save first, then query, view the log verification cache
     */
    @Test
    public void getAfterSave() {
        userService.saveOrUpdate(new User(4L, "user4"));

        User user = userService.get(4L);
        log.debug("【user】= {}", user);
        View the log, only print the log of the saved user, the query is not triggered query log, so the cache takes effect
    }

    /**
     * Test deletion to see if redis has cached data
     */
    @Test
    public void deleteUser() {
        Query once so that cached data exists in the ehcache
        userService.get(1L);
        Delete to see if the ehcache exists for cached data
        userService.delete(1L);
    }
}
