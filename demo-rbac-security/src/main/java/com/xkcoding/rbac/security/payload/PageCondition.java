package com.xkcoding.rbac.security.payload;

import lombok.Data;

/**
 * <p>
 * Paging request parameters
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-12 18:05
 */
@Data
public class PageCondition {
    /**
     * Current page number
     */
    private Integer currentPage;

    /**
     * Number of articles per page
     */
    private Integer pageSize;

}
