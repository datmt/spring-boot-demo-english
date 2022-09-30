package com.xkcoding.task.quartz.util;

import com.xkcoding.task.quartz.job.base.BaseJob;

/**
 * <p>
 * Timed task reflection tool class
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-26 13:33
 */
public class JobUtil {
    /**
     * Get the Job instance based on the full class name
     *
     * @param classname Job full class name
     * @return {@link BaseJob} instance
     * @throws Exception generics get exceptions
     */
    public static BaseJob getClass(String classname) throws Exception {
        Class<?> clazz = Class.forName(classname);
        return (BaseJob) clazz.newInstance();
    }
}
