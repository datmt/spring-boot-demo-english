package com.xkcoding.orm.jdbctemplate.dao;

import com.xkcoding.orm.jdbctemplate.dao.base.BaseDao;
import com.xkcoding.orm.jdbctemplate.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * User Dao
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-10-15 11:15
 */
@Repository
public class UserDao extends BaseDao<User, Long> {

    @Autowired
    public UserDao(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    /**
     * Save users
     *
     * @param user user object
     * @return operation affects the number of rows
     */
    public Integer insert(User user) {
        return super.insert(user, true);
    }

    /**
     * Delete users based on primary keys
     *
     * @param id primary key id
     * @return operation affects the number of rows
     */
    public Integer delete(Long id) {
        return super.deleteById(id);
    }

    /**
     * Update users
     *
     * @param user user object
     * @param id primary key id
     * @return operation affects the number of rows
     */
    public Integer update(User user, Long id) {
        return super.updateById(user, id, true);
    }

    /**
     * Get users based on primary key
     *
     * @param id primary key id
     * @return the user corresponding to the id
     */
    public User selectById(Long id) {
        return super.findOneById(id);
    }

    /**
     * Get a list of users based on query criteria
     *
     * @param user query criteria
     * @return User list
     */
    public List<User> selectUserList(User user) {
        return super.findByExample(user);
    }
}
