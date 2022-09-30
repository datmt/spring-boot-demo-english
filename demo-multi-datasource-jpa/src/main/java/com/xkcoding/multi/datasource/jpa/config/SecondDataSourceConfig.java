package com.xkcoding.multi.datasource.jpa.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * <p>
 * JPA Multiple Data Source Configuration - Secondary Data Source
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-01-17 15:58
 */
@Configuration
public class SecondDataSourceConfig {

    /**
     * Scan the configuration information at the beginning of spring.datasource.second
     *
     * @return data source configuration information
     */
    @Bean(name = "secondDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.second")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * Get the main library data source object
     *
     * @param dataSourceProperties injects a bean called secondDataSourceProperties
     * @return data source object
     */
    @Bean(name = "secondDataSource")
    public DataSource dataSource(@Qualifier("secondDataSourceProperties") DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    /**
     * This method is only used when a JdbcTemplate object is required
     *
     * @param dataSource injects a bean named secondDataSource
     * @return data source JdbcTemplate object
     */
    @Bean(name = "secondJdbcTemplate")
    public JdbcTemplate jdbcTemplate(@Qualifier("secondDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
