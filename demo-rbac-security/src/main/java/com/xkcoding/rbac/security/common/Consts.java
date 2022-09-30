package com.xkcoding.rbac.security.common;

/**
 * <p>
 * Constant pool
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-10 15:03
 */
public interface Consts {
    /**
     * Enabled
     */
    Integer ENABLE = 1;
    /**
     * Disabled
     */
    Integer DISABLE = 0;

    /**
     * Page
     */
    Integer PAGE = 1;

    /**
     * Button
     */
    Integer BUTTON = 2;

    /**
     * JWT's key prefix saved in Redis
     */
    String REDIS_JWT_KEY_PREFIX = "security:jwt:";

    /**
     * Asterisk
     */
    String SYMBOL_STAR = "*";

    /**
     * Mailbox symbol
     */
    String SYMBOL_EMAIL = "@";

    /**
     * Default current page number
     */
    Integer DEFAULT_CURRENT_PAGE = 1;

    /**
     * Default number of articles per page
     */
    Integer DEFAULT_PAGE_SIZE = 10;

    /**
     * Anonymous user username
     */
    String ANONYMOUS_NAME = "匿名用户";
}
