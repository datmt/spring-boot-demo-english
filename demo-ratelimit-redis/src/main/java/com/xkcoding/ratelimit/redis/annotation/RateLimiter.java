package com.xkcoding.ratelimit.redis.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Flow restriction annotation, added {@link AliasFor} must be obtained via {@link AnnotationUtils} to take effect
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-09-30 10:31
 * @see AnnotationUtils
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {
    long DEFAULT_REQUEST = 10;

    /**
     * max Maximum number of requests
     */
    @AliasFor("max") long value() default DEFAULT_REQUEST;

    /**
     * max Maximum number of requests
     */
    @AliasFor("value") long max() default DEFAULT_REQUEST;

    /**
     * Flow restriction key
     */
    String key() default "";

    /**
     * Timeout timeout, default 1 minute
     */
    long timeout() default 1;

    /**
     * Timeout unit, default minutes
     */
    TimeUnit timeUnit() default TimeUnit.MINUTES;
}
