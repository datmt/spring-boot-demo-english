/**
 * Controller. In addition to the business logic, two controllers are provided to help with customization:
 * {@link com.xkcoding.oauth.controller.AuthorizationController}
 * Customize the authorization controller, reset to our interface, do not use his default implementation
 * <p>
 * {@link com.xkcoding.oauth.controller.Oauth2Controller}
 * Page jump controller, take it out here because you can really do a lot of things. For example, when you log in, you can bring something with you
 * Or whatever logo you bring when you exit.
 *
 * @author <a href="https://echocow.cn">EchoCow</a>
 * @date 2020-01-07 11:25
 * @see org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint
 */
package com.xkcoding.oauth.controller;
