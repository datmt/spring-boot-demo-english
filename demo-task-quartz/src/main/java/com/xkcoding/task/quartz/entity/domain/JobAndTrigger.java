package com.xkcoding.task.quartz.entity.domain;

import lombok.Data;

import java.math.BigInteger;

/**
 * <p>
 * Entity class
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-26 15:05
 */
@Data
public class JobAndTrigger {
    /**
     * Scheduled task name
     */
    private String jobName;
    /**
     * Timed task groups
     */
    private String jobGroup;
    /**
     * Full class name of the scheduled task
     */
    private String jobClassName;
    /**
     * Trigger name
     */
    private String triggerName;
    /**
     * Trigger group
     */
    private String triggerGroup;
    /**
     * Repeat interval
     */
    private BigInteger repeatInterval;
    /**
     * Number of triggers
     */
    private BigInteger timesTriggered;
    /**
     * cron expression
     */
    private String cronExpression;
    /**
     * Time zone
     */
    private String timeZoneId;
    /**
     * Timed task status
     */
    private String triggerState;
}
