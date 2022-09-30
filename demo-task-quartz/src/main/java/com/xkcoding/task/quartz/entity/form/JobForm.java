package com.xkcoding.task.quartz.entity.form;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * Scheduled task details
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-26 13:42
 */
@Data
@Accessors(chain = true)
public class JobForm {
    /**
     * Full class name of the scheduled task
     */
    @NotBlank(message = "类名不能为空")
    private String jobClassName;
    /**
     * Task group name
     */
    @NotBlank(message = "任务组名不能为空")
    private String jobGroupName;
    /**
     * Scheduled task cron expression
     */
    @NotBlank(message = "cron表达式不能为空")
    private String cronExpression;
}
