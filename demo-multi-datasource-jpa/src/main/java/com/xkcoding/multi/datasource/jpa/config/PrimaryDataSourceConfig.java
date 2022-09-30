package com.xkcoding.multi.datasource.jpa.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * <p>
 * JPA Multiple Data Source Configuration - Primary Data Source
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-01-17 15:58
 */
@Configuration
public class PrimaryDataSourceConfig {

    /**
     * Scan configuration information starting with spring.datasource.primary
     *
     * @return data source configuration information
     */
    @Primary
    @Bean(name = "primaryDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * Get the main library data source object
     *
     * @param dataSourceProperties injects a bean called primaryDataSourceProperties
     * @return data source object
     */
    @Primary
    @Bean(name = "primaryDataSource")
    public DataSource dataSource(@Qualifier("primaryDataSourceProperties") DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    /**
     * This method is only used when a JdbcTemplate object is required
     *
     * @param dataSource injects a bean called primaryDataSource
     * @return data source JdbcTemplate object
     */
    @Primary
    @Bean(name = "primaryJdbcTemplate")
    public JdbcTemplate jdbcTemplate(@Qualifier("primaryDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
