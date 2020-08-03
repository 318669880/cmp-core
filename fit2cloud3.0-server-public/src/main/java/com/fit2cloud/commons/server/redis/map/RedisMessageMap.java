package com.fit2cloud.commons.server.redis.map;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class RedisMessageMap {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public void put(String key, Object value, long expires, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, expires, timeUnit);
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().getOperations().delete(key));
    }
}
