package com.xkcoding.dynamic.datasource.controller;

import com.xkcoding.dynamic.datasource.mapper.UserMapper;
import com.xkcoding.dynamic.datasource.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * User Controller
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-09-04 16:40
 */
@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserController {
    private final UserMapper userMapper;

    /**
     * Get a list of users
     */
    @GetMapping("/user")
    public List<User> getUserList() {
        return userMapper.selectAll();
    }

}
