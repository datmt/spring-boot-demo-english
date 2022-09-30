/**
 * Spring security oauth2 related configuration.
 * Automatic configuration using spring boot oauth2.
 * {@link com.xkcoding.oauth.config.Oauth2AuthorizationServerConfig}
 * Authorization server-related configuration, which mainly sets how the authorization server reads client, user information, and some endpoint configurations
 * More things can be configured here, such as endpoint mapping, token enhancements, etc
 * <p>
 * {@link com.xkcoding.oauth.config.Oauth2AuthorizationTokenConfig}
 * Authorization server token related configuration, mainly set jwt, encryption method and other information
 * <p>
 * {@link com.xkcoding.oauth.config.ClientLogoutSuccessHandler}
 * Resource server exit processing later. In the authorization code mode, all clients need to jump to the authorization server to log in
 * When the login is successful, jump to the callback address, if the user needs to log out, also jump to the authorization server to log out
 * But spring security oauth2 doesn't seem to have this logic.
 * Therefore, I added a redirect_url parameter to the logout endpoint, indicating the address to be redirected after the logout is successful
 * This processor is designed to complete the jump operation after a successful login.
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2020-01-07 9:16
 */
package com.xkcoding.oauth.config;
