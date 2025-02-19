package com.xkcoding.oauth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Security configuration.
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2020-01-06 13:27
 */
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final ClientLogoutSuccessHandler clientLogoutSuccessHandler;
    private final ClientLoginFailureHandler clientLoginFailureHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin().loginPage("/oauth/login").failureHandler(clientLoginFailureHandler).loginProcessingUrl("/authorization/form").and().logout().logoutUrl("/oauth/logout").logoutSuccessHandler(clientLogoutSuccessHandler).and().authorizeRequests().antMatchers("/oauth/**").permitAll().anyRequest().authenticated();
    }

    /**
     * Authorization management.
     *
     * @return Authentication management objects
     * @throws Exception authentication exception information
     */
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
