package com.fit2cloud.mc;

import com.fit2cloud.mc.config.BeforeTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ElasticTest extends BeforeTest {
    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;
    @Resource(name = "restTemplate")
    private RestTemplate restTemplate;

    @Test
    public void test1() {
        System.out.println(elasticsearchTemplate);
    }

    @Test
    public void test2() {
        String template = restTemplate.getForObject("https://www.baidu.com", String.class);
        System.out.println(template);
    }
}
