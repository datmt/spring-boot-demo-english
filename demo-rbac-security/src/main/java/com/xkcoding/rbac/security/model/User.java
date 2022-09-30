package com.xkcoding.rbac.security.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>
 * Users
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-07 16:00
 */
@Data
@Entity
@Table(name = "sec_user")
public class User {

    /**
     * Primary key
     */
    @Id
    private Long id;

    /**
     * Username
     */
    private String username;

    /**
     * Password
     */
    private String password;

    /**
     * Nickname
     */
    private String nickname;

    /**
     * Mobile phone
     */
    private String phone;

    /**
     * Email
     */
    private String email;

    /**
     * Birthday
     */
    private Long birthday;

    /**
     * Gender, male-1, female-2
     */
    private Integer sex;

    /**
     * Status, enabled -1, disabled -0
     */
    private Integer status;

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
