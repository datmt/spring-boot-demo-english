package com.xkcoding.multi.datasource.mybatis.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xkcoding.multi.datasource.mybatis.mapper.UserMapper;
import com.xkcoding.multi.datasource.mybatis.model.User;
import com.xkcoding.multi.datasource.mybatis.service.UserService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Data Services Layer implementation
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-01-21 14:37
 */
@Service
@DS("slave")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * On the class {@code @DS("slave")} represents the default slave library, and on the method, write {@code @DS("master")} represents the default master library
     *
     * @param user user
     */
    @DS("master")
    @Override
    public void addUser(User user) {
        baseMapper.insert(user);
    }
}
