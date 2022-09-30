package com.xkcoding.dynamic.datasource.datasource;

import com.xkcoding.dynamic.datasource.model.DatasourceConfig;
import com.xkcoding.dynamic.datasource.utils.SpringUtil;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * <p>
 * Dynamic data sources
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-09-04 10:41
 */
@Slf4j
public class DynamicDataSource extends HikariDataSource {
    @Override
    public Connection getConnection() throws SQLException {
        Gets the current data source ID
        Long id = DatasourceConfigContextHolder.getCurrentDatasourceConfig();
        Get the data source based on the current id
        HikariDataSource datasource = DatasourceHolder.INSTANCE.getDatasource(id);

        if (null == datasource) {
            datasource = initDatasource(id);
        }

        return datasource.getConnection();
    }

    /**
     * Initialize the data source
     *
     * @param id data source id
     * @return Data source
     */
    private HikariDataSource initDatasource(Long id) {
        HikariDataSource dataSource = new HikariDataSource();

        Determine whether it is the default data source
        if (DatasourceHolder.DEFAULT_ID.equals(id)) {
            The default data source is generated according to the application.yml configuration
            DataSourceProperties properties = SpringUtil.getBean(DataSourceProperties.class);
            dataSource.setJdbcUrl(properties.getUrl());
            dataSource.setUsername(properties.getUsername());
            dataSource.setPassword(properties.getPassword());
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        } else {
            If it is not the default data source, the configuration of the data source corresponding to the id is obtained through the cache
            DatasourceConfig datasourceConfig = DatasourceConfigCache.INSTANCE.getConfig(id);

            if (datasourceConfig == null) {
                throw new RuntimeException("无此数据源");
            }

            dataSource.setJdbcUrl(datasourceConfig.buildJdbcUrl());
            dataSource.setUsername(datasourceConfig.getUsername());
            dataSource.setPassword(datasourceConfig.getPassword());
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        }
        Adds the created data source to the Data Source Manager and binds the current thread
        DatasourceHolder.INSTANCE.addDatasource(id, dataSource);
        return dataSource;
    }
}
