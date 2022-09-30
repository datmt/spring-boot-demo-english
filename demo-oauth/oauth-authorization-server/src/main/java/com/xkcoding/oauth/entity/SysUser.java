package com.xkcoding.oauth.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Set;

/**
 * User entity.
 * Avoid entity class coupling, so do not implement the {@link UserDetails} interface
 * Because there is and only needs this interface when logging in to load users
 * Let's manually build a default implementation of {@link User}
 * For a way to implement the interface, see {@link SysClientDetails}
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2020-01-06 12:41
 */
@Data
@Table
@Entity
@EqualsAndHashCode(exclude = "roles")
@ToString(exclude = "roles")
public class SysUser {

    /**
     * Primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Username.
     */
    private String username;

    /**
     * Password.
     */
    private String password;

    /**
     * All roles of the current user.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "sys_user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<SysRole> roles;
}
