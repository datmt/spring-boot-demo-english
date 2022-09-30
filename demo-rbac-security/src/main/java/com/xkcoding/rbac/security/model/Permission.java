package com.xkcoding.rbac.security.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>
 * Permissions
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-07 16:04
 */
@Data
@Entity
@Table(name = "sec_permission")
public class Permission {
    /**
     * Primary key
     */
    @Id
    private Long id;

    /**
     * Permission name
     */
    private String name;

    /**
     * Represents the front-end routing address when the type is Page, and represents the back-end interface address when the type is Button
     */
    private String url;

    /**
     * Permission type, page-1, button-2
     */
    private Integer type;

    /**
     * Permission expression
     */
    private String permission;

    /**
     * Backend interface access mode
     */
    private String method;

    /**
     * Sort
     */
    private Integer sort;

    /**
     * Parent ID
     */
    @Column(name = "parent_id")
    private Long parentId;
}
