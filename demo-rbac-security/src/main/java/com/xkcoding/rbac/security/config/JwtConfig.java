package com.xkcoding.rbac.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 * JWT configuration
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-07 13:42
 */
@ConfigurationProperties(prefix = "jwt.config")
@Data
public class JwtConfig {
    /**
     * jwt encryption key, default: xkcoding.
     */
    private String key = "xkcoding";

    /**
     * jwt expiration time, default: 600000 {@code 10 minutes}.
     */
    private Long ttl = 600000L;

    /**
     * On Remember me after jwt expires, default 604800000 {@code 7 days}
     */
    private Long remember = 604800000L;
}
