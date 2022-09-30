package com.xkcoding.orm.mybatis.MapperAndPage.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * User entity class
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-08 14:14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orm_user")
public class User implements Serializable {
    private static final long serialVersionUID = -1840831686851699943L;

    /**
     * Primary key
     */
    @Id
    @KeySql(useGeneratedKeys = true)
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
    private Date createTime;

    /**
     * Last login time
     */
    private Date lastLoginTime;

    /**
     * Last updated
     */
    private Date lastUpdateTime;
}
