package com.xkcoding.oauth.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Customer group exits the processor successfully logged in.
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2020-01-06 22:11
 */
@Slf4j
@Component
public class ClientLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setStatus(HttpStatus.FOUND.value());
        Jump to the client's callback address
        response.sendRedirect(request.getParameter("redirectUrl"));
    }

}
