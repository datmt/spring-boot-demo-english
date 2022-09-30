package com.xkcoding.zookeeper.annotation;

import java.lang.annotation.*;

/**
 * <p>
 * Distributed lock dynamic key annotation, after the configuration of the key value will dynamically get the parameter content
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-27 14:17
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface LockKeyParam {
    /**
     * If the dynamic key is in the user object, then it is necessary to set the value of fields to the user property name in the object can be more than one, and the base type does not need to set the value
     * <p>Example 1: public void count(@LockKeyParam({"id"}) User user)
     * <p>Example 2: public void count(@LockKeyParam({"id","userName"}) User user)
     * <p>Example 3: public void count (@LockKeyParam String userId)
     */
    String[] fields() default {};
}
