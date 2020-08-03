package com.fit2cloud.commons.server.redis.queue;

public interface PopQueue<T> {
    T pop(String queue);
}
