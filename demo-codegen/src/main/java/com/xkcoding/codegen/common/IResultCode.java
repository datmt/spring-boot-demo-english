package com.xkcoding.codegen.common;

/**
 * <p>
 * Unified status code interface
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-03-21 16:28
 */
public interface IResultCode {
    /**
     * Get the status code
     *
     * @return status code
     */
    Integer getCode();

    /**
     * Get the return message
     *
     * @return Returns a message
     */
    String getMessage();
}
