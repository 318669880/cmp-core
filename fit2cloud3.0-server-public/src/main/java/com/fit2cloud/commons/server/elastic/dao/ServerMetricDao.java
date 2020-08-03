package com.fit2cloud.commons.server.elastic.dao;

import com.fit2cloud.commons.server.elastic.constants.EsConstants;
import com.fit2cloud.commons.server.elastic.domain.ServerMetric;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component
public class ServerMetricDao extends AbstractMetricDao<ServerMetric> {

    @Resource
    private ServerMetricRepository serverMetricRepository;

    @PostConstruct
    private void init() {
        super.indexName = EsConstants.IndexName.SERVER_METRIC.getIndexName();
        super.metricRepository = serverMetricRepository;
    }

}
