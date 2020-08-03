package com.fit2cloud.commons.server.redis.subscribe;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RedisMessagePublisher {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public void publish(String topic, final Object message) {
        redisTemplate.convertAndSend(topic, message);
    }
}
