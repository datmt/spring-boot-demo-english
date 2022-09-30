package com.xkcoding.rbac.security.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * JWT response returns
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-10 16:01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    /**
     * token field
     */
    private String token;
    /**
     * Token type
     */
    private String tokenType = "Bearer";

    public JwtResponse(String token) {
        this.token = token;
    }
}
