package com.fit2cloud.commons.server.elastic.dao;
import com.fit2cloud.commons.server.elastic.constants.EsConstants;
import com.fit2cloud.commons.server.elastic.domain.DatastoreMetric;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component
public class DatastoreMetricDao extends AbstractMetricDao<DatastoreMetric>{

    @Resource
    private DatastoreMetricRepository datastoreMetricRepository;

    @PostConstruct
    private void init() {
        super.indexName = EsConstants.IndexName.DATASTORE_METRIC.getIndexName();
        super.metricRepository = datastoreMetricRepository;
    }
}
