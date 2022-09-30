package com.xkcoding.zookeeper.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Zookeeper-based distributed lock annotations
 * After you make this note on the method that needs to be locked, AOP will help you manage the lock of that method uniformly
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-27 14:11
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ZooLock {
    /**
     * Distributed lock key
     */
    String key();

    /**
     * Lock release time, default 5 seconds
     */
    long timeout() default 5 * 1000;

    /**
     * Time format, default: milliseconds
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
