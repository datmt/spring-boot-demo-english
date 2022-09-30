package com.xkcoding.rbac.security.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * Common paging parameters are returned
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-11 20:26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {
    private static final long serialVersionUID = 3420391142991247367L;

    /**
     * Current page data
     */
    private List<T> rows;

    /**
     * Total number of items
     */
    private Long total;

    public static <T> PageResult of(List<T> rows, Long total) {
        return new PageResult<>(rows, total);
    }
}
