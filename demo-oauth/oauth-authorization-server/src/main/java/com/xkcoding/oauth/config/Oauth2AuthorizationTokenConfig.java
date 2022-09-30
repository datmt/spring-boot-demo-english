package com.xkcoding.oauth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;

/**
 * Token related configuration.
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2020-01-06 13:33
 */
@Configuration
@RequiredArgsConstructor
public class Oauth2AuthorizationTokenConfig {

    /**
     * Declare the memory TokenStore implementation, which is used to store tokens related.
     * The default implementation is mysql, redis
     *
     * @return InMemoryTokenStore
     */
    @Bean
    @Primary
    public TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }

    /**
     * jwt token configured, asymmetric encryption
     *
     * @return converter
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        final JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
        accessTokenConverter.setKeyPair(keyPair());
        return accessTokenConverter;
    }

    /**
     * Key keyPair.
     * Can be used to generate jwt / jwk.
     *
     * @return keyPair
     */
    @Bean
    public KeyPair keyPair() {
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("oauth2.jks"), "123456".toCharArray());
        return keyStoreKeyFactory.getKeyPair("oauth2");
    }

    /**
     * Encryption, using BCrypt.
     * The larger the parameter, the more encryption times, the longer the time.
     * The default is 10.
     *
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
