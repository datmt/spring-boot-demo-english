package com.xkcoding.codegen.common;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * Unified API object returns
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-03-22 10:13
 */
@Data
@NoArgsConstructor
public class R<T> {
    /**
     * Status code
     */
    private Integer code;

    /**
     * Returns the message
     */
    private String message;

    /**
     * Status
     */
    private boolean status;

    /**
     * Returns data
     */
    private T data;

    public R(Integer code, String message, boolean status, T data) {
        this.code = code;
        this.message = message;
        this.status = status;
        this.data = data;
    }

    public R(IResultCode resultCode, boolean status, T data) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.status = status;
        this.data = data;
    }

    public R(IResultCode resultCode, boolean status) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.status = status;
        this.data = null;
    }

    public static <T> R success() {
        return new R<>(ResultCode.OK, true);
    }

    public static <T> R message(String message) {
        return new R<>(ResultCode.OK.getCode(), message, true, null);
    }

    public static <T> R success(T data) {
        return new R<>(ResultCode.OK, true, data);
    }

    public static <T> R fail() {
        return new R<>(ResultCode.ERROR, false);
    }

    public static <T> R fail(IResultCode resultCode) {
        return new R<>(resultCode, false);
    }

    public static <T> R fail(Integer code, String message) {
        return new R<>(code, message, false, null);
    }

    public static <T> R fail(IResultCode resultCode, T data) {
        return new R<>(resultCode, false, data);
    }

    public static <T> R fail(Integer code, String message, T data) {
        return new R<>(code, message, false, data);
    }

}
