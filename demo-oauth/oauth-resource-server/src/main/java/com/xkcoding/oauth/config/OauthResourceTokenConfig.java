package com.xkcoding.oauth.config;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Base64;

/**
 * token related configuration, jwt related.
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2020-01-09  14:39
 */
@Slf4j
@Configuration
@AllArgsConstructor
public class OauthResourceTokenConfig {

    private final ResourceServerProperties resourceServerProperties;

    /**
     * This is not a store of tokens, he converts the access token with authentication
     * This method can be used anywhere {@link TokenStore} is required
     *
     * @return TokenStore
     */
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    /**
     * jwt token conversion
     *
     * @return jwt
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setVerifierKey(getPubKey());
        return converter;
    }

    /**
     * Asymmetric key encryption, get the public key.
     * Automatic selection of loading method.
     *
     * @return public key
     */
    private String getPubKey() {
        If the key is not available locally, it is obtained from the authorization server
        return StringUtils.isEmpty(resourceServerProperties.getJwt().getKeyValue()) ? getKeyFromAuthorizationServer() : resourceServerProperties.getJwt().getKeyValue();
    }

    /**
     * When there is no public key locally, get it from the server
     * Basic certification required
     *
     * @return public key
     */
    private String getKeyFromAuthorizationServer() {
        ObjectMapper objectMapper = new ObjectMapper();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, encodeClient());
        HttpEntity<String> requestEntity = new HttpEntity<>(null, httpHeaders);
        String pubKey = new RestTemplate().getForObject(resourceServerProperties.getJwt().getKeyUri(), String.class, requestEntity);
        try {
            JSONObject body = objectMapper.readValue(pubKey, JSONObject.class);
            log.info("Get Key From Authorization Server.");
            return body.getStr("value");
        } catch (IOException e) {
            log.error("Get public key error: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Client information
     *
     * @return basic
     */
    private String encodeClient() {
        return "Basic " + Base64.getEncoder().encodeToString((resourceServerProperties.getClientId() + ":" + resourceServerProperties.getClientSecret()).getBytes());
    }
}
