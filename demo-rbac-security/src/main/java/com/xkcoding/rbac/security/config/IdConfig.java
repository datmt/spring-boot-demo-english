package com.xkcoding.rbac.security.config;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * Snowflake primary key generator
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-10 11:28
 */
@Configuration
public class IdConfig {
    /**
     * Snowflake generator
     */
    @Bean
    public Snowflake snowflake() {
        return IdUtil.createSnowflake(1, 1);
    }
}
