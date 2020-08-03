package com.fit2cloud.commons.server;

import com.fit2cloud.commons.server.service.MailService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * Author: chunxing
 * Date: 2018/7/27  下午4:15
 * Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MailTest {

    @Resource
    private MailService mailService;

    @Test
    public void send() {
        mailService.sendSimpleEmail("XXL", "XXX", "chunxing@fit2cloud.com");
    }
}
