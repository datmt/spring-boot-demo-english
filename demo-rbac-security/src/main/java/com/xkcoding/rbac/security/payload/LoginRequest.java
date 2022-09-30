package com.xkcoding.rbac.security.payload;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * Login request parameters
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-10 15:52
 */
@Data
public class LoginRequest {

    /**
     * Username or email or mobile phone number
     */
    @NotBlank(message = "用户名不能为空")
    private String usernameOrEmailOrPhone;

    /**
     * Password
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * Remember me
     */
    private Boolean rememberMe = false;

}
