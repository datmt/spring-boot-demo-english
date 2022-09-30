package com.xkcoding.rbac.security.model;

import com.xkcoding.rbac.security.model.unionkey.RolePermissionKey;
import lombok.Data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <p>
 * Role-permissions
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-10 13:46
 */
@Data
@Entity
@Table(name = "sec_role_permission")
public class RolePermission {
    /**
     * Primary key
     */
    @EmbeddedId
    private RolePermissionKey id;
}
