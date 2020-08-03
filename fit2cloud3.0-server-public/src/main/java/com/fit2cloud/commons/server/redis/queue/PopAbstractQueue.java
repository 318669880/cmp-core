package com.fit2cloud.commons.server.redis.queue;

import com.fit2cloud.commons.utils.LogUtil;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

public abstract class PopAbstractQueue<T> implements PopQueue<T> {
    protected String queue;
    protected int interval = 15;
    protected boolean isAlive = true;
    @Resource
    protected RedisTemplate<String, Object> redisTemplate;

    public PopAbstractQueue(String queue, int interval) {
        this.queue = queue;
        this.interval = interval;
    }

    public T pop(String queue) {
        try {
            return (T) redisTemplate.opsForList().rightPop(queue);
        } catch (Throwable t) {
            LogUtil.error(t);
            return null;
        }
    }

    public abstract void handleMessage(T t);

    @PostConstruct
    public void onMessage() {
        Thread thread = new Thread(() -> {
            while (isAlive) {
                try {
                    T pop = pop(queue);
                    if (pop == null) {
                        Thread.sleep(interval * 1000L);
                        continue;
                    }
                    handleMessage(pop);
                } catch (Throwable t) {
                    LogUtil.error(t);
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    @PreDestroy
    void preDestroy() {
        isAlive = false;
    }
}
