package com.xkcoding.orm.mybatis.plus.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * <p>
 * mybatis-plus configuration
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-08 17:29
 */
@Configuration
@MapperScan(basePackages = {"com.xkcoding.orm.mybatis.plus.mapper"})
@EnableTransactionManagement
public class MybatisPlusConfig {
    /**
     * Performance analysis interceptor, not recommended for production
     */
    @Bean
    public PerformanceInterceptor performanceInterceptor() {
        return new PerformanceInterceptor();
    }

    /**
     * Pagination plugin
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}
