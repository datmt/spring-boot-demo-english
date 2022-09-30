package com.xkcoding.dynamic.datasource.aspect;

import com.xkcoding.dynamic.datasource.annotation.DefaultDatasource;
import com.xkcoding.dynamic.datasource.datasource.DatasourceConfigContextHolder;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * <p>
 * Data source selector slice
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019-09-04 16:52
 */
@Aspect
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DatasourceSelectorAspect {
    @Pointcut("execution(public * com.xkcoding.dynamic.datasource.controller.*.*(..))")
    public void datasourcePointcut() {
    }

    /**
     * Front-end operation, intercept specific requests, get the data source id in the header, set the thread variable, and use it for subsequent switching data sources
     */
    @Before("datasourcePointcut()")
    public void doBefore(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();

        Excludes methods that do not switch data sources
        DefaultDatasource annotation = method.getAnnotation(DefaultDatasource.class);
        if (null != annotation) {
            DatasourceConfigContextHolder.setDefaultDatasource();
        } else {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
            HttpServletRequest request = attributes.getRequest();
            String configIdInHeader = request.getHeader("Datasource-Config-Id");
            if (StringUtils.hasText(configIdInHeader)) {
                long configId = Long.parseLong(configIdInHeader);
                DatasourceConfigContextHolder.setCurrentDatasourceConfig(configId);
            } else {
                DatasourceConfigContextHolder.setDefaultDatasource();
            }
        }
    }

    /**
     * Post-operation, set back to the default data source id
     */
    @AfterReturning("datasourcePointcut()")
    public void doAfter() {
        DatasourceConfigContextHolder.setDefaultDatasource();
    }

}
