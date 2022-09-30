package com.xkcoding.rbac.security.model;

import com.xkcoding.rbac.security.model.unionkey.UserRoleKey;
import lombok.Data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <p>
 * User role association
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-10 11:18
 */
@Data
@Entity
@Table(name = "sec_user_role")
public class UserRole {
    /**
     * Primary key
     */
    @EmbeddedId
    private UserRoleKey id;
}
