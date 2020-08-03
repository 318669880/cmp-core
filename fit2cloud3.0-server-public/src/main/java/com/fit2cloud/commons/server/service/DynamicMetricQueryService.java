package com.fit2cloud.commons.server.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fit2cloud.commons.server.exception.F2CException;
import com.fit2cloud.commons.server.model.DynamicMetricData;
import com.fit2cloud.commons.server.model.DynamicMetricRequest;
import com.fit2cloud.commons.server.model.MetricQueryType;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class DynamicMetricQueryService {
    @Value("${prometheus.host:http://127.0.0.1:8080}")
    private String prometheusHost;
    @Resource(name = "remoteRestTemplate")
    private RestTemplate restTemplate;

    public List<DynamicMetricData> queryMetricData(DynamicMetricRequest dynamicMetricRequest) {
        List<DynamicMetricData> metricDataList = new ArrayList<>();
        long endTime = dynamicMetricRequest.getEndTime();
        long startTime = dynamicMetricRequest.getStartTime();
        int step = dynamicMetricRequest.getStep();
        long reliableEndTime;
        if (endTime > System.currentTimeMillis()) {
            reliableEndTime = System.currentTimeMillis();
        } else {
            reliableEndTime = endTime;
        }

        Optional.ofNullable(dynamicMetricRequest.getMetricDataQueries()).ifPresent(metricDataQueries -> metricDataQueries.forEach(query -> {
            String promQL = query.getPromQL();
            if (StringUtils.isEmpty(promQL)) {
                F2CException.throwException("promQL is null");
            } else {
                if (MetricQueryType.RANGE.equals(query.getMetricQueryType())) {
                    Optional.ofNullable(queryPrometheusMetric(promQL, query.getSeriesName(), startTime, reliableEndTime, step)).ifPresent(metricDataList::addAll);
                } else {
                    Optional.ofNullable(queryApiMetric(promQL, query.getSeriesName(), startTime, reliableEndTime)).ifPresent(metricDataList::addAll);
                }
            }
        }));

        return metricDataList;
    }

    private List<DynamicMetricData> queryApiMetric(String promQL, String seriesName, long startTime, long endTime) {
        long period = (endTime - startTime) / (3600 * 1000);
        long now = System.currentTimeMillis();
        double offset = Math.ceil((now - endTime) / (3600 * 1000.0));
        if (offset > 1) {
            promQL += String.format("[%sh] offset %sh", period, (int) offset);
        } else {
            promQL += String.format("[%sh]", period);
        }
        JSONObject response = restTemplate.getForObject(prometheusHost + "/api/v1/query?query={query}", JSONObject.class, promQL);
        return handleResult(seriesName, response);
    }

    private List<DynamicMetricData> queryPrometheusMetric(String promQL, String seriesName, long startTime, long endTime, int step) {
        DecimalFormat df = new DecimalFormat("#.###");
        String start = df.format(startTime / 1000.0);
        String end = df.format(endTime / 1000.0);
        JSONObject response = restTemplate.getForObject(prometheusHost + "/api/v1/query_range?query={promQL}&start={start}&end={end}&step={step}", JSONObject.class, promQL, start, end, step);
        return handleResult(seriesName, response);
    }

    private List<DynamicMetricData> handleResult(String seriesName, JSONObject response) {
        List<DynamicMetricData> dynamicMetricData = new ArrayList<>();

        Map<String, Set<String>> labelMap = new HashMap<>();

        if (response != null && StringUtils.equals(response.getString("status"), "success")) {
            JSONObject data = response.getJSONObject("data");
            JSONArray result = data.getJSONArray("result");

            if (result.size() > 1) {
                result.forEach(rObject -> {
                    JSONObject resultObject = JSONObject.parseObject(rObject.toString());
                    JSONObject metrics = resultObject.getJSONObject("metric");

                    if (metrics != null && metrics.size() > 0) {
                        for (Map.Entry<String, Object> entry : metrics.entrySet())
                            labelMap.computeIfAbsent(entry.getKey(), k -> new HashSet<>()).add(entry.getValue().toString());
                    }
                });
            }

            Optional<String> uniqueLabelKey = labelMap.entrySet().stream().filter(entry -> entry.getValue().size() == result.size()).map(Map.Entry::getKey).findFirst();

            result.forEach(rObject -> {
                DynamicMetricData metricData = new DynamicMetricData();
                List<Long> timestamps = new ArrayList<>();
                List<Double> values = new ArrayList<>();

                JSONObject resultObject = JSONObject.parseObject(rObject.toString());
                JSONObject metrics = resultObject.getJSONObject("metric");
                JSONArray jsonArray = resultObject.getJSONArray("values");
                jsonArray.forEach(value -> {
                    JSONArray ja = JSONObject.parseArray(value.toString());
                    Double timestamp = ja.getDouble(0);
                    timestamps.add((long) (timestamp * 1000));
                    values.add(ja.getDouble(1));
                });

                if (CollectionUtils.isNotEmpty(values)) {
                    metricData.setValues(values);
                    metricData.setTimestamps(timestamps);
                    metricData.setSeriesName(seriesName);
                    uniqueLabelKey.ifPresent(s -> metricData.setUniqueLabel(metrics.getString(s)));
                    dynamicMetricData.add(metricData);
                }
            });


        }

        return dynamicMetricData;
    }

}
