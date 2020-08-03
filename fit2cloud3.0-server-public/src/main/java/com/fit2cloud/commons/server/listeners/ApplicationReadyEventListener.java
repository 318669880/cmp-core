package com.fit2cloud.commons.server.listeners;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

/**
 * 服务启动完成后打印日志
 */
public class ApplicationReadyEventListener implements ApplicationListener<ApplicationReadyEvent> {

    private static boolean started = false;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        if (applicationReadyEvent.getApplicationContext().getParent() != null && !started) {
            System.out.println("-------------- Application Start Done --------------");
            started = true;
        }
    }
}
