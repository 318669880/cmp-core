package com.fit2cloud.commons.server.elastic.dao;

import com.fit2cloud.commons.server.elastic.domain.HostMetric;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HostMetricRepository extends ElasticsearchRepository<HostMetric, String>{
}
