package com.fit2cloud.commons.server.redis.queue;

public interface PushQueue {
    void push(Object message);
}
