package com.fit2cloud.autoconfigure;

import com.fit2cloud.commons.pluginmanager.CloudProviderManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class PluginManagerAutoConfiguration {
    @Bean(initMethod = "init")
    @ConditionalOnMissingBean
    public CloudProviderManager cloudProviderManager() {
        String pluginLocation = "/opt/fit2cloud/share/plugins";
        File file = new File(pluginLocation);
        if (!file.exists()) {
            pluginLocation = "/opt/fit2cloud/plugins";
        }
        return new CloudProviderManager(pluginLocation, "com.fit2cloud");
    }
}
