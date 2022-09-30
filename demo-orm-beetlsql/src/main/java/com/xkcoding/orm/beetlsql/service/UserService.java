package com.xkcoding.orm.beetlsql.service;

import com.xkcoding.orm.beetlsql.entity.User;
import org.beetl.sql.core.engine.PageQuery;

import java.util.List;

/**
 * <p>
 * User Service
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-14 16:18
 */
public interface UserService {
    /**
     * New users
     *
     * @param user user
     * @return Saved users
     */
    User saveUser(User user);


    /**
     * Batch insert users
     *
     * @param users user list
     */
    void saveUserList(List<User> users);

    /**
     * Delete users based on primary keys
     *
     * @param id primary key
     */
    void deleteUser(Long id);

    /**
     * Update users
     *
     * @param user user
     * @return updated users
     */
    User updateUser(User user);

    /**
     * Query for a single user
     *
     * @param id primary key id
     * @return User information
     */
    User getUser(Long id);

    /**
     * Query the list of users
     *
     * @return User list
     */
    List<User> getUserList();

    /**
     * Paginated queries
     *
     * @param currentPage current page
     * @param pageSize per page
     * @return Paginated user list
     */
    PageQuery<User> getUserByPage(Integer currentPage, Integer pageSize);
}
