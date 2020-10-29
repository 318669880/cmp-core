package com.fit2cloud.commons.server;

import com.fit2cloud.commons.server.service.WechatService;
import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @Author gin
 * @Date 2020/10/29 12:23 下午
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WechatTest {
    @Resource
    private WechatService wechatService;
    @Resource
    private RestTemplate restTemplate;

    @Test
    public void token() {
        String accessToken = wechatService.getAccessToken();
    }

    @Test
    public void sendText() {
        wechatService.sendTextMessageToUser("文本测试...", "junjie.xia");
    }

    @Test
    public void getPart() {
        String accessToken = wechatService.getAccessToken();
//        ResponseEntity<String> responseEntity = restTemplate.getForEntity("https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=" + accessToken, String.class);
//        ResponseEntity<String> responseEntity = restTemplate.getForEntity("https://qyapi.weixin.qq.com/cgi-bin/tag/list?access_token=" + accessToken, String.class);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("https://qyapi.weixin.qq.com/cgi-bin/tag/get?access_token=" + accessToken+"&tagid=10", String.class);
        System.out.println(new Gson().toJson(responseEntity));
    }
}
