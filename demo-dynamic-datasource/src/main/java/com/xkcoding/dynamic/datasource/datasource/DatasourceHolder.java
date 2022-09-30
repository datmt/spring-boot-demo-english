package com.xkcoding.dynamic.datasource.datasource;

import com.zaxxer.hikari.HikariDataSource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * Data source management
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-09-04 14:23
 */
public enum DatasourceHolder {
    /**
     * Current instance
     */
    INSTANCE;

    /**
     * Start execution, timed 5 minutes to clean up
     */
    DatasourceHolder() {
        DatasourceScheduler.INSTANCE.schedule(this::clearExpiredDatasource, 5 * 60 * 1000);
    }

    /**
     * The id of the default data source
     */
    public static final Long DEFAULT_ID = -1L;

    /**
     * Manage dynamic data source lists.
     */
    private static final Map<Long, DatasourceManager> DATASOURCE_CACHE = new ConcurrentHashMap<>();

    /**
     * Add dynamic data source
     *
     * @param id data source id
     * @param dataSource data source
     */
    public synchronized void addDatasource(Long id, HikariDataSource dataSource) {
        DatasourceManager datasourceManager = new DatasourceManager(dataSource);
        DATASOURCE_CACHE.put(id, datasourceManager);
    }

    /**
     * Query dynamic data sources
     *
     * @param id data source id
     * @return Data source
     */
    public synchronized HikariDataSource getDatasource(Long id) {
        if (DATASOURCE_CACHE.containsKey(id)) {
            DatasourceManager datasourceManager = DATASOURCE_CACHE.get(id);
            datasourceManager.refreshTime();
            return datasourceManager.getDataSource();
        }
        return null;
    }

    /**
     * Clear the timed out data source
     */
    public synchronized void clearExpiredDatasource() {
        DATASOURCE_CACHE.forEach((k, v) -> {
            Excludes the default data source
            if (!DEFAULT_ID.equals(k)) {
                if (v.isExpired()) {
                    DATASOURCE_CACHE.remove(k);
                }
            }
        });
    }

    /**
     * Clear dynamic data sources
     *
     * @param id data source id
     */
    public synchronized void removeDatasource(Long id) {
        if (DATASOURCE_CACHE.containsKey(id)) {
            Close the data source
            DATASOURCE_CACHE.get(id).getDataSource().close();
            Remove the cache
            DATASOURCE_CACHE.remove(id);
        }
    }
}
