package com.xkcoding.dynamic.datasource.datasource;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * <p>
 * Data source management class
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-09-04 14:27
 */
public class DatasourceManager {
    /**
     * Default release time
     */
    private static final Long DEFAULT_RELEASE = 10L;

    /**
     * Data source
     */
    @Getter
    private HikariDataSource dataSource;

    /**
     * Last use time
     */
    private LocalDateTime lastUseTime;

    public DatasourceManager(HikariDataSource dataSource) {
        this.dataSource = dataSource;
        this.lastUseTime = LocalDateTime.now();
    }

    /**
     * Whether it has expired, and if so, close the data source
     *
     * @return whether expired, {@code true} expired, {@code false} did not expire
     */
    public boolean isExpired() {
        if (LocalDateTime.now().isBefore(this.lastUseTime.plusMinutes(DEFAULT_RELEASE))) {
            return false;
        }
        this.dataSource.close();
        return true;
    }

    /**
     * Refresh last use time
     */
    public void refreshTime() {
        this.lastUseTime = LocalDateTime.now();
    }
}
