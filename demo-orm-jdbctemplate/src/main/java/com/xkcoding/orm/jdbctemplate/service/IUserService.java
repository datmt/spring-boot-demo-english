package com.xkcoding.orm.jdbctemplate.service;

import com.xkcoding.orm.jdbctemplate.entity.User;

import java.util.List;

/**
 * <p>
 * User Service
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-10-15 13:51
 */
public interface IUserService {
    /**
     * Save users
     *
     * @param user user entity
     * @return Save successfully {@code true} Save failed {@code false}
     */
    Boolean save(User user);

    /**
     * Delete users
     *
     * @param id primary key id
     * @return Delete successful {@code true} Delete failed {@code false}
     */
    Boolean delete(Long id);

    /**
     * Update users
     *
     * @param user user entity
     * @param id primary key id
     * @return update successful {@code true} Update failed {@code false}
     */
    Boolean update(User user, Long id);

    /**
     * Get a single user
     *
     * @param id primary key id
     * @return Single user object
     */
    User getUser(Long id);

    /**
     * Get a list of users
     *
     * @param user user entity
     * @return User list
     */
    List<User> getUser(User user);

}
