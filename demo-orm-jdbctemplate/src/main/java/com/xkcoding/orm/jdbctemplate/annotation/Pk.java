package com.xkcoding.orm.jdbctemplate.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Primary key annotations
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-10-15 11:23
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Pk {
    /**
     * Self-incrementing
     *
     * @return Self-augmented primary key
     */
    boolean auto() default true;
}
