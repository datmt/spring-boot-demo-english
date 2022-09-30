package com.xkcoding.multi.datasource.mybatis.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xkcoding.multi.datasource.mybatis.SpringBootDemoMultiDatasourceMybatisApplicationTests;
import com.xkcoding.multi.datasource.mybatis.model.User;
import com.xkcoding.multi.datasource.mybatis.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * <p>
 * Test the master-slave data source
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-01-21 14:45
 */
@Slf4j
public class UserServiceImplTest extends SpringBootDemoMultiDatasourceMybatisApplicationTests {
    @Autowired
    private UserService userService;

    /**
     * Add from master and slave libraries
     */
    @Test
    public void addUser() {
        User userMaster = User.builder().name("主库添加").age(20).build();
        userService.addUser(userMaster);

        User userSlave = User.builder().name("从库添加").age(20).build();
        userService.save(userSlave);
    }

    /**
     * Query from the library
     */
    @Test
    public void testListUser() {
        List<User> list = userService.list(new QueryWrapper<>());
        log.info("【list】= {}", JSONUtil.toJsonStr(list));
    }
}
