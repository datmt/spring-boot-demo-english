package com.xkcoding.cache.redis.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * User entity
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-15 16:39
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    private static final long serialVersionUID = 2892248514883451461L;
    /**
     * Primary key id
     */
    private Long id;
    /**
     * Name
     */
    private String name;
}
