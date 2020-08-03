package com.fit2cloud.commons.server.redis.queue;

import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

public abstract class PushAbstractQueue implements PushQueue {
    protected String queue;
    @Resource
    protected RedisTemplate<String, Object> redisTemplate;

    public PushAbstractQueue(String queue) {
        this.queue = queue;
    }

    @Override
    public void push(Object message) {
        redisTemplate.opsForList().leftPush(queue, message);
    }
}
