package com.xkcoding.task.quartz.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xkcoding.task.quartz.entity.domain.JobAndTrigger;
import com.xkcoding.task.quartz.entity.form.JobForm;
import com.xkcoding.task.quartz.mapper.JobMapper;
import com.xkcoding.task.quartz.service.JobService;
import com.xkcoding.task.quartz.util.JobUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * Job Service
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-26 13:25
 */
@Service
@Slf4j
public class JobServiceImpl implements JobService {
    private final Scheduler scheduler;
    private final JobMapper jobMapper;

    @Autowired
    public JobServiceImpl(Scheduler scheduler, JobMapper jobMapper) {
        this.scheduler = scheduler;
        this.jobMapper = jobMapper;
    }

    /**
     * Add and start scheduled tasks
     *
     * @param form form parameter {@link JobForm}
     * @return {@link JobDetail}
     * @throws Exception exception
     */
    @Override
    public void addJob(JobForm form) throws Exception {
        Start the scheduler
        scheduler.start();

        Build Job information
        JobDetail jobDetail = JobBuilder.newJob(JobUtil.getClass(form.getJobClassName()).getClass()).withIdentity(form.getJobClassName(), form.getJobGroupName()).build();

        Cron Expression Dispatcher Builder (i.e. when the task executes)
        CronScheduleBuilder cron = CronScheduleBuilder.cronSchedule(form.getCronExpression());

        Build a Trigger from a Cron expression
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(form.getJobClassName(), form.getJobGroupName()).withSchedule(cron).build();

        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            log.error("【定时任务】创建失败！", e);
            throw new Exception("【定时任务】创建失败！");
        }

    }

    /**
     * Delete scheduled tasks
     *
     * @param form form parameter {@link JobForm}
     * @throws SchedulerException exception
     */
    @Override
    public void deleteJob(JobForm form) throws SchedulerException {
        scheduler.pauseTrigger(TriggerKey.triggerKey(form.getJobClassName(), form.getJobGroupName()));
        scheduler.unscheduleJob(TriggerKey.triggerKey(form.getJobClassName(), form.getJobGroupName()));
        scheduler.deleteJob(JobKey.jobKey(form.getJobClassName(), form.getJobGroupName()));
    }

    /**
     * Pause scheduled tasks
     *
     * @param form form parameter {@link JobForm}
     * @throws SchedulerException exception
     */
    @Override
    public void pauseJob(JobForm form) throws SchedulerException {
        scheduler.pauseJob(JobKey.jobKey(form.getJobClassName(), form.getJobGroupName()));
    }

    /**
     * Resume scheduled tasks
     *
     * @param form form parameter {@link JobForm}
     * @throws SchedulerException exception
     */
    @Override
    public void resumeJob(JobForm form) throws SchedulerException {
        scheduler.resumeJob(JobKey.jobKey(form.getJobClassName(), form.getJobGroupName()));
    }

    /**
     * Reconfigure scheduled tasks
     *
     * @param form form parameter {@link JobForm}
     * @throws Exception exception
     */
    @Override
    public void cronJob(JobForm form) throws Exception {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(form.getJobClassName(), form.getJobGroupName());
            Expression dispatch builder
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(form.getCronExpression());

            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

            Build a Trigger from a Cron expression
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();

            Reset the job execution with the new trigger
            scheduler.rescheduleJob(triggerKey, trigger);
        } catch (SchedulerException e) {
            log.error("【定时任务】更新失败！", e);
            throw new Exception("【定时任务】创建失败！");
        }
    }

    /**
     * Query the list of scheduled tasks
     *
     * @param currentPage current page
     * @param pageSize per page
     * @return Scheduled task list
     */
    @Override
    public PageInfo<JobAndTrigger> list(Integer currentPage, Integer pageSize) {
        PageHelper.startPage(currentPage, pageSize);
        List<JobAndTrigger> list = jobMapper.list();
        return new PageInfo<>(list);
    }
}
