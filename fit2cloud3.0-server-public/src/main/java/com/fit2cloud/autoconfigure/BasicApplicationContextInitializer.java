package com.fit2cloud.autoconfigure;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class BasicApplicationContextInitializer implements ApplicationContextInitializer {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }
}
