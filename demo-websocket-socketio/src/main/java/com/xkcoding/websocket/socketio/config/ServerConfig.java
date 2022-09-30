package com.xkcoding.websocket.socketio.config;

import cn.hutool.core.util.StrUtil;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * Websocket server configuration
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-18 16:42
 */
@Configuration
@EnableConfigurationProperties({WsConfig.class})
public class ServerConfig {

    @Bean
    public SocketIOServer server(WsConfig wsConfig) {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname(wsConfig.getHost());
        config.setPort(wsConfig.getPort());

        This listener can be used for authentication
        config.setAuthorizationListener(data -> {
             http://localhost:8081?token=xxxxxxx
            For example, if you use the above link for connect, you can use the following code to obtain the user password information, this document does not do authentication
            String token = data.getSingleUrlParam("token");
            To verify the legitimacy of the token, the actual business needs to verify whether the token has expired, etc., refer to JwtUtil in spring-boot-demo-rbac-security
            If the authentication fails, a Socket.EVENT_CONNECT_ERROR event is returned
            return StrUtil.isNotBlank(token);
        });

        return new SocketIOServer(config);
    }

    /**
     * Spring scans custom annotations
     */
    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer server) {
        return new SpringAnnotationScanner(server);
    }
}
