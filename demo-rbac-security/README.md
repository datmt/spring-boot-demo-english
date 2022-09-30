# spring-boot-demo-rbac-security

> This demo demonstrates how the Spring Boot project integrates with Spring Security to complete permission blocking operations. This demo is based on the backend rights management section of the front and back end separation, which is different from the template technology used in other blogs, and I hope it will be helpful to everyone.

## 1. Key features

- [x] Based on 'RBAC' permission model design, see Database Table Structure Design ['security.sql'](./sql/security.sql)
- [x] Support **Dynamic permission management**, see ['RbacAuthorityService.java'](./src/main/java/com/xkcoding/rbac/security/config/RbacAuthorityService.java)
- [x] **Login/Logout** sections are implemented using custom controllers, not using the internal default implementation of 'Spring Security', for front-end and back-end separation projects, see ['SecurityConfig.java'] (./src/main/java/com/xkcoding/rbac/security/config/ SecurityConfig.java) and ['AuthController.java'](./src/main/java/com/xkcoding/rbac/security/controller/AuthController.java)
- [x] The persistence technique is done using 'spring-data-jpa'
- [x] Use 'JWT' to implement security authentication, and introduce 'Redis' to solve the drawbacks of 'JWT' can not manually set the expiration of the drawback, and ensure that the same user only supports the same device login at the same time, different devices will log in, see ['JwtUtil.java'] (./src/main/java/com/xkcoding/rbac/security/util/JwtUtil.java)
- [x] Online Demographics for details on ['MonitorService.java'](./src/main/java/com/xkcoding/rbac/security/service/MonitorService.java) and ['RedisUtil.java'](./src/main/java/com/xkcoding/rbac/ security/util/RedisUtil.java)
- [x] Manually kick out the user, see ['MonitorService.java'](./src/main/java/com/xkcoding/rbac/security/service/MonitorService.java)
- [x] Custom configuration requests that do not require interception, see ['CustomConfig.java'](./src/main/java/com/xkcoding/rbac/security/config/CustomConfig.java) and ['application.yml'](./src/main/resources/ application.yml)

## 2. run

### 2.1. environment

1. JDK 1.8 or above
2. Maven 3.5 and above
3. Mysql 5.7 or above
4. Redis

### 2.2. Operates in

1. Create a new database named 'spring-boot-demo' with the character set set to 'utf-8', if the database name is not 'spring-boot-demo' you need to modify 'spring.datasource.url' in 'application.yml'
2. Using the SQL file ['security.sql'] (./sql/security.sql), create database tables and initialize RBAC data
3. Run 'SpringBootDemoRbacSecurityApplication'
4. Administrator account: admin/123456 Normal user: user/123456
5. Use 'POST' to request access to the '/${contextPath}/api/auth/login' endpoint, enter the account password, return to the token after successful login, put the obtained token in the header of the specific request, the key is fixed as 'Authorization', the value prefix is 'Bearer followed by space' and then add the token, and add the parameters of the specific request, you can
6. enjoy ~​ :kissing_smiling_eyes:

## 3. Part of the key code

### 3.1. pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <artifactId>spring-boot-demo-rbac-security</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>spring-boot-demo-rbac-security</name>
    <description>Demo project for Spring Boot</description>

    <parent>
        <groupId>com.xkcoding</groupId>
        <artifactId>spring-boot-demo</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
        <jjwt.veersion>0.9.1</jjwt.veersion>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>

        <!-- object pool, --> must be introduced when using redis
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-pool2</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>

        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt</artifactId>
            <version>${jjwt.veersion}</version>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>

    <build>
        <finalName>spring-boot-demo-rbac-security</finalName>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

### 3.2. JwtUtil.java

> JWT tool class, the main functions: generate JWT and store it in Redis, parse the JWT and verify its accuracy, get the JWT from the Header of the Request

```java
/**
 * <p>
 * JWT tool class
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-07 13:42
 */
@EnableConfigurationProperties(JwtConfig.class)
@Configuration
@Slf4j
public class JwtUtil {
    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * Create JWT
     *
     * @param rememberMe remember me
     * @param id user ID
     * @param subject username
     * @param roles user role
     * @param authorities user rights
     * @return JWT
     */
    public String createJWT(Boolean rememberMe, Long id, String subject, List<String> roles, Collection<? extends GrantedAuthority> authorities) {
        Date now = new Date();
        JwtBuilder builder = Jwts.builder()
                .setId(id.toString())
                .setSubject(subject)
                .setIssuedAt(now)
                .signWith(SignatureAlgorithm.HS256, jwtConfig.getKey())
                .claim("roles", roles)
                .claim("authorities", authorities);

        Set the expiration time
        Long ttl = rememberMe ? jwtConfig.getRemember() : jwtConfig.getTtl();
        if (ttl > 0) {
            builder.setExpiration(DateUtil.offsetMillisecond(now, ttl.intValue()));
        }

        String jwt = builder.compact();
        Save the generated JWT to Redis
        stringRedisTemplate.opsForValue()
                .set(Consts.REDIS_JWT_KEY_PREFIX + subject, jwt, ttl, TimeUnit.MILLISECONDS);
        return jwt;
    }

    /**
     * Create JWT
     *
     * @param authentication user authentication information
     * @param rememberMe remember me
     * @return JWT
     */
    public String createJWT(Authentication authentication, Boolean rememberMe) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return createJWT(rememberMe, userPrincipal.getId(), userPrincipal.getUsername(), userPrincipal.getRoles(), userPrincipal.getAuthorities());
    }

    /**
     * Parse JWT
     *
     * @param jwt JWT
     * @return {@link Claims}
     */
    public Claims parseJWT(String jwt) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtConfig.getKey())
                    .parseClaimsJws(jwt)
                    .getBody();

            String username = claims.getSubject();
            String redisKey = Consts.REDIS_JWT_KEY_PREFIX + username;

            Check whether JWT exists in redis
            Long expire = stringRedisTemplate.getExpire(redisKey, TimeUnit.MILLISECONDS);
            if (Objects.isNull(expire) || expire <= 0) {
                throw new SecurityException(Status.TOKEN_EXPIRED);
            }

            Check whether the JWT in redis is consistent with the current one, and if it is inconsistent, it means that the user has logged out/the user has logged in on a different device, which means that the JWT has expired
            String redisToken = stringRedisTemplate.opsForValue()
                    .get(redisKey);
            if (! StrUtil.equals(jwt, redisToken)) {
                throw new SecurityException(Status.TOKEN_OUT_OF_CTRL);
            }
            return claims;
        } catch (ExpiredJwtException e) {
            log.error("Token has expired");
            throw new SecurityException(Status.TOKEN_EXPIRED);
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported Token");
            throw new SecurityException(Status.TOKEN_PARSE_ERROR);
        } catch (MalformedJwtException e) {
            log.error("Invalid Token");
            throw new SecurityException(Status.TOKEN_PARSE_ERROR);
        } catch (SignatureException e) {
            log.error("Invalid token signature");
            throw new SecurityException(Status.TOKEN_PARSE_ERROR);
        } catch (IllegalArgumentException e) {
            log.error("Token parameter does not exist");
            throw new SecurityException(Status.TOKEN_PARSE_ERROR);
        }
    }

    /**
     * Set JWT expiration
     *
     * @param request request
     */
    public void invalidateJWT(HttpServletRequest request) {
        String jwt = getJwtFromRequest(request);
        String username = getUsernameFromJWT(jwt);
        Clear the JWT from redis
        stringRedisTemplate.delete(Consts.REDIS_JWT_KEY_PREFIX + username);
    }

    /**
     * Get username based on jwt
     *
     * @param jwt JWT
     * @return Username
     */
    public String getUsernameFromJWT(String jwt) {
        Claims claims = parseJWT(jwt);
        return claims.getSubject();
    }

    /**
     * Get the JWT from the header of the request
     *
     * @param request request
     * @return JWT
     */
    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StrUtil.isNotBlank(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
```

### 3.3. SecurityConfig.java

> Spring Security configuration class, the main function: configure which URLs do not require authentication and which do not

```java
/**
 * <p>
 * Security configuration
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-07 16:46
 */
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(CustomConfig.class)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private CustomConfig customConfig;

    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService)
                .passwordEncoder(encoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors()

                Close the CSRF
                .and()
                .csrf()
                .disable()

                The login behavior is implemented by yourself, see AuthController#login
                .formLogin()
                .disable()
                .httpBasic()
                .disable()

                Authentication request
                .authorizeRequests()
                All requests require login access
                .anyRequest()
                .authenticated()
                RBAC dynamic url authentication
                .anyRequest()
                .access("@rbacAuthorityService.hasPermission(request,authentication)")

                The logout behavior is implemented by yourself, see AuthController#logout
                .and()
                .logout()
                .disable()

                Session management
                .sessionManagement()
                Because JWT is used, sessions are not managed here
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                Exception handling
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler);

        Add a custom JWT filter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    /**
     * Allow all requests that do not require a login to access, see AuthController
     * can also be configured in {@link #configure (HttpSecurity)} 
     * {@code http.authorizeRequests().antMatchers("/api/auth/**").permitAll()}
     */
    @Override
    public void configure(WebSecurity web) {
        WebSecurity and = web.ignoring()
                .and();

        GET is ignored
        customConfig.getIgnores()
                .getGet()
                .forEach(url -> and.ignoring()
                        .antMatchers(HttpMethod.GET, url));

        Ignore POST
        customConfig.getIgnores()
                .getPost()
                .forEach(url -> and.ignoring()
                        .antMatchers(HttpMethod.POST, url));

        DELETE is ignored
        customConfig.getIgnores()
                .getDelete()
                .forEach(url -> and.ignoring()
                        .antMatchers(HttpMethod.DELETE, url));

        PUT is ignored
        customConfig.getIgnores()
                .getPut()
                .forEach(url -> and.ignoring()
                        .antMatchers(HttpMethod.PUT, url));

        HEAD is ignored
        customConfig.getIgnores()
                .getHead()
                .forEach(url -> and.ignoring()
                        .antMatchers(HttpMethod.HEAD, url));

        Ignore PATCH
        customConfig.getIgnores()
                .getPatch()
                .forEach(url -> and.ignoring()
                        .antMatchers(HttpMethod.PATCH, url));

        OPTIONS is ignored
        customConfig.getIgnores()
                .getOptions()
                .forEach(url -> and.ignoring()
                        .antMatchers(HttpMethod.OPTIONS, url));

        TRACE is ignored
        customConfig.getIgnores()
                .getTrace()
                .forEach(url -> and.ignoring()
                        .antMatchers(HttpMethod.TRACE, url));

        Ignored in the requested format
        customConfig.getIgnores()
                .getPattern()
                .forEach(url -> and.ignoring()
                        .antMatchers(url));

    }
}
```

### 3.4. RbacAuthorityService.java

> Dynamic authentication class of routes, main functions:
>
> 1. Check the legitimacy of the request and exclude the two kinds of exception requests 404 and 405
> 2. Matches the resource that the user can access according to the current request path, and can be accessed through it, otherwise, the access is not allowed

```java
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
            List<Long> roleIds = roles.stream()
                    .map(Role::getId)
                    .collect(Collectors.toList());
            List<Permission> permissions = permissionDao.selectByRoleIdList(roleIds);

            Get the resources, the front and back end are separated, so filter the page permissions, and only retain the button permissions
            List<Permission> btnPerms = permissions.stream()
                    Filter page permissions
                    .filter(permission -> Objects.equals(permission.getType(), Consts.BUTTON))
                    The filter URL is empty
                    .filter(permission -> StrUtil.isNotBlank(permission.getUrl()))
                    Filter METHOD is empty
                    .filter(permission -> StrUtil.isNotBlank(permission.getMethod()))
                    .collect(Collectors.toList());

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
                if (!urlMapping.get(uri)
                        .contains(currentMethod)) {
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
            Set<String> url = k.getPatternsCondition()
                    .getPatterns();
            RequestMethodsRequestCondition method = k.getMethodsCondition();

            Add all request methods for each URL
            url.forEach(s -> urlMapping.putAll(s, method.getMethods()
                    .stream()
                    .map(Enum::toString)
                    .collect(Collectors.toList())));
        });

        return urlMapping;
    }
}
```

### 3.5. JwtAuthenticationFilter.java

> JWT certification filter, main functions:
>
> 1. Filter requests that do not need to be blocked
> 2. Authenticate the user identity information according to the JWT of the current request

```java
/**
 * <p>
 * Jwt certified filter
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-10 15:15
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomConfig customConfig;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (checkIgnores(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = jwtUtil.getJwtFromRequest(request);

        if (StrUtil.isNotBlank(jwt)) {
            try {
                String username = jwtUtil.getUsernameFromJWT(jwt);

                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
                filterChain.doFilter(request, response);
            } catch (SecurityException e) {
                ResponseUtil.renderJson(response, e);
            }
        } else {
            ResponseUtil.renderJson(response, Status.UNAUTHORIZED, null);
        }

    }

    /**
     * Whether the request does not require permission interception
     *
     * @param request current request
     * @return true - ignore, false - do not ignore
     */
    private boolean checkIgnores(HttpServletRequest request) {
        String method = request.getMethod();

        HttpMethod httpMethod = HttpMethod.resolve(method);
        if (ObjectUtil.isNull(httpMethod)) {
            httpMethod = HttpMethod.GET;
        }

        Set<String> ignores = Sets.newHashSet();

        switch (httpMethod) {
            case GET:
                ignores.addAll(customConfig.getIgnores()
                        .getGet());
                break;
            case PUT:
                ignores.addAll(customConfig.getIgnores()
                        .getPut());
                break;
            case HEAD:
                ignores.addAll(customConfig.getIgnores()
                        .getHead());
                break;
            case POST:
                ignores.addAll(customConfig.getIgnores()
                        .getPost());
                break;
            case PATCH:
                ignores.addAll(customConfig.getIgnores()
                        .getPatch());
                break;
            case TRACE:
                ignores.addAll(customConfig.getIgnores()
                        .getTrace());
                break;
            case DELETE:
                ignores.addAll(customConfig.getIgnores()
                        .getDelete());
                break;
            case OPTIONS:
                ignores.addAll(customConfig.getIgnores()
                        .getOptions());
                break;
            default:
                break;
        }

        ignores.addAll(customConfig.getIgnores()
                .getPattern());

        if (CollUtil.isNotEmpty(ignores)) {
            for (String ignore : ignores) {
                AntPathRequestMatcher matcher = new AntPathRequestMatcher(ignore, method);
                if (matcher.matches(request)) {
                    return true;
                }
            }
        }

        return false;
    }

}
```

### 3.6. CustomUserDetailsService.java

> implements the 'UserDetailsService' interface, the main function: query user information according to the user name

```java
/**
 * <p>
 * Custom UserDetails query
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-10 10:29
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private PermissionDao permissionDao;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmailOrPhone) throws UsernameNotFoundException {
        User user = userDao.findByUsernameOrEmailOrPhone(usernameOrEmailOrPhone, usernameOrEmailOrPhone, usernameOrEmailOrPhone)
                .orElseThrow(() -> new UsernameNotFoundException ("User information not found : " + usernameOrEmailOrPhone));
        List<Role> roles = roleDao.selectByUserId(user.getId());
        List<Long> roleIds = roles.stream()
                .map(Role::getId)
                .collect(Collectors.toList());
        List<Permission> permissions = permissionDao.selectByRoleIdList(roleIds);
        return UserPrincipal.create(user, roles, permissions);
    }
}
```

### 3.7. RedisUtil.java

> main function: Pagination to get the list of keys that are present in Redis according to the format of the key

```java
/**
 * <p>
 * Redis tool class
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-11 20:24
 */
@Component
@Slf4j
public class RedisUtil {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * Paging to get the specified format key, using the scan command instead of the keys command, can improve query efficiency in the case of large volumes
     *
     * @param patternKey key format
     * @param currentPage current page number
     * @param pageSize per page
     * @return Pagination gets the specified format key
     */
    public PageResult<String> findKeysForPage(String patternKey, int currentPage, int pageSize) {
        ScanOptions options = ScanOptions.scanOptions()
                .match(patternKey)
                .build();
        RedisConnectionFactory factory = stringRedisTemplate.getConnectionFactory();
        RedisConnection rc = factory.getConnection();
        Cursor<byte[]> cursor = rc.scan(options);

        List<String> result = Lists.newArrayList();

        long tmpIndex = 0;
        int startIndex = (currentPage - 1) * pageSize;
        int end = currentPage * pageSize;
        while (cursor.hasNext()) {
            String key = new String(cursor.next());
            if (tmpIndex >= startIndex && tmpIndex < end) {
                result.add(key);
            }
            tmpIndex++;
        }

        try {
            cursor.close();
            RedisConnectionUtils.releaseConnection(rc, factory);
        } catch (Exception e) {
            log.warn ("Redis connection closed exception,", e);
        }

        return new PageResult<>(result, tmpIndex);
    }
}
```

### 3.8. MonitorService.java

> monitoring service, the main function: query the current online number of paging list, manually kick out a user

```java
package com.xkcoding.rbac.security.service;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.xkcoding.rbac.security.common.Consts;
import com.xkcoding.rbac.security.common.PageResult;
import com.xkcoding.rbac.security.model.User;
import com.xkcoding.rbac.security.repository.UserDao;
import com.xkcoding.rbac.security.util.RedisUtil;
import com.xkcoding.rbac.security.vo.OnlineUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * Monitor Service
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-12 00:55
 */
@Service
public class MonitorService {
    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserDao userDao;

    public PageResult<OnlineUser> onlineUser(Integer page, Integer size) {
        PageResult<String> keys = redisUtil.findKeysForPage(Consts.REDIS_JWT_KEY_PREFIX + Consts.SYMBOL_STAR, page, size);
        List<String> rows = keys.getRows();
        Long total = keys.getTotal();

        Gets a list of user names based on the redis middle key
        List<String> usernameList = rows.stream()
                .map(s -> StrUtil.subAfter(s, Consts.REDIS_JWT_KEY_PREFIX, true))
                .collect(Collectors.toList());
        Query user information based on user name
        List<User> userList = userDao.findByUsernameIn(usernameList);

        Encapsulate online user information
        List<OnlineUser> onlineUserList = Lists.newArrayList();
        userList.forEach(user -> onlineUserList.add(OnlineUser.create(user)));

        return new PageResult<>(onlineUserList, total);
    }
}
```

### 3.9. The rest of the code can be found in this demo

## 4. reference

1. Spring Security Official Documents: https://docs.spring.io/spring-security/site/docs/5.1.1.RELEASE/reference/htmlsingle/
2. JWT official website: https://jwt.io/
3. JJWT Open Source Tool Reference: https://github.com/jwtk/jjwt#quickstart
4. Refer to the official documentation: https://docs.spring.io/spring-security/site/docs/5.1.1.RELEASE/reference/htmlsingle/#authorization

4. Dynamic authorization section, refer to the blog: https://blog.csdn.net/larger5/article/details/81063438

