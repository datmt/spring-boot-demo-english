package com.xkcoding.oauth.service;

import com.xkcoding.oauth.entity.SysUser;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;


/**
 * .
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2020-01-06 15:44
 */
public interface SysUserService extends UserDetailsService {
    /**
     * Query all users
     *
     * @return Users
     */
    List<SysUser> findAll();

    /**
     * Query users by id
     *
     * @param id id
     * @return Users
     */
    SysUser findById(Long id);

    /**
     * Create a user
     *
     * @param sysUser users
     */
    void createUser(SysUser sysUser);

    /**
     * Update users
     *
     * @param sysUser users
     */
    void updateUser(SysUser sysUser);

    /**
     * Update user password
     *
     * @param id user ID
     * @param password user password
     */
    void updatePassword(Long id, String password);

    /**
     * Delete users.
     *
     * @param id id
     */
    void deleteUser(Long id);
}
