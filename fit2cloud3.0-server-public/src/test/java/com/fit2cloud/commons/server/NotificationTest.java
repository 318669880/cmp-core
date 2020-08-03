package com.fit2cloud.commons.server;

import com.fit2cloud.commons.server.config.BeforeTest;
import com.fit2cloud.commons.server.service.NotificationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NotificationTest extends BeforeTest {

    @Resource
    private NotificationService notificationService;

    @Test
    public void testAdd() {
        List<String> receivers = new ArrayList<>();
        receivers.add("kun1@fit2cloud.com");
        receivers.add("kun2@fit2cloud.com");
        receivers.add("kun3@fit2cloud.com");
        receivers.add("kun4@fit2cloud.com");
        receivers.forEach(receiver -> notificationService.sendAnnouncement("test title", "test content", receiver));
    }

}