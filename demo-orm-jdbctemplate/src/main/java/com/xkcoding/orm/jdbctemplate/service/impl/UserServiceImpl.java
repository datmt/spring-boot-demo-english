package com.xkcoding.orm.jdbctemplate.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.xkcoding.orm.jdbctemplate.constant.Const;
import com.xkcoding.orm.jdbctemplate.dao.UserDao;
import com.xkcoding.orm.jdbctemplate.entity.User;
import com.xkcoding.orm.jdbctemplate.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * User Service Implement
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-10-15 13:53
 */
@Service
public class UserServiceImpl implements IUserService {
    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Save users
     *
     * @param user user entity
     * @return Save successfully {@code true} Save failed {@code false}
     */
    @Override
    public Boolean save(User user) {
        String rawPass = user.getPassword();
        String salt = IdUtil.simpleUUID();
        String pass = SecureUtil.md5(rawPass + Const.SALT_PREFIX + salt);
        user.setPassword(pass);
        user.setSalt(salt);
        return userDao.insert(user) > 0;
    }

    /**
     * Delete users
     *
     * @param id primary key id
     * @return Delete successful {@code true} Delete failed {@code false}
     */
    @Override
    public Boolean delete(Long id) {
        return userDao.delete(id) > 0;
    }

    /**
     * Update users
     *
     * @param user user entity
     * @param id primary key id
     * @return update successful {@code true} Update failed {@code false}
     */
    @Override
    public Boolean update(User user, Long id) {
        User exist = getUser(id);
        if (StrUtil.isNotBlank(user.getPassword())) {
            String rawPass = user.getPassword();
            String salt = IdUtil.simpleUUID();
            String pass = SecureUtil.md5(rawPass + Const.SALT_PREFIX + salt);
            user.setPassword(pass);
            user.setSalt(salt);
        }
        BeanUtil.copyProperties(user, exist, CopyOptions.create().setIgnoreNullValue(true));
        exist.setLastUpdateTime(new DateTime());
        return userDao.update(exist, id) > 0;
    }

    /**
     * Get a single user
     *
     * @param id primary key id
     * @return Single user object
     */
    @Override
    public User getUser(Long id) {
        return userDao.findOneById(id);
    }

    /**
     * Get a list of users
     *
     * @param user user entity
     * @return User list
     */
    @Override
    public List<User> getUser(User user) {
        return userDao.findByExample(user);
    }
}
