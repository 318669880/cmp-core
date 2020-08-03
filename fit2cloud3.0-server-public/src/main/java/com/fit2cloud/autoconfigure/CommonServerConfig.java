package com.fit2cloud.autoconfigure;

import com.fit2cloud.commons.server.module.ServerInfo;
import com.fit2cloud.commons.utils.CommonThreadPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@ComponentScan(value = "com.fit2cloud.commons.server")
@EnableElasticsearchRepositories(basePackages = "com.fit2cloud.commons.server.elastic.dao")
@PropertySource("classpath:common.properties")
public class CommonServerConfig {

    @Bean
    public ServerInfo serverInfo() {
        return new ServerInfo();
    }

    @Bean(destroyMethod = "shutdown")
    public CommonThreadPool processThreadPool() {
        CommonThreadPool commonThreadPool = new CommonThreadPool();
        commonThreadPool.setCorePoolSize(20);
        commonThreadPool.setKeepAliveSeconds(3600);
        return commonThreadPool;
    }

    @Bean(destroyMethod = "shutdown")
    public CommonThreadPool commonThreadPool() {
        CommonThreadPool commonThreadPool = new CommonThreadPool();
        commonThreadPool.setCorePoolSize(20);
        commonThreadPool.setKeepAliveSeconds(3600);
        return commonThreadPool;
    }

    @Bean(destroyMethod = "shutdown")
    public CommonThreadPool pushGatewayThreadPool() {
        CommonThreadPool commonThreadPool = new CommonThreadPool();
        commonThreadPool.setCorePoolSize(30);
        commonThreadPool.setKeepAliveSeconds(3600);
        return commonThreadPool;
    }

}
