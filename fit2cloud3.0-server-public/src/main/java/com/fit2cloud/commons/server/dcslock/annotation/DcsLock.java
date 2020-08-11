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
    String key();

    /**
     * 超时时间 超过则释放锁 默认-1 永远不超时
     * @return
     */
    long overtime() default -1;

    /**
     * 等待时间 超过则重试获取锁
     * @return
     */
    long waitime() default -1;
}
