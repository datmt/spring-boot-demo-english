package com.xkcoding.rbac.security.service;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.xkcoding.rbac.security.common.Consts;
import com.xkcoding.rbac.security.common.PageResult;
import com.xkcoding.rbac.security.model.User;
import com.xkcoding.rbac.security.payload.PageCondition;
import com.xkcoding.rbac.security.repository.UserDao;
import com.xkcoding.rbac.security.util.RedisUtil;
import com.xkcoding.rbac.security.util.SecurityUtil;
import com.xkcoding.rbac.security.vo.OnlineUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * Monitor Service
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-12 00:55
 */
@Slf4j
@Service
public class MonitorService {
    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserDao userDao;

    /**
     * Pagination list of online users
     *
     * @param pageCondition paging parameter
     * @return Pagination list of online users
     */
    public PageResult<OnlineUser> onlineUser(PageCondition pageCondition) {
        PageResult<String> keys = redisUtil.findKeysForPage(Consts.REDIS_JWT_KEY_PREFIX + Consts.SYMBOL_STAR, pageCondition.getCurrentPage(), pageCondition.getPageSize());
        List<String> rows = keys.getRows();
        Long total = keys.getTotal();

        Gets a list of user names based on the redis middle key
        List<String> usernameList = rows.stream().map(s -> StrUtil.subAfter(s, Consts.REDIS_JWT_KEY_PREFIX, true)).collect(Collectors.toList());
        Query user information based on user name
        List<User> userList = userDao.findByUsernameIn(usernameList);

        Encapsulate online user information
        List<OnlineUser> onlineUserList = Lists.newArrayList();
        userList.forEach(user -> onlineUserList.add(OnlineUser.create(user)));

        return new PageResult<>(onlineUserList, total);
    }

    /**
     * Kick out online users
     *
     * @param names list of usernames
     */
    public void kickout(List<String> names) {
        Clear the JWT information in Redis
        List<String> redisKeys = names.parallelStream().map(s -> Consts.REDIS_JWT_KEY_PREFIX + s).collect(Collectors.toList());
        redisUtil.delete(redisKeys);

        Gets the current user name
        String currentUsername = SecurityUtil.getCurrentUsername();
        names.parallelStream().forEach(name -> {
            TODO: Notifies the user who has been kicked out has been kicked out by the currently logged-in user,
            Later considerations are to use the websocket implementation, and the specific pseudocode implementation is as follows.
            String message = "You have been manually offline by a user [" + currentUsername + "]!";
            log.debug("用户【{}】被用户【{}】手动下线！", name, currentUsername);
        });
    }
}
