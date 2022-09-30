package com.xkcoding.rbac.security.vo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.xkcoding.rbac.security.common.Consts;
import com.xkcoding.rbac.security.model.User;
import lombok.Data;

/**
 * <p>
 * Online user VO
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-12 00:58
 */
@Data
public class OnlineUser {

    /**
     * Primary key
     */
    private Long id;

    /**
     * Username
     */
    private String username;

    /**
     * Nickname
     */
    private String nickname;

    /**
     * Mobile phone
     */
    private String phone;

    /**
     * Email
     */
    private String email;

    /**
     * Birthday
     */
    private Long birthday;

    /**
     * Gender, male-1, female-2
     */
    private Integer sex;

    public static OnlineUser create(User user) {
        OnlineUser onlineUser = new OnlineUser();
        BeanUtil.copyProperties(user, onlineUser);
        Desensitization
        onlineUser.setPhone(StrUtil.hide(user.getPhone(), 3, 7));
        onlineUser.setEmail(StrUtil.hide(user.getEmail(), 1, StrUtil.indexOfIgnoreCase(user.getEmail(), Consts.SYMBOL_EMAIL)));
        return onlineUser;
    }
}
