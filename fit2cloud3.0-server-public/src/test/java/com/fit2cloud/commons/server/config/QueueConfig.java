package com.fit2cloud.commons.server.config;

import com.fit2cloud.commons.server.redis.queue.CloudAccountQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfig {
    @Bean
    public CloudAccountQueue cloudAccountQueue() {
        return new CloudAccountQueue("CLOUD_ACCOUNT", 2);
    }
}
