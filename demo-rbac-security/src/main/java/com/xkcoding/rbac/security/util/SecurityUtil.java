package com.xkcoding.rbac.security.util;

import cn.hutool.core.util.ObjectUtil;
import com.xkcoding.rbac.security.common.Consts;
import com.xkcoding.rbac.security.vo.UserPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * <p>
 * Spring Security Tools class
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-12 18:30
 */
public class SecurityUtil {
    /**
     * Get the username of the currently logged-in user
     *
     * @return The user name of the currently logged-on user
     */
    public static String getCurrentUsername() {
        UserPrincipal currentUser = getCurrentUser();
        return ObjectUtil.isNull(currentUser) ? Consts.ANONYMOUS_NAME : currentUser.getUsername();
    }

    /**
     * Get the current logged-in user information
     *
     * @return The current logged-on user information, when logging in anonymously, is null
     */
    public static UserPrincipal getCurrentUser() {
        Object userInfo = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userInfo instanceof UserDetails) {
            return (UserPrincipal) userInfo;
        }
        return null;
    }
}
