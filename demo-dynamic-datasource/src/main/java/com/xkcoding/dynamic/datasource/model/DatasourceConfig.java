package com.xkcoding.dynamic.datasource.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * <p>
 * Data source configuration table
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-09-04 10:58
 */
@Data
@Table(name = "datasource_config")
public class DatasourceConfig implements Serializable {
    /**
     * Primary key
     */
    @Id
    @Column(name = "`id`")
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * Database address
     */
    @Column(name = "`host`")
    private String host;

    /**
     * Database port
     */
    @Column(name = "`port`")
    private Integer port;

    /**
     * Database user name
     */
    @Column(name = "`username`")
    private String username;

    /**
     * Database password
     */
    @Column(name = "`password`")
    private String password;

    /**
     * Database name
     */
    @Column(name = "`database`")
    private String database;

    /**
     * Construct the JDBC URL
     *
     * @return JDBC URL
     */
    public String buildJdbcUrl() {
        return String.format("jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=utf-8&useSSL=false", this.host, this.port, this.database);
    }

}
