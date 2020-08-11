package com.fit2cloud.mc.strategy.factory;

import com.fit2cloud.mc.strategy.service.ModelOperateService;
import com.fit2cloud.mc.strategy.service.ModelOperateStrategy;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/10 9:39 上午
 * @Description: Please Write notes scientifically
 */

@Component
public class ModelOperateStrategyFactory implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    public static ModelOperateStrategy build(String modele){
        return context.getBean(modele,ModelOperateStrategy.class);
    }


}
