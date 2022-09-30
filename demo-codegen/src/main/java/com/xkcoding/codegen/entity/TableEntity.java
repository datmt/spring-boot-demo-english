package com.xkcoding.codegen.entity;

import lombok.Data;

import java.util.List;

/**
 * <p>
 * Table Attributes: https://blog.csdn.net/lkforce/article/details/79557482
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-03-22 09:47
 */
@Data
public class TableEntity {
    /**
     * Name
     */
    private String tableName;
    /**
     * Remarks
     */
    private String comments;
    /**
     * Primary key
     */
    private ColumnEntity pk;
    /**
     * Column names
     */
    private List<ColumnEntity> columns;
    /**
     * Hump type
     */
    private String caseClassName;
    /**
     * Normal type
     */
    private String lowerClassName;
}
