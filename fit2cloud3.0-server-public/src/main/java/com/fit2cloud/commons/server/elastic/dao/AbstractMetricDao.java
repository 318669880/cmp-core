package com.fit2cloud.commons.server.elastic.dao;

import com.alibaba.fastjson.JSON;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

public class AbstractMetricDao<T> {

    @Resource
    protected ElasticsearchTemplate elasticsearchTemplate;

    protected ElasticsearchRepository metricRepository;

    protected String indexName;

    /**
     * 批量保存监控数据
     * @param metrics
     */
    public void saveBanch(List<T> metrics) {

        List<IndexQuery> queries = new ArrayList();
        int buchSize = 0;
        for (T item : metrics) {
            IndexQuery indexQuery = new IndexQuery();
            indexQuery.setSource(JSON.toJSONString(item));
            indexQuery.setIndexName(indexName);
            indexQuery.setType("metric");
            queries.add(indexQuery);
            if (buchSize != 0 && buchSize % 1000 == 0) {
                elasticsearchTemplate.bulkIndex(queries);
                queries.clear();
            }
            buchSize++;
        }

        if (queries.size() > 0) {
            elasticsearchTemplate.bulkIndex(queries);
        }

    }

    public T save(T metric) {
        return (T) metricRepository.save(metric);
    }

    public void delete(T serverMetric) {
        metricRepository.delete(serverMetric);
    }

    public List<T> getAll() {
        List<T> list = new ArrayList<>();
        metricRepository.findAll().forEach(e -> list.add((T) e));
        return list;
    }

}
