package com.xkcoding.oauth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.Objects;

/**
 * Page controller.
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2020-01-06 16:30
 */
@Controller
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class Oauth2Controller {

    /**
     * Authorization code mode jumps to the login page
     *
     * @return view
     */
    @GetMapping("/login")
    public String loginView() {
        return "login";
    }

    /**
     * Sign out
     *
     * @param the callback address after the redirectUrl exit is complete
     * @param principal user information
     * @return Results
     */
    @GetMapping("/logout")
    public ModelAndView logoutView(@RequestParam("redirect_url") String redirectUrl, Principal principal) {
        if (Objects.isNull(principal)) {
            throw new ResourceAccessException("请求错误，用户尚未登录");
        }
        ModelAndView view = new ModelAndView();
        view.setViewName("logout");
        view.addObject("user", principal.getName());
        view.addObject("redirectUrl", redirectUrl);
        return view;
    }

}
