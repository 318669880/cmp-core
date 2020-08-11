package com.fit2cloud.commons.server.dcslock.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/11 12:21 下午
 * @Description: Please Write notes scientifically
 * 分布式锁
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface DcsLock {

    /**
     * 分布式锁key值
     * @return
     */
    String key() default "";

    /**
     * 超时时间 默认5s
     * @return
     *
     */
    long overtime() default 5000;

    /**
     * 等待时间 超过则重试获取锁 默认1.5s
     * @return
     */
    long waitime() default 1500;
}
