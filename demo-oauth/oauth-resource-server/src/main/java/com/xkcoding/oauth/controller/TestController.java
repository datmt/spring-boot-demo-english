package com.xkcoding.oauth.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Test interface.
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2020-01-09  14:37
 */
@RestController
public class TestController {

    /**
     * Resources that only users with ROLE_ADMIN can access
     *
     * @return ADMIN
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String admin() {
        return "ADMIN";
    }

    /**
     * Resources that only users with ROLE_TEST can access
     *
     * @return TEST
     */
    @PreAuthorize("hasRole('TEST')")
    @GetMapping("/test")
    public String test() {
        return "TEST";
    }

    /**
     * scope User resources with READ are available to access
     *
     * @return READ
     */
    @PreAuthorize("#oauth2.hasScope('READ')")
    @GetMapping("/read")
    public String read() {
        return "READ";
    }

    /**
     * scope User resources with WRITE can only be accessed
     *
     * @return WRITE
     */
    @PreAuthorize("#oauth2.hasScope('WRITE')")
    @GetMapping("/write")
    public String write() {
        return "WRITE";
    }

}
