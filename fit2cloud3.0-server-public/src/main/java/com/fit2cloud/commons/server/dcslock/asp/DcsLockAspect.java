package com.fit2cloud.commons.server.dcslock.asp;

import com.fit2cloud.commons.server.dcslock.DcsLockFactory;
import com.fit2cloud.commons.server.dcslock.annotation.DcsLock;
import com.fit2cloud.commons.server.base.domain.MethodInfo;
import com.fit2cloud.commons.server.dcslock.service.DcsLockService;
import com.fit2cloud.commons.server.exception.F2CException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/11 12:29 下午
 * @Description: Please Write notes scientifically
 * 分布式锁切面实现
 */

@Aspect
@Component
@Lazy(false)
public class DcsLockAspect {

    @Value("${dcsLockType:dbLock}")
    private String dcsLockType;

    private Logger log = LoggerFactory.getLogger(DcsLockAspect.class);

    @Pointcut("@annotation(com.fit2cloud.commons.server.dcslock.annotation.DcsLock)")
    private void cut(){}

    @Around("cut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Exception {
        Object[] args = proceedingJoinPoint.getArgs();
        MethodInfo methodInfo = getMethodAnnotation(proceedingJoinPoint);
        DcsLock dcsLock = methodInfo.getDcsLock();
        String methodName = methodInfo.getMethod().getName();
        String className = methodInfo.getaClass().getName();
        try{
            DcsLockService lockService = DcsLockFactory.getService(dcsLockType);
            String key = className+methodName+dcsLock.key();
            Long overtime = dcsLock.overtime();
            Long waitime = dcsLock.waitime();
            if(lockService.tryLock(key,overtime,waitime)){
                Object result = proceedingJoinPoint.proceed();
                return result;
            }
        } catch (Throwable throwable) {
            //throwable.printStackTrace();
            throw new Exception(throwable);
            //F2CException.throwException(new Exception(throwable));
        }
        return null;
    }

    private MethodInfo getMethodAnnotation(JoinPoint joinPoint) throws NoSuchMethodException {
        MethodInfo methodInfo = new MethodInfo();
        // 获取方法名
        String methodName = joinPoint.getSignature().getName();
        // 反射获取目标类
        Class<?> targetClass = joinPoint.getTarget().getClass();
        methodInfo.setaClass(targetClass);
        // 拿到方法对应的参数类型
        Class<?>[] parameterTypes = ((MethodSignature) joinPoint.getSignature()).getParameterTypes();
        // 根据类、方法、参数类型（重载）获取到方法的具体信息
        Method objMethod = targetClass.getMethod(methodName, parameterTypes);
        methodInfo.setMethod(objMethod);
        // 拿到方法定义的注解信息
        methodInfo.setDcsLock(objMethod.getDeclaredAnnotation(DcsLock.class));
        return methodInfo;
    }




}
