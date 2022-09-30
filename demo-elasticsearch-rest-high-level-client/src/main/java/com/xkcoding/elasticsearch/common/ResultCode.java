package com.xkcoding.elasticsearch.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ResultCode
 *
 * @author fxbin
 * @version v1.0
 * @since 2019-08-26 1:47
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    /**
     * The interface call was successful
     */
    SUCCESS(0, "Request Successful"),

    /**
     * The server is temporarily unavailable, we recommend that you try again later. It is recommended to retry no more than 3 times.
     */
    FAILURE(-1, "System Busy");

    final int code;

    final String msg;

}
