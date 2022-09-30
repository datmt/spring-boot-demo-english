package com.xkcoding.codegen.common;

import lombok.Getter;

/**
 * <p>
 * Generic state enumeration
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-03-22 10:13
 */
@Getter
public enum ResultCode implements IResultCode {
    /**
     * Success
     */
    OK(200, "成功"),
    /**
     * Failed
     */
    ERROR(500, "失败");

    /**
     * Return code
     */
    private Integer code;

    /**
     * Returns the message
     */
    private String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
