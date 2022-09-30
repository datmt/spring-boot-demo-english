package com.xkcoding.rbac.security.repository;

import com.xkcoding.rbac.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 * User DAO
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-07 16:18
 */
public interface UserDao extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    /**
     * Query users based on user name, email address, and mobile phone number
     *
     * @param username username
     * @param email address
     * @param phone number
     * @return User information
     */
    Optional<User> findByUsernameOrEmailOrPhone(String username, String email, String phone);

    /**
     * Query the list of users based on the list of user names
     *
     * @param usernameList list of usernames
     * @return User list
     */
    List<User> findByUsernameIn(List<String> usernameList);
}
