package com.xkcoding.dynamic.datasource.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Spring tool class
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-09-04 16:16
 */
@Slf4j
@Service
@Lazy(false)
public class SpringUtil implements ApplicationContextAware, DisposableBean {

    private static ApplicationContext applicationContext = null;

    /**
     * Get the ApplicationContext stored in a static variable.
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * Implement the ApplicationContextAware interface, inject Context into static variables.
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringUtil.applicationContext = applicationContext;
    }

    /**
     * Get a bean from the static variable applicationContext, and automatically transition to the type of the assigned object.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T) applicationContext.getBean(name);
    }

    /**
     * Get a bean from the static variable applicationContext, and automatically transition to the type of the assigned object.
     */
    public static <T> T getBean(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }

    /**
     * Clear SpringContextHolder from ApplicationContext to null.
     */
    public static void clearHolder() {
        if (log.isDebugEnabled()) {
            log.debug("清除SpringContextHolder中的ApplicationContext:" + applicationContext);
        }
        applicationContext = null;
    }

    /**
     * Post events
     *
     * @param event event
     */
    public static void publishEvent(ApplicationEvent event) {
        if (applicationContext == null) {
            return;
        }
        applicationContext.publishEvent(event);
    }

    /**
     * Implement the DisposableBean interface to clean up static variables when the Context is closed.
     */
    @Override
    public void destroy() {
        SpringUtil.clearHolder();
    }

}
