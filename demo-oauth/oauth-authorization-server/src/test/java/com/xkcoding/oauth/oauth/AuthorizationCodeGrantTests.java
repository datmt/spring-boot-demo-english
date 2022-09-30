package com.xkcoding.oauth.oauth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.xkcoding.oauth.oauth.AuthorizationServerInfo.getUrl;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Authorization code mode test.
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2020-01-06 20:43
 */
public class AuthorizationCodeGrantTests {

    private AuthorizationCodeResourceDetails resource = new AuthorizationCodeResourceDetails();
    private AuthorizationServerInfo authorizationServerInfo = new AuthorizationServerInfo();

    @BeforeEach
    void setUp() {
        resource.setAccessTokenUri(getUrl("/oauth/token"));
        resource.setClientId("oauth2");
        resource.setId("oauth2");
        resource.setScope(Arrays.asList("READ", "WRITE"));
        resource.setAccessTokenUri(getUrl("/oauth/token"));
        resource.setUserAuthorizationUri(getUrl("/oauth/authorize"));
    }

    @Test
    void testCannotConnectWithoutToken() {
        OAuth2RestTemplate template = new OAuth2RestTemplate(resource);
        assertThrows(UserRedirectRequiredException.class, () -> template.getForObject(getUrl("/oauth/me"), String.class));
    }

    @Test
    void testAttemptedTokenAcquisitionWithNoRedirect() {
        AuthorizationCodeAccessTokenProvider provider = new AuthorizationCodeAccessTokenProvider();
        assertThrows(UserRedirectRequiredException.class, () -> provider.obtainAccessToken(resource, new DefaultAccessTokenRequest()));
    }

    /**
     * The reason he provided is not used here is because many places do not meet our needs
     * Such as csrf, such as many of the endpoints that are customized by themselves
     * So only we have to test step by step to get the authorization code
     */
    @Test
    void testCodeAcquisitionWithCorrectContext() {
         1. Request the login page to get the value of _csrf and cookies
        ResponseEntity<String> page = authorizationServerInfo.getForString("/oauth/login");
        assertNotNull(page.getBody());
        String cookie = page.getHeaders().getFirst("Set-Cookie");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", cookie);
        Matcher matcher = Pattern.compile("(?s).*name=\"_csrf\".*?value=\"([^\"]+).*").matcher(page.getBody());
        assertTrue(matcher.find());

         2. Add form data
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("username", "admin");
        form.add("password", "123456");
        form.add("_csrf", matcher.group(1));

         3. Login authorization and obtain a cookie for successful login
        ResponseEntity<Void> response = authorizationServerInfo.postForStatus("/authorization/form", headers, form);
        assertNotNull(response);
        cookie = response.getHeaders().getFirst("Set-Cookie");
        headers = new HttpHeaders();
        headers.set("Cookie", cookie);
        headers.setAccept(Collections.singletonList(MediaType.ALL));

         4. Request to the Confirm Authorization page to get the value of the _csrf of the Confirm Authorization page
        ResponseEntity<String> confirm = authorizationServerInfo.getForString("/oauth/authorize?response_type=code&client_id=oauth2&redirect_uri=http://example.com&scope=READ", headers);

        headers = confirm.getHeaders();
        After confirming once, it will be automatically confirmed later, and here it is determined whether it is a redirect request
        If not, it means that it is the first time that the authorization needs to be confirmed
        if (!confirm.getStatusCode().is3xxRedirection()) {
            assertNotNull(confirm.getBody());
            Matcher matcherConfirm = Pattern.compile("(?s).*name=\"_csrf\".*?value=\"([^\"]+).*").matcher(confirm.getBody());
            assertTrue(matcherConfirm.find());
            headers = new HttpHeaders();
            headers.set("Cookie", cookie);
            headers.setAccept(Collections.singletonList(MediaType.ALL));

             5. Build a form for consent authorization
            form = new LinkedMultiValueMap<>();
            form.add("user_oauth_approval", "true");
            form.add("scope.READ", "true");
            form.add("_csrf", matcherConfirm.group(1));

             6. Request authorization, obtain the authorization code
            headers = authorizationServerInfo.postForHeaders("/oauth/authorize", form, headers);
        }

        URI location = headers.getLocation();
        assertNotNull(location);
        String query = location.getQuery();
        assertNotNull(query);
        String[] result = query.split("=");
        assertEquals(2, result.length);
        System.out.println(result[1]);
    }

}
