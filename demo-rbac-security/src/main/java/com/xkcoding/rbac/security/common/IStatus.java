package com.xkcoding.rbac.security.common;

/**
 * <p>
 * REST API error code interface
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-07 14:35
 */
public interface IStatus {

    /**
     * Status code
     *
     * @return status code
     */
    Integer getCode();

    /**
     * Return information
     *
     * @return Return information
     */
    String getMessage();

}
