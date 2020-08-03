package com.fit2cloud.mc;

import com.fit2cloud.mc.service.AccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountTest {
    @Resource
    private AccountService accountService;

    @Test
    public void testSyncAccount() throws InterruptedException {
        accountService.sync(Collections.singletonList("e4022807-97a2-4efa-b5fb-b2064fd1a485"));
        TimeUnit.SECONDS.sleep(300);
    }
}
