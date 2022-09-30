package com.xkcoding.task.job;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * <p>
 * Scheduled tasks
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-22 19:09
 */
@Component
@Slf4j
public class TaskJob {

    /**
     * Executes every 10s in standard time
     */
    @Scheduled(cron = "0/10 * * * * ?")
    public void job1() {
        log.info("【job1】开始执行：{}", DateUtil.formatDateTime(new Date()));
    }

    /**
     * Performed at intervals of 2s from the start time
     * Fixed intervals
     */
    @Scheduled(fixedRate = 2000)
    public void job2() {
        log.info("【job2】开始执行：{}", DateUtil.formatDateTime(new Date()));
    }

    /**
     * Starts at startup time and is performed at 4s intervals after a delay of 5s
     * Fixed waiting time
     */
    @Scheduled(fixedDelay = 4000, initialDelay = 5000)
    public void job3() {
        log.info("【job3】开始执行：{}", DateUtil.formatDateTime(new Date()));
    }
}
