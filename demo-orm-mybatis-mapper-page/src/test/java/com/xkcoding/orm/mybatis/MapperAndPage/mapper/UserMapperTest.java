package com.xkcoding.orm.mybatis.MapperAndPage.mapper;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xkcoding.orm.mybatis.MapperAndPage.SpringBootDemoOrmMybatisMapperPageApplicationTests;
import com.xkcoding.orm.mybatis.MapperAndPage.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * UserMapper test
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-08 14:25
 */
@Slf4j
public class UserMapperTest extends SpringBootDemoOrmMybatisMapperPageApplicationTests {

    @Autowired
    private UserMapper userMapper;

    /**
     * Test Universal Mapper - Save
     */
    @Test
    public void testInsert() {
        String salt = IdUtil.fastSimpleUUID();
        User testSave3 = User.builder().name("testSave3").password(SecureUtil.md5("123456" + salt)).salt(salt).email("testSave3@xkcoding.com").phoneNumber("17300000003").status(1).lastLoginTime(new DateTime()).createTime(new DateTime()).lastUpdateTime(new DateTime()).build();
        userMapper.insertUseGeneratedKeys(testSave3);
        Assert.assertNotNull(testSave3.getId());
        log.debug("【测试主键回写#testSave3.getId()】= {}", testSave3.getId());
    }

    /**
     * Test Universal Mapper - Batch Save
     */
    @Test
    public void testInsertList() {
        List<User> userList = Lists.newArrayList();
        for (int i = 4; i < 14; i++) {
            String salt = IdUtil.fastSimpleUUID();
            User user = User.builder().name("testSave" + i).password(SecureUtil.md5("123456" + salt)).salt(salt).email("testSave" + i + "@xkcoding.com").phoneNumber("1730000000" + i).status(1).lastLoginTime(new DateTime()).createTime(new DateTime()).lastUpdateTime(new DateTime()).build();
            userList.add(user);
        }
        int i = userMapper.insertList(userList);
        Assert.assertEquals(userList.size(), i);
        List<Long> ids = userList.stream().map(User::getId).collect(Collectors.toList());
        log.debug("【测试主键回写#userList.ids】= {}", ids);
    }

    /**
     * Test Universal Mapper - Delete
     */
    @Test
    public void testDelete() {
        Long primaryKey = 1L;
        int i = userMapper.deleteByPrimaryKey(primaryKey);
        Assert.assertEquals(1, i);
        User user = userMapper.selectByPrimaryKey(primaryKey);
        Assert.assertNull(user);
    }

    /**
     * Test Universal Mapper - Update
     */
    @Test
    public void testUpdate() {
        Long primaryKey = 1L;
        User user = userMapper.selectByPrimaryKey(primaryKey);
        user.setName("通用Mapper名字更新");
        int i = userMapper.updateByPrimaryKeySelective(user);
        Assert.assertEquals(1, i);
        User update = userMapper.selectByPrimaryKey(primaryKey);
        Assert.assertNotNull(update);
        Assert.assertEquals("通用Mapper名字更新", update.getName());
        log.debug("【update】= {}", update);
    }

    /**
     * Test generic Mapper - query single
     */
    @Test
    public void testQueryOne() {
        User user = userMapper.selectByPrimaryKey(1L);
        Assert.assertNotNull(user);
        log.debug("【user】= {}", user);
    }

    /**
     * Test Universal Mapper - Query All
     */
    @Test
    public void testQueryAll() {
        List<User> users = userMapper.selectAll();
        Assert.assertTrue(CollUtil.isNotEmpty(users));
        log.debug("【users】= {}", users);
    }

    /**
     * Test Pagination Assistant - Pagination Sort Query
     */
    @Test
    public void testQueryByPageAndSort() {
        initData();
        int currentPage = 1;
        int pageSize = 5;
        String orderBy = "id desc";
        int count = userMapper.selectCount(null);
        PageHelper.startPage(currentPage, pageSize, orderBy);
        List<User> users = userMapper.selectAll();
        PageInfo<User> userPageInfo = new PageInfo<>(users);
        Assert.assertEquals(5, userPageInfo.getSize());
        Assert.assertEquals(count, userPageInfo.getTotal());
        log.debug("【userPageInfo】= {}", userPageInfo);
    }

    /**
     * Test generic Mapper - conditional query
     */
    @Test
    public void testQueryByCondition() {
        initData();
        Example example = new Example(User.class);
        filtration
        example.createCriteria().andLike("name", "%Save1%").orEqualTo("phoneNumber", "17300000001");
        sort
        example.setOrderByClause("id desc");
        int count = userMapper.selectCountByExample(example);
        pagination
        PageHelper.startPage(1, 3);
        Inquire
        List<User> userList = userMapper.selectByExample(example);
        PageInfo<User> userPageInfo = new PageInfo<>(userList);
        Assert.assertEquals(3, userPageInfo.getSize());
        Assert.assertEquals(count, userPageInfo.getTotal());
        log.debug("【userPageInfo】= {}", userPageInfo);
    }

    /**
     * Initialize data
     */
    private void initData() {
        testInsertList();
    }

}
