package com.fit2cloud.commons.server.redis;

import com.fit2cloud.commons.server.constants.RedisConstants;
import com.fit2cloud.commons.server.model.CloudAccountDTO;
import com.fit2cloud.commons.server.redis.map.RedisMessageMap;
import com.fit2cloud.commons.server.redis.queue.CloudAccountQueue;
import com.fit2cloud.commons.server.redis.subscribe.RedisMessagePublisher;
import com.fit2cloud.commons.server.service.CloudAccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {

    @Resource
    private CloudAccountService cloudAccountService;
    @Resource
    private RedisMessagePublisher redisMessagePublisher;
    @Resource
    private CloudAccountQueue cloudAccountQueue;
    @Resource
    private RedisMessageMap redisMessageMap;

    @Test
    public void test2() {

        List<CloudAccountDTO> accountList = cloudAccountService.getAccountList(null);

        accountList.forEach(account -> {
            redisMessagePublisher.publish(RedisConstants.Topic.CLOUD_ACCOUNT, account);
        });
    }

    @Test
    public void test3() throws Exception {
        List<CloudAccountDTO> accountList = cloudAccountService.getAccountList(null);
        accountList.forEach(account -> {
            cloudAccountQueue.push(account);
        });

        Thread.sleep(1000 * 1000L);
    }

    @Test
    public void test4() throws Exception {
        String key = "message";
        redisMessageMap.put(key, "hello world", 10, TimeUnit.SECONDS);
        Object message = redisMessageMap.get(key);
        System.out.println(message);
        Thread.sleep(10 * 1000L);
        message = redisMessageMap.get(key);
        System.out.println(message);
        redisMessageMap.put(key, "hello world", 10, TimeUnit.SECONDS);
        message = redisMessageMap.get(key);
        System.out.println(message);
        redisMessageMap.delete(key);
        message = redisMessageMap.get(key);
        System.out.println(message);
    }
}
