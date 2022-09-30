package com.xkcoding.rbac.security.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>
 * Roles
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-07 15:45
 */
@Data
@Entity
@Table(name = "sec_role")
public class Role {
    /**
     * Primary key
     */
    @Id
    private Long id;

    /**
     * Role name
     */
    private String name;

    /**
     * Description
     */
    private String description;

    /**
     * Creation time
     */
    @Column(name = "create_time")
    private Long createTime;

    /**
     * Update time
     */
    @Column(name = "update_time")
    private Long updateTime;
}
