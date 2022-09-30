package com.xkcoding.elasticsearch.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * ElasticsearchProperties
 *
 * @author fxbin
 * @version v1.0
 * @since 2019-09-15 22:58
 */
@Data
@Builder
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "demo.data.elasticsearch")
public class ElasticsearchProperties {

    /**
     * Request agreement
     */
    private String schema = "http";

    /**
     * Cluster name
     */
    private String clusterName = "elasticsearch";

    /**
     * Cluster nodes
     */
    @NotNull(message = "集群节点不允许为空")
    private List<String> clusterNodes = new ArrayList<>();

    /**
     * Connection timeout (ms)
     */
    private Integer connectTimeout = 1000;

    /**
     * socket timeout period
     */
    private Integer socketTimeout = 30000;

    /**
     * Connection request timeout period
     */
    private Integer connectionRequestTimeout = 500;

    /**
     * Maximum number of connections per route
     */
    private Integer maxConnectPerRoute = 10;

    /**
     * Maximum total number of connections
     */
    private Integer maxConnectTotal = 30;

    /**
     * Index configuration information
     */
    private Index index = new Index();

    /**
     * Verify the account
     */
    private Account account = new Account();

    /**
     * Index configuration information
     */
    @Data
    public static class Index {

        /**
         * Number of shards
         */
        private Integer numberOfShards = 3;

        /**
         * Number of copies
         */
        private Integer numberOfReplicas = 2;

    }

    /**
     * Verify the account
     */
    @Data
    public static class Account {

        /**
         * Authenticate users
         */
        private String username;

        /**
         * Authentication password
         */
        private String password;

    }

}
