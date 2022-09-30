package com.xkcoding.codegen.entity;

import lombok.Data;

/**
 * <p>
 * Build configuration
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-03-22 09:47
 */
@Data
public class GenConfig {
    /**
     * Request parameters
     */
    private TableRequest request;
    /**
     * Package name
     */
    private String packageName;
    /**
     * Author
     */
    private String author;
    /**
     * Module name
     */
    private String moduleName;
    /**
     * Table prefix
     */
    private String tablePrefix;
    /**
     * Table name
     */
    private String tableName;
    /**
     * Table remarks
     */
    private String comments;
}
