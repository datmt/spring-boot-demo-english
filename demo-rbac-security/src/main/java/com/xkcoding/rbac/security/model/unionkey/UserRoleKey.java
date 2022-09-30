package com.xkcoding.rbac.security.model.unionkey;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * <p>
 * User-role federated primary key
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-10 11:20
 */
@Embeddable
@Data
public class UserRoleKey implements Serializable {
    private static final long serialVersionUID = 5633412144183654743L;

    /**
     * User ID
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * Role ID
     */
    @Column(name = "role_id")
    private Long roleId;
}
