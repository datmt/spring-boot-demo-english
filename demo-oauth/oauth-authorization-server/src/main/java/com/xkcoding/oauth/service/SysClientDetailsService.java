package com.xkcoding.oauth.service;

import com.xkcoding.oauth.entity.SysClientDetails;
import org.springframework.security.oauth2.provider.ClientAlreadyExistsException;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.security.oauth2.provider.NoSuchClientException;

import java.util.List;

/**
 * Declare your own implementation.
 * See {@link ClientRegistrationService}
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2020-01-06 13:39
 */
public interface SysClientDetailsService extends ClientDetailsService {

    /**
     * Query by client id
     *
     * @param clientId client ID
     * @return Results
     */
    SysClientDetails findByClientId(String clientId);

    /**
     * Add client information.
     *
     * @param clientDetails client information
     * @throws The ClientAlreadyExistsException client already exists
     */
    void addClientDetails(SysClientDetails clientDetails) throws ClientAlreadyExistsException;

    /**
     * Updated client information, excluding clientSecret.
     *
     * @param clientDetails client information
     * @throws NoSuchClientException could not find client exception
     */
    void updateClientDetails(SysClientDetails clientDetails) throws NoSuchClientException;

    /**
     * Update client secret.
     *
     * @param clientId client ID
     * @param clientSecret client secret
     * @throws NoSuchClientException could not find client exception
     */
    void updateClientSecret(String clientId, String clientSecret) throws NoSuchClientException;

    /**
     * Delete client information.
     *
     * @param clientId client ID
     * @throws NoSuchClientException could not find client exception
     */
    void removeClientDetails(String clientId) throws NoSuchClientException;

    /**
     * Enquire all
     *
     * @return Results
     */
    List<SysClientDetails> findAll();
}
