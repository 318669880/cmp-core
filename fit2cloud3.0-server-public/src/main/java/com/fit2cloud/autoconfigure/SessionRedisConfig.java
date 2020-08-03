package com.fit2cloud.autoconfigure;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.Resource;

/**
 * Author: chunxing
 * Date: 2018/5/28  上午11:22
 * Description:
 */
@Configuration
public class SessionRedisConfig {

    @Resource
    private Environment environment;

    @Bean("lettuceConnectionFactory")
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(environment.getProperty("redis.hostname", "localhost"));
        String redisPassword = environment.getProperty("redis.password");
        if (StringUtils.isNotBlank(redisPassword)) {
            configuration.setPassword(RedisPassword.of(redisPassword));
        }
        configuration.setPort(environment.getProperty("redis.port", Integer.class, 6379));
        configuration.setDatabase(environment.getProperty("redis.database", Integer.class, 0));
        return new LettuceConnectionFactory(configuration);
    }

    @Bean("sessionRedisTemplate")
    public RedisTemplate<String, Object> sessionRedisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        return redisTemplate(lettuceConnectionFactory);
    }

    @Bean("redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(lettuceConnectionFactory);
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisMessageListenerContainer redisContainer() {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory());
        return container;
    }
}
