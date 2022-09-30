package com.xkcoding.task.quartz.mapper;

import com.xkcoding.task.quartz.entity.domain.JobAndTrigger;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * Job Mapper
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-26 15:12
 */
@Component
public interface JobMapper {
    /**
     * Query the list of scheduled jobs and triggers
     *
     * @return list of scheduled jobs and triggers
     */
    List<JobAndTrigger> list();
}
