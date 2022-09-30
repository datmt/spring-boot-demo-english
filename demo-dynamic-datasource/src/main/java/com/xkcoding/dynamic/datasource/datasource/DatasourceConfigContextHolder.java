package com.xkcoding.dynamic.datasource.datasource;

/**
 * <p>
 * Data source identity management
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-09-04 14:16
 */
public class DatasourceConfigContextHolder {
    private static final ThreadLocal<Long> DATASOURCE_HOLDER = ThreadLocal.withInitial(() -> DatasourceHolder.DEFAULT_ID);

    /**
     * Set the default data source
     */
    public static void setDefaultDatasource() {
        DATASOURCE_HOLDER.remove();
        setCurrentDatasourceConfig(DatasourceHolder.DEFAULT_ID);
    }

    /**
     * Get the current data source configuration ID
     *
     * @return Data source configuration ID
     */
    public static Long getCurrentDatasourceConfig() {
        return DATASOURCE_HOLDER.get();
    }

    /**
     * Set the current data source configuration ID
     *
     * @param id data source configuration ID
     */
    public static void setCurrentDatasourceConfig(Long id) {
        DATASOURCE_HOLDER.set(id);
    }

}
