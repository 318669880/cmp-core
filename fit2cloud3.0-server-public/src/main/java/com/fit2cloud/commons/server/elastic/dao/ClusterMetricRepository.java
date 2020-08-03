package com.fit2cloud.commons.server.elastic.dao;

import com.fit2cloud.commons.server.elastic.domain.ClusterMetric;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClusterMetricRepository extends ElasticsearchRepository<ClusterMetric,String> {
}
