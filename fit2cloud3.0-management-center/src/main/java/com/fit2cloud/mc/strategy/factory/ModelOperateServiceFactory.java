package com.fit2cloud.mc.strategy.factory;

import com.fit2cloud.mc.strategy.service.ModelOperateService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/10 9:39 上午
 * @Description: Please Write notes scientifically
 */
public class ModelOperateServiceFactory implements ApplicationContextAware {

    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }




}
