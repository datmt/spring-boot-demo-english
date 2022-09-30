package com.xkcoding.exception.handler.constant;

import lombok.Getter;

/**
 * <p>
 * Status code encapsulation
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-10-02 21:02
 */
@Getter
public enum Status {
    /**
     * Operation successful
     */
    OK(200, "操作成功"),

    /**
     * Unknown exception
     */
    UNKNOWN_ERROR(500, "服务器出错啦");
    /**
     * Status code
     */
    private Integer code;
    /**
     * Content
     */
    private String message;

    Status(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
