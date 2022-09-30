package com.xkcoding.multi.datasource.mybatis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xkcoding.multi.datasource.mybatis.model.User;

/**
 * <p>
 * Data service layer
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-01-21 14:31
 */
public interface UserService extends IService<User> {

    /**
     * Add User
     *
     * @param user user
     */
    void addUser(User user);
}
