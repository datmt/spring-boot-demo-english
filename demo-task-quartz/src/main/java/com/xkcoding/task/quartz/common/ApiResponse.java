package com.xkcoding.task.quartz.common;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * <p>
 * Universal API package
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-26 13:59
 */
@Data
public class ApiResponse implements Serializable {
    /**
     * Return information
     */
    private String message;

    /**
     * Returns data
     */
    private Object data;

    public ApiResponse() {
    }

    private ApiResponse(String message, Object data) {
        this.message = message;
        this.data = data;
    }

    /**
     * Generic footprint gets the ApiResponse object
     *
     * @param message returns information
     * @param data returns data
     * @return ApiResponse
     */
    public static ApiResponse of(String message, Object data) {
        return new ApiResponse(message, data);
    }

    /**
     * Generic successful encapsulation gets ApiResponse object
     *
     * @param data returns data
     * @return ApiResponse
     */
    public static ApiResponse ok(Object data) {
        return new ApiResponse(HttpStatus.OK.getReasonPhrase(), data);
    }

    /**
     * Generic footprint gets the ApiResponse object
     *
     * @param message returns information
     * @return ApiResponse
     */
    public static ApiResponse msg(String message) {
        return of(message, null);
    }

}
