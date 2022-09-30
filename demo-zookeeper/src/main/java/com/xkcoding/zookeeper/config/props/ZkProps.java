package com.xkcoding.zookeeper.config.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 * Zookeeper configuration items
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-27 14:47
 */
@Data
@ConfigurationProperties(prefix = "zk")
public class ZkProps {
    /**
     * Connection address
     */
    private String url;

    /**
     * Timeout (milliseconds), default 1000
     */
    private int timeout = 1000;

    /**
     * Number of retries, default 3
     */
    private int retry = 3;
}
