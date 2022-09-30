package com.xkcoding.rbac.security.common;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * Generic API interface encapsulation
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-07 14:55
 */
@Data
public class ApiResponse implements Serializable {
    private static final long serialVersionUID = 8993485788201922830L;

    /**
     * Status code
     */
    private Integer code;

    /**
     * Back to content
     */
    private String message;

    /**
     * Returns data
     */
    private Object data;

    /**
     * No parameter constructor
     */
    private ApiResponse() {

    }

    /**
     * Full parameter constructor
     *
     * @param code status code
     * @param message returns content
     * @param data returns data
     */
    private ApiResponse(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * Construct a custom API to return
     *
     * @param code status code
     * @param message returns content
     * @param data returns data
     * @return ApiResponse
     */
    public static ApiResponse of(Integer code, String message, Object data) {
        return new ApiResponse(code, message, data);
    }

    /**
     * Construct a successful API return without data
     *
     * @return ApiResponse
     */
    public static ApiResponse ofSuccess() {
        return ofSuccess(null);
    }

    /**
     * Construct a successful API return with data
     *
     * @param data returns data
     * @return ApiResponse
     */
    public static ApiResponse ofSuccess(Object data) {
        return ofStatus(Status.SUCCESS, data);
    }

    /**
     * Construct an API return for a successful and custom message
     *
     * @param message returns content
     * @return ApiResponse
     */
    public static ApiResponse ofMessage(String message) {
        return of(Status.SUCCESS.getCode(), message, null);
    }

    /**
     * Construct a stateful API to return
     *
     * @param status status {@link Status}
     * @return ApiResponse
     */
    public static ApiResponse ofStatus(Status status) {
        return ofStatus(status, null);
    }

    /**
     * Construct a stateful API return with data
     *
     * @param status status {@link IStatus}
     * @param data returns data
     * @return ApiResponse
     */
    public static ApiResponse ofStatus(IStatus status, Object data) {
        return of(status.getCode(), status.getMessage(), data);
    }

    /**
     * Construct an exception API to return
     *
     * @param t exception
     * @param <T> a subclass of {@link BaseException}
     * @return ApiResponse
     */
    public static <T extends BaseException> ApiResponse ofException(T t) {
        return of(t.getCode(), t.getMessage(), t.getData());
    }
}
