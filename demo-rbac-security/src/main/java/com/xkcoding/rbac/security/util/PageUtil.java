package com.xkcoding.rbac.security.util;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import com.xkcoding.rbac.security.common.Consts;
import com.xkcoding.rbac.security.payload.PageCondition;
import org.springframework.data.domain.PageRequest;

/**
 * <p>
 * Pagination tool class
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-12 18:09
 */
public class PageUtil {
    /**
     * Verify the paging parameters, NULL, set the default value of the paging parameters
     *
     * @param condition query parameter
     * @param clazz class
     * @param <T>       {@link PageCondition}
     */
    public static <T extends PageCondition> void checkPageCondition(T condition, Class<T> clazz) {
        if (ObjectUtil.isNull(condition)) {
            condition = ReflectUtil.newInstance(clazz);
        }
        Verify the paging parameters
        if (ObjectUtil.isNull(condition.getCurrentPage())) {
            condition.setCurrentPage(Consts.DEFAULT_CURRENT_PAGE);
        }
        if (ObjectUtil.isNull(condition.getPageSize())) {
            condition.setPageSize(Consts.DEFAULT_PAGE_SIZE);
        }
    }

    /**
     * Build {@link PageRequest} based on paging parameters
     *
     * @param condition query parameter
     * @param <T>       {@link PageCondition}
     * @return {@link PageRequest}
     */
    public static <T extends PageCondition> PageRequest ofPageRequest(T condition) {
        return PageRequest.of(condition.getCurrentPage(), condition.getPageSize());
    }
}
