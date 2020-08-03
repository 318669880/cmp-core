package com.fit2cloud.commons.server.process.lock;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class MemoryLock {

    private Map<String, Long> cache = new ConcurrentHashMap<>();

    abstract String getPrefix();

    abstract Long getTimeOut();

    public synchronized boolean lock(String key) {
        if (isLock(key)) {
            return false;
        }
        cache.put(getPrefix() + key, getTimeOut());
        return true;
    }

    public void unlock(String key) {
        cache.remove(getPrefix() + key);
    }

    private boolean isLock(String key) {
        Long expires = cache.get(getPrefix() + key);
        if (expires == null) {
            return false;
        }
        if (expires > System.currentTimeMillis()) {
            return true;
        } else {
            unlock(key);
            return false;
        }
    }
}
