package com.fit2cloud.mc;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;


@SpringBootApplication(exclude = QuartzAutoConfiguration.class)
@EnableEurekaServer
@EnableDiscoveryClient
@EnableScheduling
@EnableCaching
@EnableAsync
public class Application {

    private final static String public_path = "/opt/fit2cloud/conf/fit2cloud.properties";

    private final static String uri = "spring.cloud.config.uri";
    private final static String profile = "spring.cloud.config.profile";
    private final static String label = "spring.cloud.config.label";
    private final static String name = "spring.cloud.config.name";
    private final static String include = "management.endpoints.web.exposure.include";



    public static void main(String[] args) {
        try {
            Properties public_properties = readFile(public_path);
            Properties private_properties = readFile();
            String ip = localIp();
            String port = private_properties.getProperty("server.port");
            String profileValue = ip + ":" + port;
            setEnv(uri, public_properties);
            setEnv(profile, profileValue);
            setEnv(label, "master");
            setEnv(name, "register-center");
            setEnv(include, "*");
            SpringApplication.run(Application.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Properties readFile(String path) throws Exception{
        Properties properties = new Properties();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
        properties.load(bufferedReader);
        return properties;
    }

    private static void setEnv(String key, Properties properties){
        if (properties.containsKey(key))
        System.setProperty(key, properties.getProperty(key));
    }
    private static void setEnv(String key, String value){
        System.setProperty(key, value);
    }


    private static String localIp() throws UnknownHostException {
        InetAddress address = InetAddress.getLocalHost();
        String hostAddress = address.getHostAddress();
        return hostAddress;
    }

    private static Properties readFile() throws Exception{
        InputStream resourceAsStream = Application.class.getResourceAsStream("/application.properties");
        Properties properties = new Properties();
        properties.load(resourceAsStream);
        return properties;
    }
}
