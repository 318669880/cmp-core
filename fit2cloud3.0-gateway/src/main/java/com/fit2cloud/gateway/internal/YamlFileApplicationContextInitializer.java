package com.fit2cloud.gateway.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;

public class YamlFileApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private final static String YML_PATH = "file:/opt/fit2cloud/conf/gateway.yml";

    private Logger logger = LoggerFactory.getLogger(YamlFileApplicationContextInitializer.class);

    @Override
    public void initialize(@NonNull ConfigurableApplicationContext applicationContext) {
        try {
            Resource resource = applicationContext.getResource(YML_PATH);
            if (!resource.exists()) {
                logger.warn(YML_PATH + " not exist.");
                return;
            }
            YamlPropertySourceLoader sourceLoader = new YamlPropertySourceLoader();
            List<PropertySource<?>> yamlTestProperties = sourceLoader.load("fit2cloud-gateway", resource);
            if (CollectionUtils.isEmpty(yamlTestProperties)) {
                return;
            }
            applicationContext.getEnvironment().getPropertySources().addFirst(yamlTestProperties.get(0));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}