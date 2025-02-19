package com.xkcoding.oauth.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * Login failure processor, after failure with the failure information redirected to the login address to log in again.
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2020-01-07 13:01
 */
@Slf4j
@Component
public class ClientLoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        log.debug("Login failed!");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.sendRedirect("/oauth/login?error=" + URLEncoder.encode(exception.getLocalizedMessage(), "UTF-8"));
    }
}
