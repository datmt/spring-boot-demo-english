package com.xkcoding.war;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * <p>
 * Launcher
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-10-30 19:37
 */
@SpringBootApplication
public class SpringBootDemoWarApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootDemoWarApplication.class, args);
    }

    /**
     * If you need to type into a war package, you need to write a class inheritance {@link SpringBootServletInitializer} and rewrite {@link SpringBootServletInitializer#configure(SpringApplicationBuilder)}
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SpringBootDemoWarApplication.class);
    }
}
