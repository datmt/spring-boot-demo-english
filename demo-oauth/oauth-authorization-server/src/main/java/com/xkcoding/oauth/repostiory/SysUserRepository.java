package com.xkcoding.oauth.repostiory;

import com.xkcoding.oauth.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * User information repository.
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2020-01-06 13:08
 */
public interface SysUserRepository extends JpaRepository<SysUser, Long> {

    /**
     * Find users by username.
     *
     * @param username username
     * @return Results
     */
    Optional<SysUser> findFirstByUsername(String username);

}
