package com.xkcoding.rbac.security.util;

import com.google.common.collect.Lists;
import com.xkcoding.rbac.security.common.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * Redis tool class
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-11 20:24
 */
@Component
@Slf4j
public class RedisUtil {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * Paging to get the specified format key, using the scan command instead of the keys command, can improve query efficiency in the case of large volumes
     *
     * @param patternKey key format
     * @param currentPage current page number
     * @param pageSize per page
     * @return Pagination gets the specified format key
     */
    public PageResult<String> findKeysForPage(String patternKey, int currentPage, int pageSize) {
        ScanOptions options = ScanOptions.scanOptions().match(patternKey).build();
        RedisConnectionFactory factory = stringRedisTemplate.getConnectionFactory();
        RedisConnection rc = factory.getConnection();
        Cursor<byte[]> cursor = rc.scan(options);

        List<String> result = Lists.newArrayList();

        long tmpIndex = 0;
        int startIndex = (currentPage - 1) * pageSize;
        int end = currentPage * pageSize;
        while (cursor.hasNext()) {
            String key = new String(cursor.next());
            if (tmpIndex >= startIndex && tmpIndex < end) {
                result.add(key);
            }
            tmpIndex++;
        }

        try {
            cursor.close();
            RedisConnectionUtils.releaseConnection(rc, factory);
        } catch (Exception e) {
            log.warn("Redis连接关闭异常，", e);
        }

        return new PageResult<>(result, tmpIndex);
    }

    /**
     * Delete a key in Redis
     *
     * @param key key
     */
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * Bulk delete some keys in Redis
     *
     * @param list of keys
     */
    public void delete(Collection<String> keys) {
        stringRedisTemplate.delete(keys);
    }
}
