package com.xkcoding.ratelimit.guava.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Flow restriction annotation, added {@link AliasFor} must be obtained via {@link AnnotationUtils} to take effect
 *
 * @author yangkai.shen
 * @date Created in 2019-09-12 14:14
 * @see AnnotationUtils
 * </p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {
    int NOT_LIMITED = 0;

    /**
     * qps
     */
    @AliasFor("qps") double value() default NOT_LIMITED;

    /**
     * qps
     */
    @AliasFor("value") double qps() default NOT_LIMITED;

    /**
     * Timeout duration
     */
    int timeout() default 0;

    /**
     * Timeout unit
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
