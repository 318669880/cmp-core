package com.fit2cloud.commons.server.redis.queue;

public abstract class AbstractQueue<T> extends PopAbstractQueue<T> implements PushQueue {
    public AbstractQueue(String queue, int interval) {
        super(queue, interval);
    }

    @Override
    public void push(Object message) {
        redisTemplate.opsForList().leftPush(queue, message);
    }
}
