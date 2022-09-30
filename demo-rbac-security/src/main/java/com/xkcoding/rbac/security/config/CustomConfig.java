package com.xkcoding.rbac.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 * Custom configuration
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-13 10:56
 */
@ConfigurationProperties(prefix = "custom.config")
@Data
public class CustomConfig {
    /**
     * Addresses that do not require interception
     */
    private IgnoreConfig ignores;
}
