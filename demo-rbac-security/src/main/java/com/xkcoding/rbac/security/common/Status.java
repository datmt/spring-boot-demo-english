package com.xkcoding.rbac.security.common;

import lombok.Getter;

/**
 * <p>
 * Universal status code
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-07 14:31
 */
@Getter
public enum Status implements IStatus {
    /**
     * Operation Successful!
     */
    SUCCESS(200, "操作成功！"),

    /**
     * Operation abnormality!
     */
    ERROR(500, "操作异常！"),

    /**
     * Exit successfully!
     */
    LOGOUT(200, "退出成功！"),

    /**
     * Please log in first!
     */
    UNAUTHORIZED(401, "请先登录！"),

    /**
     * No permission to access!
     */
    ACCESS_DENIED(403, "权限不足！"),

    /**
     * The request does not exist!
     */
    REQUEST_NOT_FOUND(404, "请求不存在！"),

    /**
     * Request method is not supported!
     */
    HTTP_BAD_METHOD(405, "请求方式不支持！"),

    /**
     * Request an exception!
     */
    BAD_REQUEST(400, "请求异常！"),

    /**
     * Parameter mismatch!
     */
    PARAM_NOT_MATCH(400, "参数不匹配！"),

    /**
     * Parameter cannot be empty!
     */
    PARAM_NOT_NULL(400, "参数不能为空！"),

    /**
     * The current user is locked, please contact the administrator to unlock!
     */
    USER_DISABLED(403, "当前用户已被锁定，请联系管理员解锁！"),

    /**
     * Wrong username or password!
     */
    USERNAME_PASSWORD_ERROR(5001, "用户名或密码错误！"),

    /**
     * The token has expired, please log back in!
     */
    TOKEN_EXPIRED(5002, "token 已过期，请重新登录！"),

    /**
     * Token resolution failed, please try to log in again!
     */
    TOKEN_PARSE_ERROR(5002, "token 解析失败，请尝试重新登录！"),

    /**
     * The current user is already logged in elsewhere, please try changing your password or logging back in!
     */
    TOKEN_OUT_OF_CTRL(5003, "当前用户已在别处登录，请尝试更改密码或重新登录！"),

    /**
     * Can't kick yourself out manually, please try logging out!
     */
    KICKOUT_SELF(5004, "无法手动踢出自己，请尝试退出登录操作！");

    /**
     * Status code
     */
    private Integer code;

    /**
     * Return information
     */
    private String message;

    Status(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static Status fromCode(Integer code) {
        Status[] statuses = Status.values();
        for (Status status : statuses) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return SUCCESS;
    }

    @Override
    public String toString() {
        return String.format(" Status:{code=%s, message=%s} ", getCode(), getMessage());
    }

}
