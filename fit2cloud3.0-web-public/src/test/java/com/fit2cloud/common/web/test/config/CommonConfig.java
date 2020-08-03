package com.fit2cloud.common.web.test.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 *
 */
@PropertySource(value = {
        "classpath:properties/global.properties",
        "classpath:properties/quartz.properties",
        "file:/opt/fit2cloud/conf/fit2cloud.properties",
}, encoding = "UTF-8", ignoreResourceNotFound = true)
@Configuration
public class CommonConfig {

    
}
