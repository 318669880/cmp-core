package com.fit2cloud.mc;

import com.fit2cloud.mc.config.BeforeTest;
import com.fit2cloud.mc.service.SysStatsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WorkspaceTest extends BeforeTest {

    @Resource
    private SysStatsService sysStatsService;


    @Test
    public void test() {
        sysStatsService.diskUsage("asdf", 23);
    }
}
