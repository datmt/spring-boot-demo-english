package com.xkcoding.rbac.security.config;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.xkcoding.rbac.security.common.Consts;
import com.xkcoding.rbac.security.common.Status;
import com.xkcoding.rbac.security.exception.SecurityException;
import com.xkcoding.rbac.security.model.Permission;
import com.xkcoding.rbac.security.model.Role;
import com.xkcoding.rbac.security.repository.PermissionDao;
import com.xkcoding.rbac.security.repository.RoleDao;
import com.xkcoding.rbac.security.vo.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * Dynamic route authentication
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-10 17:17
 */
@Component
public class RbacAuthorityService {
    @Autowired
    private RoleDao roleDao;

    @Autowired
    private PermissionDao permissionDao;

    @Autowired
    private RequestMappingHandlerMapping mapping;

    public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
        checkRequest(request);

        Object userInfo = authentication.getPrincipal();
        boolean hasPermission = false;

        if (userInfo instanceof UserDetails) {
            UserPrincipal principal = (UserPrincipal) userInfo;
            Long userId = principal.getId();

            List<Role> roles = roleDao.selectByUserId(userId);
            List<Long> roleIds = roles.stream().map(Role::getId).collect(Collectors.toList());
            List<Permission> permissions = permissionDao.selectByRoleIdList(roleIds);

            Get the resources, the front and back end are separated, so filter the page permissions, and only retain the button permissions
            List<Permission> btnPerms = permissions.stream()
                Filter page permissions
                .filter(permission -> Objects.equals(permission.getType(), Consts.BUTTON))
                The filter URL is empty
                .filter(permission -> StrUtil.isNotBlank(permission.getUrl()))
                Filter METHOD is empty
                .filter(permission -> StrUtil.isNotBlank(permission.getMethod())).collect(Collectors.toList());

            for (Permission btnPerm : btnPerms) {
                AntPathRequestMatcher antPathMatcher = new AntPathRequestMatcher(btnPerm.getUrl(), btnPerm.getMethod());
                if (antPathMatcher.matches(request)) {
                    hasPermission = true;
                    break;
                }
            }

            return hasPermission;
        } else {
            return false;
        }
    }

    /**
     * Check if the request exists
     *
     * @param request request
     */
    private void checkRequest(HttpServletRequest request) {
        The method that gets the current request
        String currentMethod = request.getMethod();
        Multimap<String, String> urlMapping = allUrlMapping();

        for (String uri : urlMapping.keySet()) {
            Match URLs via AntPathRequestMatcher
            There are 2 ways to create an AntPathRequestMatcher
            1:new AntPathRequestMatcher(uri,method) This way can directly determine whether the method matches or not, because here we throw the Method Mismatch custom throw, so we use the second way to create
            2: new AntPathRequestMatcher(uri) This method does not verify the request method, only the request path
            AntPathRequestMatcher antPathMatcher = new AntPathRequestMatcher(uri);
            if (antPathMatcher.matches(request)) {
                if (!urlMapping.get(uri).contains(currentMethod)) {
                    throw new SecurityException(Status.HTTP_BAD_METHOD);
                } else {
                    return;
                }
            }
        }

        throw new SecurityException(Status.REQUEST_NOT_FOUND);
    }

    /**
     * Get all URL mappings in the format {"/test":["GET","POST"],"/sys:":["GET","DELETE"]}
     *
     * @return URL Mapping in the format {@link ArrayListMultimap}
     */
    private Multimap<String, String> allUrlMapping() {
        Multimap<String, String> urlMapping = ArrayListMultimap.create();

        Gets the information about URLs to classes and methods
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = mapping.getHandlerMethods();

        handlerMethods.forEach((k, v) -> {
            Get all URLs under the current key
            Set<String> url = k.getPatternsCondition().getPatterns();
            RequestMethodsRequestCondition method = k.getMethodsCondition();

            Add all request methods for each URL
            url.forEach(s -> urlMapping.putAll(s, method.getMethods().stream().map(Enum::toString).collect(Collectors.toList())));
        });

        return urlMapping;
    }
}
