package com.fit2cloud.mc;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;


@SpringBootApplication(exclude = QuartzAutoConfiguration.class)
@EnableEurekaServer
@EnableDiscoveryClient
@EnableScheduling
@EnableCaching
@EnableAsync
public class Application {

    private final static String path = "/opt/fit2cloud/conf/fit2cloud.properties";

    private final static String[] env_keys = {
            "spring.cloud.config.uri",
            "spring.cloud.config.profile",
            "spring.cloud.config.label",
            "spring.cloud.config.name",
            "management.endpoints.web.exposure.include"
    };

    public static void main(String[] args) {
        try {
            Properties properties = readFile();
            Arrays.stream(env_keys).forEach(key -> setEnv(key, properties));
            SpringApplication.run(Application.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Properties readFile() throws Exception{
        Properties properties = new Properties();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
        properties.load(bufferedReader);
        return properties;
    }

    private static void setEnv(String key, Properties properties){
        if (properties.containsKey(key))
        System.setProperty(key, properties.getProperty(key));
    }
}
