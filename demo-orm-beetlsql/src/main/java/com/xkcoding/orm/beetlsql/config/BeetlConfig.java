package com.xkcoding.orm.beetlsql.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

/**
 * <p>
 * Beetl data source configuration
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-14 17:15
 */
@Configuration
public class BeetlConfig {

    /**
     * Beettl needs to display the configuration data source to start the project, big pit, remember!
     */
    @Bean(name = "datasource")
    public DataSource getDataSource(Environment env) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
        dataSource.setJdbcUrl(env.getProperty("spring.datasource.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.password"));
        return dataSource;
    }
}
