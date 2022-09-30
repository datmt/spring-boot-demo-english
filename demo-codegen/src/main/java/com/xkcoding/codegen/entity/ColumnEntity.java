package com.xkcoding.codegen.entity;

import lombok.Data;

/**
 * <p>
 * Column Properties: https://blog.csdn.net/lkforce/article/details/79557482
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-03-22 09:46
 */
@Data
public class ColumnEntity {
    /**
     * List
     */
    private String columnName;
    /**
     * Data type
     */
    private String dataType;
    /**
     * Remarks
     */
    private String comments;
    /**
     * Hump properties
     */
    private String caseAttrName;
    /**
     * Normal properties
     */
    private String lowerAttrName;
    /**
     * Attribute type
     */
    private String attrType;
    /**
     * JDBC type
     */
    private String jdbcType;
    /**
     * Additional information
     */
    private String extra;
}
