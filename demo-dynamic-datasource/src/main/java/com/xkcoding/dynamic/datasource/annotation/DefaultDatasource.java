package com.xkcoding.dynamic.datasource.annotation;

import java.lang.annotation.*;

/**
 * <p>
 * The user ID can only use the default data source
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-09-04 17:37
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DefaultDatasource {
}
