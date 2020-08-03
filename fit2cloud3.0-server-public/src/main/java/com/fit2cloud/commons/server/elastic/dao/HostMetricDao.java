package com.fit2cloud.commons.server.elastic.dao;
import com.fit2cloud.commons.server.elastic.constants.EsConstants;
import com.fit2cloud.commons.server.elastic.domain.HostMetric;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class HostMetricDao extends AbstractMetricDao<HostMetric>{

    @Resource
    private HostMetricRepository hostMetricRepository;

    public HostMetricDao(){
        super.indexName = EsConstants.IndexName.HOST_METRIC.getIndexName();
        super.metricRepository = hostMetricRepository;
    }

}
