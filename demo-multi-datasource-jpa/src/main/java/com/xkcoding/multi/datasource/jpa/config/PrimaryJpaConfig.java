package com.xkcoding.multi.datasource.jpa.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * <p>
 * JPA Multi-Data Source Configuration - Primary JPA configuration
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-01-17 16:54
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    repository package name
    basePackages = PrimaryJpaConfig.REPOSITORY_PACKAGE,
    The entity manages the bean name
    entityManagerFactoryRef = "primaryEntityManagerFactory",
    Transaction management bean name
    transactionManagerRef = "primaryTransactionManager")
public class PrimaryJpaConfig {
    static final String REPOSITORY_PACKAGE = "com.xkcoding.multi.datasource.jpa.repository.primary";
    private static final String ENTITY_PACKAGE = "com.xkcoding.multi.datasource.jpa.entity.primary";


    /**
     * Scan configuration information starting with spring.jpa.primary
     *
     * @return jpa configuration information
     */
    @Primary
    @Bean(name = "primaryJpaProperties")
    @ConfigurationProperties(prefix = "spring.jpa.primary")
    public JpaProperties jpaProperties() {
        return new JpaProperties();
    }

    /**
     * Get the master library entity management factory object
     *
     * @param primaryDataSource injects a data source called primaryDataSource
     * @param jpaProperties injects jpa configuration information called primaryJpaProperties
     * @param builder injected into EntityManagerFactoryBuilder
     * @return Entity management factory objects
     */
    @Primary
    @Bean(name = "primaryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier("primaryDataSource") DataSource primaryDataSource, @Qualifier("primaryJpaProperties") JpaProperties jpaProperties, EntityManagerFactoryBuilder builder) {
        return builder
            Set up the data source
            .dataSource(primaryDataSource)
            Set the jpa configuration
            .properties(jpaProperties.getProperties())
            Set the entity package name
            .packages(ENTITY_PACKAGE)
            Sets the persistence unit name that specifies the data source when @PersistenceContext annotation gets EntityManager
            .persistenceUnit("primaryPersistenceUnit").build();
    }

    /**
     * Get the entity management object
     *
     * @param factory injects a bean called primaryEntityManagerFactory
     * @return Entity management objects
     */
    @Primary
    @Bean(name = "primaryEntityManager")
    public EntityManager entityManager(@Qualifier("primaryEntityManagerFactory") EntityManagerFactory factory) {
        return factory.createEntityManager();
    }

    /**
     * Get the main library transaction management object
     *
     * @param factory injects a bean called primaryEntityManagerFactory
     * @return Transaction management objects
     */
    @Primary
    @Bean(name = "primaryTransactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("primaryEntityManagerFactory") EntityManagerFactory factory) {
        return new JpaTransactionManager(factory);
    }

}
