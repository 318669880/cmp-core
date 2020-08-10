package com.fit2cloud.mc.strategy.factory;

import com.fit2cloud.mc.strategy.service.ModelOperateService;
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
public class ModelOperateServiceFactory implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    public static ModelOperateService build(String modele){
        //allService().entrySet().stream().filter((k,v) -> )
        return context.getBean(modele,ModelOperateService.class);

    }

    private static Map<String, ModelOperateService> allService(){
        Map<String, ModelOperateService> serviceList = context.getBeansOfType(ModelOperateService.class);
        return serviceList;
    }




}
