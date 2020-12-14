package com.fit2cloud.gateway;

import com.fit2cloud.autoconfigure.AsyncConfig;
import com.fit2cloud.autoconfigure.MetricConfig;
import com.fit2cloud.autoconfigure.PluginManagerAutoConfiguration;
import com.fit2cloud.gateway.internal.YamlFileApplicationContextInitializer;
import org.keycloak.adapters.springboot.KeycloakAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableZuulProxy
@EnableSwagger2
@EnableDiscoveryClient
@SpringBootApplication(exclude = {KeycloakAutoConfiguration.class,
        QuartzAutoConfiguration.class,
        PluginManagerAutoConfiguration.class,
        MetricConfig.class,
        AsyncConfig.class})
@EnableConfigServer
public class Application {


    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class)
                .initializers(new YamlFileApplicationContextInitializer())
                .run(args);
    }

}
