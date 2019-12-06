package com.oyf.permission.aspectj;

import com.oyf.permission.annotation.LimitClick;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;


/**
 * @创建者 oyf
 * @创建时间 2019/12/5 16:27
 * @描述  点击限制拦截器
 **/
@Aspect
public class LimitClickAspectJ {

    @Pointcut("execution(@com.oyf.permission.annotation.LimitClick * *(..))")
    public void limit() {
    }

    private static long lastClickTime = 0;

    @Around("limit()")
    public Object limitClick(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();
        if (!(signature instanceof MethodSignature)) {
            return joinPoint.proceed();
        }
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        LimitClick annotation = method.getAnnotation(LimitClick.class);
        if (annotation == null) {
            return joinPoint.proceed();
        }
        int delay = annotation.value();
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - lastClickTime > delay) {
            lastClickTime = currentTimeMillis;
            return joinPoint.proceed();
        }
        return null;
    }
}
