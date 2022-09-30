package com.xkcoding.orm.jdbctemplate.entity;

import com.xkcoding.orm.jdbctemplate.annotation.Column;
import com.xkcoding.orm.jdbctemplate.annotation.Pk;
import com.xkcoding.orm.jdbctemplate.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * User entity class
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-10-15 10:45
 */
@Data
@Table(name = "orm_user")
public class User implements Serializable {
    /**
     * Primary key
     */
    @Pk
    private Long id;

    /**
     * Username
     */
    private String name;

    /**
     * Encrypted password
     */
    private String password;

    /**
     * Salt used for encryption
     */
    private String salt;

    /**
     * Email
     */
    private String email;

    /**
     * Mobile phone number
     */
    @Column(name = "phone_number")
    private String phoneNumber;

    /**
     * Status, -1: Tombstone, 0: Disabled, 1: Enabled
     */
    private Integer status;

    /**
     * Creation time
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * Last login time
     */
    @Column(name = "last_login_time")
    private Date lastLoginTime;

    /**
     * Last updated
     */
    @Column(name = "last_update_time")
    private Date lastUpdateTime;
}
