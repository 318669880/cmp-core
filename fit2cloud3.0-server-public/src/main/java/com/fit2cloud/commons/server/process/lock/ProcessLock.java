package com.fit2cloud.commons.server.process.lock;

import org.springframework.stereotype.Component;

@Component
public class ProcessLock extends MemoryLock {

    private static final Long EXPIRES = 60000L;

    @Override
    String getPrefix() {
        return "PROCESS_";
    }

    @Override
    Long getTimeOut() {
        return System.currentTimeMillis() + EXPIRES;
    }
}
