package com.xkcoding.oauth.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Client information.
 * The ClientDetails interface is implemented here
 * Personal advice should not write any logical code inside an entity class
 * In order to avoid the importance of entity class coupling, this interface should not be implemented
 * But here in order to demonstrate a different way from {@link SysUser}, so I chose to implement this interface
 * Another way is to write a method that turns it into a default implementation of {@link BaseClientDetails} which is a bit better and much simpler
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2020-01-06 12:54
 */
@Data
@Table
@Entity
public class SysClientDetails implements ClientDetails {

    /**
     * Primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * client id
     */
    private String clientId;

    /**
     * client key
     */
    private String clientSecret;

    /**
     * Resource server name
     */
    private String resourceIds;

    /**
     * Authorized domain
     */
    private String scopes;

    /**
     * Authorization type
     */
    private String grantTypes;

    /**
     * Redirect address, authorization code is required
     */
    private String redirectUrl;

    /**
     * Authorization information
     */
    private String authorizations;

    /**
     * Authorization token validity time
     */
    private Integer accessTokenValiditySeconds;

    /**
     * Refresh token validity time
     */
    private Integer refreshTokenValiditySeconds;

    /**
     * Automatic authorization request domain
     */
    private String autoApproveScopes;

    /**
     * Whether it is safe or not
     *
     * @return Results
     */
    @Override
    public boolean isSecretRequired() {
        return this.clientSecret != null;
    }

    /**
     * Whether there are scopes
     *
     * @return Results
     */
    @Override
    public boolean isScoped() {
        return this.scopes != null && !this.scopes.isEmpty();
    }

    /**
     * scopes
     *
     * @return scopes
     */
    @Override
    public Set<String> getScope() {
        return stringToSet(scopes);
    }

    /**
     * Authorization type
     *
     * @return Results
     */
    @Override
    public Set<String> getAuthorizedGrantTypes() {
        return stringToSet(grantTypes);
    }

    @Override
    public Set<String> getResourceIds() {
        return stringToSet(resourceIds);
    }


    /**
     * Get callback address
     *
     * @return redirectUrl
     */
    @Override
    public Set<String> getRegisteredRedirectUri() {
        return stringToSet(redirectUrl);
    }

    /**
     * Need to mention it here
     * Personally feel that this should be all the permissions of the client
     * But there is already a scope that can be a good way to authenticate the client's permissions
     * Then in the four roles of oauth2, there may be permissions for the resource server
     * However, the general resource server has its own permission management mechanism, such as RBAC after getting the user information
     * So in the default implementation of spring security is a set of empty directly
     * Here we also give him an empty handle
     *
     * @return GrantedAuthority
     */
    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    /**
     * Determine whether to automatically authorize
     *
     * @param scope scope
     * @return Results
     */
    @Override
    public boolean isAutoApprove(String scope) {
        if (autoApproveScopes == null || autoApproveScopes.isEmpty()) {
            return false;
        }
        Set<String> authorizationSet = stringToSet(authorizations);
        for (String auto : authorizationSet) {
            if ("true".equalsIgnoreCase(auto) || scope.matches(auto)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Additional information is a reserved field for spring security
     * Not used temporarily, just give an empty one
     *
     * @return map
     */
    @Override
    public Map<String, Object> getAdditionalInformation() {
        return Collections.emptyMap();
    }

    private Set<String> stringToSet(String s) {
        return Arrays.stream(s.split(",")).collect(Collectors.toSet());
    }
}
