package com.xkcoding.rbac.security.repository;

import com.xkcoding.rbac.security.model.RolePermission;
import com.xkcoding.rbac.security.model.unionkey.RolePermissionKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * <p>
 * Role-Permission DAO
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-10 13:45
 */
public interface RolePermissionDao extends JpaRepository<RolePermission, RolePermissionKey>, JpaSpecificationExecutor<RolePermission> {
}
