package com.xkcoding.cache.ehcache.entity;

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
 * @date Created in 2018-11-16 16:53
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
