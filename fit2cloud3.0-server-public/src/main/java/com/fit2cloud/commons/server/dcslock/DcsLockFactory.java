package com.fit2cloud.commons.server.dcslock;

import com.fit2cloud.commons.server.dcslock.service.DcsLockService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/11 3:07 下午
 * @Description: Please Write notes scientifically
 */
@Component
public class DcsLockFactory implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static DcsLockService getService(String serviceName){
        return context.getBean(serviceName,DcsLockService.class);
    }
}
