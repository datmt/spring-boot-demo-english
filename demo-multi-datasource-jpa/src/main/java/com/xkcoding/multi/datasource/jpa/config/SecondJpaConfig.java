package com.xkcoding.multi.datasource.jpa.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
 * JPA Multiple Data Source Configuration - Sub-JPA configuration
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-01-17 16:54
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    repository package name
    basePackages = SecondJpaConfig.REPOSITORY_PACKAGE,
    The entity manages the bean name
    entityManagerFactoryRef = "secondEntityManagerFactory",
    Transaction management bean name
    transactionManagerRef = "secondTransactionManager")
public class SecondJpaConfig {
    static final String REPOSITORY_PACKAGE = "com.xkcoding.multi.datasource.jpa.repository.second";
    private static final String ENTITY_PACKAGE = "com.xkcoding.multi.datasource.jpa.entity.second";


    /**
     * Scan configuration information beginning with spring.jpa.second
     *
     * @return jpa configuration information
     */
    @Bean(name = "secondJpaProperties")
    @ConfigurationProperties(prefix = "spring.jpa.second")
    public JpaProperties jpaProperties() {
        return new JpaProperties();
    }

    /**
     * Get the master library entity management factory object
     *
     * @param secondDataSource injects a data source named secondDataSource
     * @param jpaProperties injects jpa configuration information called secondJpaProperties
     * @param builder injected into EntityManagerFactoryBuilder
     * @return Entity management factory objects
     */
    @Bean(name = "secondEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier("secondDataSource") DataSource secondDataSource, @Qualifier("secondJpaProperties") JpaProperties jpaProperties, EntityManagerFactoryBuilder builder) {
        return builder
            Set up the data source
            .dataSource(secondDataSource)
            Set the jpa configuration
            .properties(jpaProperties.getProperties())
            Set the entity package name
            .packages(ENTITY_PACKAGE)
            Sets the persistence unit name that specifies the data source when @PersistenceContext annotation gets EntityManager
            .persistenceUnit("secondPersistenceUnit").build();
    }

    /**
     * Get the entity management object
     *
     * @param factory injects a bean called secondEntityManagerFactory
     * @return Entity management objects
     */
    @Bean(name = "secondEntityManager")
    public EntityManager entityManager(@Qualifier("secondEntityManagerFactory") EntityManagerFactory factory) {
        return factory.createEntityManager();
    }

    /**
     * Get the main library transaction management object
     *
     * @param factory injects a bean called secondEntityManagerFactory
     * @return Transaction management objects
     */
    @Bean(name = "secondTransactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("secondEntityManagerFactory") EntityManagerFactory factory) {
        return new JpaTransactionManager(factory);
    }

}
