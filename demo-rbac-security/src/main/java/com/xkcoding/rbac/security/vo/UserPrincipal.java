package com.xkcoding.rbac.security.vo;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xkcoding.rbac.security.common.Consts;
import com.xkcoding.rbac.security.model.Permission;
import com.xkcoding.rbac.security.model.Role;
import com.xkcoding.rbac.security.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * Custom User
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-10 15:09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipal implements UserDetails {
    /**
     * Primary key
     */
    private Long id;

    /**
     * Username
     */
    private String username;

    /**
     * Password
     */
    @JsonIgnore
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
    private Long createTime;

    /**
     * Update time
     */
    private Long updateTime;

    /**
     * List of user roles
     */
    private List<String> roles;

    /**
     * List of user permissions
     */
    private Collection<? extends GrantedAuthority> authorities;

    public static UserPrincipal create(User user, List<Role> roles, List<Permission> permissions) {
        List<String> roleNames = roles.stream().map(Role::getName).collect(Collectors.toList());

        List<GrantedAuthority> authorities = permissions.stream().filter(permission -> StrUtil.isNotBlank(permission.getPermission())).map(permission -> new SimpleGrantedAuthority(permission.getPermission())).collect(Collectors.toList());

        return new UserPrincipal(user.getId(), user.getUsername(), user.getPassword(), user.getNickname(), user.getPhone(), user.getEmail(), user.getBirthday(), user.getSex(), user.getStatus(), user.getCreateTime(), user.getUpdateTime(), roleNames, authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Objects.equals(this.status, Consts.ENABLE);
    }
}
