package com.fit2cloud.mc;

import com.alibaba.fastjson.JSONObject;
import com.fit2cloud.commons.server.elastic.dao.SystemLogRepository;
import com.fit2cloud.commons.server.elastic.domain.SystemLog;
import com.fit2cloud.commons.utils.UUIDUtil;
import com.fit2cloud.mc.config.BeforeTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;
import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan("com.fit2cloud.mc.*")
public class ElasticTest extends BeforeTest {
    @Resource
    private ElasticsearchOperations elasticsearchOperations;
    /*@Resource(name = "restTemplate")
    private RestTemplate restTemplate;*/

    /*@Resource
    private EsTestRepository esTestRepository;*/


    @Resource
    private SystemLogRepository systemLogRepository;



    @Test
    public void test1() {
        System.out.println();
    }

    /*@Test
    public void test2() {
        String template = restTemplate.getForObject("https://www.baidu.com", String.class);
        System.out.println(template);
    }*/



    @Test
    public void es_save(){
        SystemLog systemLog = new SystemLog();
        systemLog.setHost("localhost");
        systemLog.setId("1");
        systemLog.setLevel("ERROR");
        systemLog.setLogTime(Timestamp.valueOf(LocalDateTime.now()).getTime());
        systemLog.setMessage("ERROR");
        systemLogRepository.save(systemLog);
    }

    @Test
    public void es_delete(){
        Criteria criteria = Criteria.where("id").is("1");
        CriteriaQuery query = new CriteriaQuery(criteria);
        elasticsearchOperations.delete(query, SystemLog.class, IndexCoordinates.of("fit2cloud-cmp-logs"));
    }

    @Test
    public void es_query(){
        systemLogRepository.findById("1").ifPresent(log -> System.out.println(JSONObject.toJSONString(log)));
    }

    public void es_update(){

    }
}
