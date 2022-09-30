package com.xkcoding.dubbo.provider.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.xkcoding.dubbo.common.service.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * <p>
 * Hello service implementation
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-12-25 16:58
 */
@Service
@Component
@Slf4j
public class HelloServiceImpl implements HelloService {
    /**
     * Say hello
     *
     * @param name name
     * @return Say hello
     */
    @Override
    public String sayHello(String name) {
        log.info("someone is calling me......");
        return "say hello to: " + name;
    }
}
