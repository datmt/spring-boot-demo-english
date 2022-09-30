package com.xkcoding.task.quartz.service;

import com.github.pagehelper.PageInfo;
import com.xkcoding.task.quartz.entity.domain.JobAndTrigger;
import com.xkcoding.task.quartz.entity.form.JobForm;
import org.quartz.SchedulerException;

/**
 * <p>
 * Job Service
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-26 13:24
 */
public interface JobService {
    /**
     * Add and start scheduled tasks
     *
     * @param form form parameter {@link JobForm}
     * @throws Exception exception
     */
    void addJob(JobForm form) throws Exception;

    /**
     * Delete scheduled tasks
     *
     * @param form form parameter {@link JobForm}
     * @throws SchedulerException exception
     */
    void deleteJob(JobForm form) throws SchedulerException;

    /**
     * Pause scheduled tasks
     *
     * @param form form parameter {@link JobForm}
     * @throws SchedulerException exception
     */
    void pauseJob(JobForm form) throws SchedulerException;

    /**
     * Resume scheduled tasks
     *
     * @param form form parameter {@link JobForm}
     * @throws SchedulerException exception
     */
    void resumeJob(JobForm form) throws SchedulerException;

    /**
     * Reconfigure scheduled tasks
     *
     * @param form form parameter {@link JobForm}
     * @throws Exception exception
     */
    void cronJob(JobForm form) throws Exception;

    /**
     * Query the list of scheduled tasks
     *
     * @param currentPage current page
     * @param pageSize per page
     * @return Scheduled task list
     */
    PageInfo<JobAndTrigger> list(Integer currentPage, Integer pageSize);
}
