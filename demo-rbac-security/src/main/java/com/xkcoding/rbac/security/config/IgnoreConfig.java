package com.xkcoding.rbac.security.config;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * Ignore configuration
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-17 17:37
 */
@Data
public class IgnoreConfig {
    /**
     * URL formats that need to be ignored, regardless of the request method
     */
    private List<String> pattern = Lists.newArrayList();

    /**
     * GET requests that need to be ignored
     */
    private List<String> get = Lists.newArrayList();

    /**
     * POST requests that need to be ignored
     */
    private List<String> post = Lists.newArrayList();

    /**
     * DELETE requests that need to be ignored
     */
    private List<String> delete = Lists.newArrayList();

    /**
     * PUT requests that need to be ignored
     */
    private List<String> put = Lists.newArrayList();

    /**
     * HEAD requests that need to be ignored
     */
    private List<String> head = Lists.newArrayList();

    /**
     * PATCH requests that need to be ignored
     */
    private List<String> patch = Lists.newArrayList();

    /**
     * OPTIONS requests that need to be ignored
     */
    private List<String> options = Lists.newArrayList();

    /**
     * TRACE requests that need to be ignored
     */
    private List<String> trace = Lists.newArrayList();
}
