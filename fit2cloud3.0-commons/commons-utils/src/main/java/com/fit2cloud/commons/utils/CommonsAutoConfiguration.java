package com.fit2cloud.commons.utils;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

@Configuration
public class CommonsAutoConfiguration {
    @Resource
    private Environment environment;

    @Bean
    @ConditionalOnMissingBean
    public CommonBeanFactory commonBeanFactory() {
        return new CommonBeanFactory();
    }

    @Bean
    public GlobalConfigurations globalConfigurations() {
        return new GlobalConfigurations(environment);
    }
}
