package com.fit2cloud.commons.server;

import com.alibaba.fastjson.JSON;
import com.fit2cloud.commons.server.constants.F2CMetricName;
import com.fit2cloud.commons.server.model.MetricData;
import com.fit2cloud.commons.server.model.MetricDataRequest;
import com.fit2cloud.commons.server.model.MetricRequest;
import com.fit2cloud.commons.server.service.MetricQueryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MetricQueryTest {
    @Resource
    private MetricQueryService metricQueryService;

    @Test
    public void testQueryMetricServer() {
        long endTime = System.currentTimeMillis();
        MetricRequest request = new MetricRequest();
        request.setStartTime(endTime - 240 * 3600 * 1000L);
        request.setEndTime(endTime);
        List<MetricDataRequest> metricQuerys = new ArrayList<MetricDataRequest>() {{
            add(new MetricDataRequest() {{
                setResourceId("0e720f92-a2fe-4f97-a4ce-d71f24472925");
                setMetric(F2CMetricName.SERVER_CPU_USAGE);
            }});
        }};
        request.setMetricDataQueries(metricQuerys);
        List<MetricData> metricData = metricQueryService.queryMetricData(request);
        System.out.println(JSON.toJSON(metricData));
    }

    @Test
    public void testQueryPrometheusMetricServer() {
        long endTime = System.currentTimeMillis();
        MetricRequest request = new MetricRequest();
        request.setStartTime(endTime - 240 * 3600 * 1000L);
        request.setEndTime(endTime);
        List<MetricDataRequest> metricQuerys = new ArrayList<MetricDataRequest>() {{
            add(new MetricDataRequest() {{
                setResourceId("192.168.1.108:9100");
                setMetric(F2CMetricName.SERVER_CPU_USAGE);
            }});
        }};
        request.setMetricDataQueries(metricQuerys);
        List<MetricData> metricData = metricQueryService.queryMetricData(request);
        System.out.println(JSON.toJSON(metricData));
    }

    @Test
    public void testQueryMetricHost() {
        long endTime = System.currentTimeMillis();
        MetricRequest request = new MetricRequest();
        request.setStartTime(endTime - 240 * 3600 * 1000L);
        request.setEndTime(endTime);
        List<MetricDataRequest> metricQuerys = new ArrayList<MetricDataRequest>() {{
            add(new MetricDataRequest() {{
                setResourceId("785c6e78-50a9-4c45-84bd-731b84554e2e");
            }});
        }};
        request.setMetricDataQueries(metricQuerys);
        List<MetricData> metricData = metricQueryService.queryMetricData(request);
        System.out.println(JSON.toJSON(metricData));
    }

    @Test
    public void testQueryMetricDatastore() {
        long endTime = System.currentTimeMillis();
        MetricRequest request = new MetricRequest();
        request.setStartTime(endTime - 240 * 3600 * 1000L);
        request.setEndTime(endTime);
        List<MetricDataRequest> metricQuerys = new ArrayList<MetricDataRequest>() {{
            add(new MetricDataRequest() {{
                setResourceId("c9bdf621-8530-488d-92a5-b7d02e653089");
            }});
        }};
        request.setMetricDataQueries(metricQuerys);
        List<MetricData> metricData = metricQueryService.queryMetricData(request);
        System.out.println(JSON.toJSON(metricData));
    }

    @Test
    public void testQueryMetricDisk() {
        long endTime = System.currentTimeMillis();
        MetricRequest request = new MetricRequest();
        request.setStartTime(endTime - 240 * 3600 * 1000L);
        request.setEndTime(endTime);
        List<MetricDataRequest> metricQuerys = new ArrayList<MetricDataRequest>() {{
            add(new MetricDataRequest() {{
                setResourceId("1beaf148-019e-456f-8528-a637380a6d7f");
            }});
        }};
        request.setMetricDataQueries(metricQuerys);
        List<MetricData> metricData = metricQueryService.queryMetricData(request);
        System.out.println(JSON.toJSON(metricData));
    }

    @Test
    public void testSysQueryMetricCPU() {
        long endTime = System.currentTimeMillis();
        MetricRequest request = new MetricRequest();
        request.setStartTime(endTime -  3600 * 1000L);
        request.setEndTime(endTime);
        request.setStep(60);
        List<MetricDataRequest> metricQuerys = new ArrayList<MetricDataRequest>() {{
            add(new MetricDataRequest() {{
                setResourceId("rdtest2-node1");
                setMetric(F2CMetricName.SYS_CPU_USAGE);
            }});
        }};
        request.setMetricDataQueries(metricQuerys);
        List<MetricData> metricData = metricQueryService.queryMetricData(request);
        System.out.println(JSON.toJSON(metricData));
    }

}
