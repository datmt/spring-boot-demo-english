package com.xkcoding.codegen.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * Paginated result set
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-03-22 11:24
 */
@Data
@AllArgsConstructor
public class PageResult<T> {
    /**
     * Total number of items
     */
    private Long total;

    /**
     * Page number
     */
    private int pageNumber;

    /**
     * Number of results per page
     */
    private int pageSize;

    /**
     * Result set
     */
    private List<T> list;
}
