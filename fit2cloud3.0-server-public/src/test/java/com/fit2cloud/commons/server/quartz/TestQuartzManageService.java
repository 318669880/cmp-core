package com.fit2cloud.commons.server.quartz;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestQuartzManageService {

    @Resource
    private CommonJob commonJob;

    @Test
    public void test2() throws Exception {

    }
}
