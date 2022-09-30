package com.xkcoding.zookeeper.aspectj;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.xkcoding.zookeeper.annotation.LockKeyParam;
import com.xkcoding.zookeeper.annotation.ZooLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * <p>
 * Use aop slices to record request log information
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-10-01 22:05
 */
@Aspect
@Component
@Slf4j
public class ZooLockAspect {
    private final CuratorFramework zkClient;

    private static final String KEY_PREFIX = "DISTRIBUTED_LOCK_";

    private static final String KEY_SEPARATOR = "/";

    @Autowired
    public ZooLockAspect(CuratorFramework zkClient) {
        this.zkClient = zkClient;
    }

    /**
     * Entry point
     */
    @Pointcut("@annotation(com.xkcoding.zookeeper.annotation.ZooLock)")
    public void doLock() {

    }

    /**
     * Wrap operation
     *
     * @param point pointcut
     * @return Original method return value
     * @throws Throwable exception information
     */
    @Around("doLock()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Object[] args = point.getArgs();
        ZooLock zooLock = method.getAnnotation(ZooLock.class);
        if (StrUtil.isBlank(zooLock.key())) {
            throw new RuntimeException("分布式锁键不能为空");
        }
        String lockKey = buildLockKey(zooLock, method, args);
        InterProcessMutex lock = new InterProcessMutex(zkClient, lockKey);
        try {
            Assuming that the lock is successful, everything you get later is false
            if (lock.acquire(zooLock.timeout(), zooLock.timeUnit())) {
                return point.proceed();
            } else {
                throw new RuntimeException("请勿重复提交");
            }
        } finally {
            lock.release();
        }
    }

    /**
     * The key to construct a distributed lock
     *
     * @param lock annotation
     * @param method annotates the method of markup
     * @param parameters on the args method
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private String buildLockKey(ZooLock lock, Method method, Object[] args) throws NoSuchFieldException, IllegalAccessException {
        StringBuilder key = new StringBuilder(KEY_SEPARATOR + KEY_PREFIX + lock.key());

        Iteration of the annotation of all parameters, according to the subscript of the parameter of the annotation using LockKeyParam, to obtain the parameter value of the corresponding subscript in args and spliced to the first half of the key
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();

        for (int i = 0; i < parameterAnnotations.length; i++) {
            Loop through all annotations for this parameter
            for (Annotation annotation : parameterAnnotations[i]) {
                The annotation is not @LockKeyParam
                if (!annotation.annotationType().isInstance(LockKeyParam.class)) {
                    continue;
                }

                Get all fields
                String[] fields = ((LockKeyParam) annotation).fields();
                if (ArrayUtil.isEmpty(fields)) {
                    Normal data types are directly stitched
                    if (ObjectUtil.isNull(args[i])) {
                        throw new RuntimeException("动态参数不能为null");
                    }
                    key.append(KEY_SEPARATOR).append(args[i]);
                } else {
                    The fields value of the @LockKeyParam is not null, so the current parameter should be the object type
                    for (String field : fields) {
                        Class<?> clazz = args[i].getClass();
                        Field declaredField = clazz.getDeclaredField(field);
                        declaredField.setAccessible(true);
                        Object value = declaredField.get(clazz);
                        key.append(KEY_SEPARATOR).append(value);
                    }
                }
            }
        }
        return key.toString();
    }

}
