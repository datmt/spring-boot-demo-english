package com.xkcoding.orm.mybatis.plus.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

import static com.baomidou.mybatisplus.annotation.FieldFill.INSERT;
import static com.baomidou.mybatisplus.annotation.FieldFill.INSERT_UPDATE;

/**
 * <p>
 * User entity class
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-08 16:49
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("orm_user")
public class User implements Serializable {
    private static final long serialVersionUID = -1840831686851699943L;

    /**
     * Primary key
     */
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
    private String phoneNumber;

    /**
     * Status, -1: Tombstone, 0: Disabled, 1: Enabled
     */
    private Integer status;

    /**
     * Creation time
     */
    @TableField(fill = INSERT)
    private Date createTime;

    /**
     * Last login time
     */
    private Date lastLoginTime;

    /**
     * Last updated
     */
    @TableField(fill = INSERT_UPDATE)
    private Date lastUpdateTime;
}
