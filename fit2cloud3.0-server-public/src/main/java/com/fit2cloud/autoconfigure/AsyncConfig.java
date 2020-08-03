package com.fit2cloud.autoconfigure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Author: chunxing
 * Date: 2018/7/27  下午4:54
 * Description:
 */
@EnableAsync
@Configuration
public class AsyncConfig {

    @Bean
    public AsyncTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("Async-Executor");
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        return executor;
    }
}
