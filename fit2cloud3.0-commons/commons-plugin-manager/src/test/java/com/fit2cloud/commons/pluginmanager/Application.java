package com.fit2cloud.commons.pluginmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;

@SpringBootApplication
public class Application {
    @Bean(initMethod = "init")
    public CloudProviderManager cloudProviderManager() {
        String pluginLocation = "/opt/fit2cloud/plugins";
        File file = new File(pluginLocation);
        if (!file.exists()) {
            pluginLocation = "/opt/fit2cloud/share/plugins";
        }
        return new CloudProviderManager(pluginLocation, "com.fit2cloud");
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
