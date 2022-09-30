package com.xkcoding.dynamic.datasource.datasource;

import com.xkcoding.dynamic.datasource.model.DatasourceConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * Data source configuration cache
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-09-04 17:13
 */
public enum DatasourceConfigCache {
    /**
     * Current instance
     */
    INSTANCE;

    /**
     * Manage dynamic data source lists.
     */
    private static final Map<Long, DatasourceConfig> CONFIG_CACHE = new ConcurrentHashMap<>();

    /**
     * Add data source configuration
     *
     * @param id data source configuration ID
     * @param config data source configuration
     */
    public synchronized void addConfig(Long id, DatasourceConfig config) {
        CONFIG_CACHE.put(id, config);
    }

    /**
     * Query data source configuration
     *
     * @param id data source configuration ID
     * @return data source configuration
     */
    public synchronized DatasourceConfig getConfig(Long id) {
        if (CONFIG_CACHE.containsKey(id)) {
            return CONFIG_CACHE.get(id);
        }
        return null;
    }

    /**
     * Clear the data source configuration
     */
    public synchronized void removeConfig(Long id) {
        CONFIG_CACHE.remove(id);
        Synchronously clears the data source corresponding to the DatasourceHolder
        DatasourceHolder.INSTANCE.removeDatasource(id);
    }
}
