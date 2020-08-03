package com.fit2cloud.commons.server.elastic.dao;
import com.alibaba.fastjson.JSON;
import com.fit2cloud.commons.server.elastic.constants.EsConstants;
import com.fit2cloud.commons.server.elastic.domain.ClusterMetric;
import com.fit2cloud.commons.server.elastic.domain.DatastoreMetric;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class ClusterMetricDao extends AbstractMetricDao<ClusterMetric>{

    @Resource
    private ClusterMetricRepository clusterMetricRepository;

    @PostConstruct
    private void init() {
        super.indexName = EsConstants.IndexName.CLUSTER_METRIC.getIndexName();
        super.metricRepository = clusterMetricRepository;
    }

}
