package com.xkcoding.oauth.controller;

import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * Customize the confirmation authorization page.
 * Note that setComplete cannot be done in code, as the entire authorization process is not over
 * We only revised some of the information it confirmed in the middle of the way.
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2020-01-06 16:42
 */
@Controller
@SessionAttributes("authorizationRequest")
public class AuthorizationController {

    /**
     * Customize the authorization confirmation page
     * Of course you can also use the {@link AuthorizationEndpoint#setUserApprovalPage (String)} method
     * Set up, but model is less flexible
     *
     * @param model model
     * @return ModelAndView
     */
    @GetMapping("/oauth/confirm_access")
    public ModelAndView getAccessConfirmation(Map<String, Object> model) {
        AuthorizationRequest authorizationRequest = (AuthorizationRequest) model.get("authorizationRequest");
        ModelAndView view = new ModelAndView();
        view.setViewName("authorization");
        view.addObject("clientId", authorizationRequest.getClientId());
        Pass scope to the past, Set collection
        view.addObject("scopes", authorizationRequest.getScope());
        return view;
    }

}
