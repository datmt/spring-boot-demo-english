package com.xkcoding.codegen.entity;

import lombok.Data;

/**
 * <p>
 * Form request parameters
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-03-22 10:24
 */
@Data
public class TableRequest {
    /**
     * Current page
     */
    private Integer currentPage;
    /**
     * Number of articles per page
     */
    private Integer pageSize;
    /**
     * jdbc- prefix
     */
    private String prepend;
    /**
     * jdbc-url
     */
    private String url;
    /**
     * Username
     */
    private String username;
    /**
     * Password
     */
    private String password;
    /**
     * Table name
     */
    private String tableName;
}
