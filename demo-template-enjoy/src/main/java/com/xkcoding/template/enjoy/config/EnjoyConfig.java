package com.xkcoding.template.enjoy.config;

import com.jfinal.template.ext.spring.JFinalViewResolver;
import com.jfinal.template.source.ClassPathSourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * Enjoy template configuration class
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-10-11 14:06
 */
@Configuration
public class EnjoyConfig {
    @Bean(name = "jfinalViewResolver")
    public JFinalViewResolver getJFinalViewResolver() {
        JFinalViewResolver jfr = new JFinalViewResolver();
        The setDevMode configuration is placed first
        jfr.setDevMode(true);
        Use ClassPathSourceFactory to load template files from class path and jar packages
        jfr.setSourceFactory(new ClassPathSourceFactory());
        Use setBaseTemplatePath when using ClassPathSourceFactory
        instead of jfr.setPrefix("/view/")
        JFinalViewResolver.engine.setBaseTemplatePath("/templates/");

        jfr.setSessionInView(true);
        jfr.setSuffix(".html");
        jfr.setContentType("text/html;charset=UTF-8");
        jfr.setOrder(0);
        return jfr;
    }
}
