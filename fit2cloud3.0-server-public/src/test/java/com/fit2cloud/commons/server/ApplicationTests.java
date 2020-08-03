package com.fit2cloud.commons.server;

import com.alibaba.fastjson.JSON;
import com.fit2cloud.commons.server.config.BeforeTest;
import com.fit2cloud.commons.server.constants.F2CMetricName;
import com.fit2cloud.commons.server.model.MetricDataRequest;
import com.fit2cloud.commons.server.model.MetricRequest;
import com.fit2cloud.commons.utils.ResultHolder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTests extends BeforeTest {

    @LocalServerPort
    private int port;
    @Resource
    private TestRestTemplate restTemplate;
    private String baseUrl;

    @Before
    public void before() {
        this.baseUrl = "http://localhost:" + port;
    }

    @Test
    public void contextLoads() {
    }

    @Test
    public void testController() {
        String forObject = restTemplate.getForObject(baseUrl + "/user/list/1/2", String.class);
        System.out.println(forObject);
    }

    @Test
    public void testMetricQueryController() {
        MetricRequest request = new MetricRequest();
        long endTime = System.currentTimeMillis();
        request.setStartTime(endTime - 240 * 3600 * 1000L);
        request.setEndTime(endTime);
        List<MetricDataRequest> metricQuerys = new ArrayList<MetricDataRequest>() {{
            add(new MetricDataRequest() {{
                setResourceId("3257de59-d812-47b7-a53d-b1dbffe242cd");
                setMetric(F2CMetricName.SERVER_CPU_USAGE);
            }});
            add(new MetricDataRequest() {{
                setResourceId("6d8f69b3-0355-4276-a624-4f57af9d0d85");
                setMetric(F2CMetricName.SERVER_MEMORY_USAGE);
            }});
        }};
        request.setMetricDataQueries(metricQuerys);
        ResultHolder response = restTemplate.postForObject(baseUrl + "/server/metric/query", request, ResultHolder.class);
        System.out.println(JSON.toJSON(response));
    }


}
