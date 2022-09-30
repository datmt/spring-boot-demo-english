package com.xkcoding.task.xxl.job.config.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 * xxl-job configuration
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-08-07 10:25
 */
@Data
@ConfigurationProperties(prefix = "xxl.job")
public class XxlJobProps {
    /**
     * Dispatch center configuration
     */
    private XxlJobAdminProps admin;

    /**
     * Actuator configuration
     */
    private XxlJobExecutorProps executor;

    /**
     * Access token for interacting with the dispatch center
     */
    private String accessToken;

    @Data
    public static class XxlJobAdminProps {
        /**
         * Dispatch center address
         */
        private String address;
    }

    @Data
    public static class XxlJobExecutorProps {
        /**
         * Actuator name
         */
        private String appName;

        /**
         * Actuator IP
         */
        private String ip;

        /**
         * Actuator port
         */
        private int port;

        /**
         * Actuator logs
         */
        private String logPath;

        /**
         * Number of days executor log retention, -1
         */
        private int logRetentionDays;
    }
}
