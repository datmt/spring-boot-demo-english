package com.xkcoding.orm.beetlsql.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.xkcoding.orm.beetlsql.dao.UserDao;
import com.xkcoding.orm.beetlsql.entity.User;
import com.xkcoding.orm.beetlsql.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.engine.PageQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * User Service
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-14 16:28
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * New users
     *
     * @param user user
     */
    @Override
    public User saveUser(User user) {
        userDao.insert(user, true);
        return user;
    }

    /**
     * Batch insert users
     *
     * @param users user list
     */
    @Override
    public void saveUserList(List<User> users) {
        userDao.insertBatch(users);
    }

    /**
     * Delete users based on primary keys
     *
     * @param id primary key
     */
    @Override
    public void deleteUser(Long id) {
        userDao.deleteById(id);
    }

    /**
     * Update users
     *
     * @param user user
     * @return updated users
     */
    @Override
    public User updateUser(User user) {
        if (ObjectUtil.isNull(user)) {
            throw new RuntimeException("用户id不能为null");
        }
        userDao.updateTemplateById(user);
        return userDao.single(user.getId());
    }

    /**
     * Query for a single user
     *
     * @param id primary key id
     * @return User information
     */
    @Override
    public User getUser(Long id) {
        return userDao.single(id);
    }

    /**
     * Query the list of users
     *
     * @return User list
     */
    @Override
    public List<User> getUserList() {
        return userDao.all();
    }

    /**
     * Paginated queries
     *
     * @param currentPage current page
     * @param pageSize per page
     * @return Paginated user list
     */
    @Override
    public PageQuery<User> getUserByPage(Integer currentPage, Integer pageSize) {
        return userDao.createLambdaQuery().page(currentPage, pageSize);
    }
}
