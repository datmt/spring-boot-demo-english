package com.xkcoding.rbac.security.model.unionkey;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * <p>
 * Role-permission federation primary key
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-10 13:47
 */
@Data
@Embeddable
public class RolePermissionKey implements Serializable {
    private static final long serialVersionUID = 6850974328279713855L;

    /**
     * Role ID
     */
    @Column(name = "role_id")
    private Long roleId;

    /**
     * Permission id
     */
    @Column(name = "permission_id")
    private Long permissionId;
}
