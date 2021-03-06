package com.fit2cloud.commons.server.elastic;


import com.alibaba.fastjson.JSON;
import com.fit2cloud.commons.server.elastic.dao.SystemLogRepository;
import com.fit2cloud.commons.server.elastic.domain.SystemLog;
import com.fit2cloud.commons.server.service.SystemLogService;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.commons.utils.Pager;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ElasticsearchTest {
    @BeforeClass
    public static void setSystemProperties() {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

    @Resource
    private ElasticsearchRestTemplate elasticsearchTemplate;
    //    @Resource
//    private CloudServerMetricRepository cloudServerMetricRepository;
    @Resource
    private SystemLogRepository systemLogRepository;
    @Resource
    private SystemLogService systemLogService;

//    @Test
//    public void test1() {
//        CloudServerMetric metric = new CloudServerMetric();
//        metric.setResourceId("test_123");
//        metric.setMetricName("memoryusage");
//        metric.setAverageValue(0.0);
//        metric.setMaxValue(0.0);
//        metric.setMinValue(0.0);
//        MetricUtils.fillSyncDate(System.currentTimeMillis(), metric);
//        cloudServerMetricRepository.save(metric);
//    }

    @Test
    public void test2() {
        LogUtil.info("??????????????????");
    }

    @Test
    public void test3() {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchPhraseQuery("module", "management-center"));
        Page<SystemLog> search = systemLogRepository.search(boolQueryBuilder, PageRequest.of(1, 10));
        search.forEach(item -> {
            System.out.println(item.getModule() + " : " + item.getLogTime());
        });
    }

    @Test
    public void test4() {
        Map<String, Object> params = new HashMap<>();
//        params.put("message", "???");
//        params.put("message", "??????");
//        params.put("message", "?????????");
//        params.put("message", "???");
        params.put("message", "????????????");
        Pager pager = systemLogService.querySystemLog(1, 20, params);
        Object listObject = pager.getListObject();
        System.out.println(JSON.toJSON(listObject));
    }


    @Test
    public void test6() {
        systemLogRepository.findAll().forEach(item -> {
            System.out.println(JSON.toJSONString(item));
        });
    }

    @Test
    public void test7() {
        // .lte(1530513236984L);
        RangeQueryBuilder queryBuilder = QueryBuilders.rangeQuery("logTime").lte(1531822248000L);

        DeleteQuery deleteQuery = new DeleteQuery();
        deleteQuery.setQuery(queryBuilder);
        //elasticsearchTemplate.delete(deleteQuery, SystemLog.class);
        //elasticsearchTemplate.delete(deleteQuery, SystemLog.class, IndexCoordinates.of(""));
    }

    @Test
    public void test8() throws Exception {
        long time = 1604994181000L;

        systemLogService.cleanHistoryLog(time);
    }

}
