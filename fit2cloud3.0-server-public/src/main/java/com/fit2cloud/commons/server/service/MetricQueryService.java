package com.fit2cloud.commons.server.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fit2cloud.commons.server.constants.F2CMetricName;
import com.fit2cloud.commons.server.constants.MetricUnit;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.model.MetricData;
import com.fit2cloud.commons.server.model.MetricDataRequest;
import com.fit2cloud.commons.server.model.MetricRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(rollbackFor = Exception.class)
public class MetricQueryService {
    @Value("${prometheus.host:http://127.0.0.1:8080}")
    private String prometheusHost;
    @Resource(name = "remoteRestTemplate")
    private RestTemplate restTemplate;

    public List<MetricData> queryMetricData(MetricRequest metricRequest) {
        List<MetricData> metricDataList = new ArrayList<>();
        long endTime = metricRequest.getEndTime();
        long startTime = metricRequest.getStartTime();
        int step = metricRequest.getStep();

        Optional.ofNullable(metricRequest.getMetricDataQueries()).ifPresent(metricDataQueries -> metricDataQueries.forEach(query -> {
            String resourceId = query.getResourceId();
            String promQL = query.getPromQL();
            if (StringUtils.isEmpty(promQL)) {
                F2CMetricName f2CMetricName = query.getMetric();
                if (f2CMetricName == null) {
                    F2CException.throwException(String.format("metric name: %s can't be parsed", query.getMetric()));
                }

                promQL = String.format("%s{resourceId='%s'}", f2CMetricName.getName(), resourceId);
                Optional.ofNullable(queryApiMetric(resourceId, f2CMetricName, promQL, startTime, endTime)).ifPresent(metricDataList::add);
            } else {
                Optional.ofNullable(queryPrometheusMetric(resourceId, promQL, query, startTime, endTime, step)).ifPresent(metricDataList::add);
            }
        }));

        return metricDataList;
    }

    private MetricData queryPrometheusMetric(String resourceId, String promQL, MetricDataRequest query, long startTime, long endTime, int step) {
        F2CMetricName metric = query.getMetric();

        if (StringUtils.isEmpty(promQL)) {
            return null;
        }
        DecimalFormat df = new DecimalFormat("#.###");
        String start = df.format(startTime / 1000.0);
        String end = df.format(endTime / 1000.0);
        JSONObject response = restTemplate.getForObject(prometheusHost + "/api/v1/query_range?query={promQL}&start={start}&end={end}&step={step}", JSONObject.class, promQL.replace("${resourceId}", resourceId), start, end, step);
        return handleResult(resourceId, metric == null ? "" : metric.name(), metric == null ? MetricUnit.NONE : metric.getUnit(), response);
    }

    private MetricData queryApiMetric(String resourceId, F2CMetricName metric, String promQL, long startTime, long endTime) {
        if (endTime > System.currentTimeMillis()) {
            endTime = System.currentTimeMillis();
        }
        long period = (endTime - startTime) / (3600 * 1000);
        long now = System.currentTimeMillis();
        Double offset = Math.ceil((now - endTime) / (3600 * 1000.0));
        if (offset > 1) {
            promQL += String.format("[%sh] offset %sh", period, offset.intValue());
        } else {
            promQL += String.format("[%sh]", period);
        }
        JSONObject response = restTemplate.getForObject(prometheusHost + "/api/v1/query?query={query}", JSONObject.class, promQL);
        return handleResult(resourceId, metric.name(), metric.getUnit(), response);
    }

    private MetricData handleResult(String resourceId, String metric, MetricUnit unit, JSONObject response) {
        if (StringUtils.equals(response.getString("status"), "success")) {
            JSONObject data = response.getJSONObject("data");
            JSONArray result = data.getJSONArray("result");
            MetricData metricData = new MetricData();
            List<Long> timestamps = new ArrayList<>();
            List<Double> values = new ArrayList<>();

            result.forEach(rObject -> {
                JSONObject resultObject = JSONObject.parseObject(rObject.toString());
                JSONArray jsonArray = resultObject.getJSONArray("values");
                jsonArray.forEach(value -> {
                    JSONArray ja = JSONObject.parseArray(value.toString());
                    Double timestamp = ja.getDouble(0);
                    timestamps.add((long) (timestamp * 1000));
                    values.add(ja.getDouble(1));
                });
            });
            if (CollectionUtils.isEmpty(values)) {
                return null;
            }
            metricData.setValues(values);
            metricData.setTimestamps(timestamps);
            metricData.setMetric(metric);
            metricData.setResourceId(resourceId);
            metricData.setUnit(unit.getDescription());
            return metricData;
        }
        return null;
    }

}
