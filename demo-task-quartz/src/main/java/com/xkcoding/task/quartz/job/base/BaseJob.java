package com.xkcoding.task.quartz.job.base;

import org.quartz.*;

/**
 * <p>
 * Job base class, mainly encapsulated on {@link org.quartz.Job} another layer, only let us implement the Job in our own project
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-26 13:27
 */
public interface BaseJob extends Job {
    /**
     * <p>
     * Called by the <code>{@link Scheduler}</code> when a <code>{@link Trigger}</code>
     * fires that is associated with the <code>Job</code>.
     * </p>
     *
     * <p>
     * The implementation may wish to set a
     * {@link JobExecutionContext#setResult(Object) result} object on the
     * {@link JobExecutionContext} before this method exits.  The result itself
     * is meaningless to Quartz, but may be informative to
     * <code>{@link JobListener}s</code> or
     * <code>{@link TriggerListener}s</code> that are watching the job's
     * execution.
     * </p>
     *
     * @param context context
     * @throws JobExecutionException if there is an exception while executing the job.
     */
    @Override
    void execute(JobExecutionContext context) throws JobExecutionException;
}
