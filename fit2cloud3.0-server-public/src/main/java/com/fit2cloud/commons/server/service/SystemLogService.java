package com.fit2cloud.commons.server.service;

import com.fit2cloud.commons.server.elastic.dao.SystemLogRepository;
import com.fit2cloud.commons.server.elastic.domain.SystemLog;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.commons.utils.Pager;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.*;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SystemLogService {
    @Resource
    private SystemLogRepository systemLogRepository;
    @Resource
    private RestHighLevelClient restHighLevelClient;



    public Pager<List<SystemLog>> querySystemLog(int goPage, int pageSize, Map<String, Object> params) {
        List<SystemLog> results = new ArrayList<>();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (StringUtils.isNotEmpty((String) params.get("module"))) {
            boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("module", params.get("module")));
        }
        if (StringUtils.isNotEmpty((String) params.get("level"))) {
            boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("level", params.get("level")));
        }
        if (StringUtils.isNotEmpty((String) params.get("message"))) {
            boolQueryBuilder.must(QueryBuilders.boolQuery()
                    .should(QueryBuilders.fuzzyQuery("message", params.get("message")))
                    .should(QueryBuilders.regexpQuery("message", ".*" + params.get("message") + ".*"))
                    .should(QueryBuilders.matchPhrasePrefixQuery("message", params.get("message"))));
        }
        if ((Long) params.getOrDefault("logTimeStart", 0L) > 0) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("logTime").gte(params.get("logTimeStart")));
        }
        if ((Long) params.getOrDefault("logTimeEnd", 0L) > 0) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("logTime").lte(params.get("logTimeEnd")));
        }
        goPage = goPage - 1 <= 0 ? 0 : goPage - 1;
        Page<SystemLog> search = systemLogRepository.search(boolQueryBuilder, PageRequest.of(goPage, pageSize, Sort.by(Sort.Order.desc("logTime"))));
        search.forEach(results::add);
        return new Pager<>(results, search.getTotalElements(), search.getTotalPages());
    }


    public void cleanHistoryLog(Long logTime) {
        String indexName = "fit2cloud-cmp-logs";
        RangeQueryBuilder queryBuilder = QueryBuilders.rangeQuery("logTime").lte(logTime);
        DeleteByQueryRequest request = new DeleteByQueryRequest(indexName);
        request.setConflicts("proceed");
        request.setQuery(queryBuilder);
        request.setScroll(TimeValue.timeValueMinutes(10));
        request.setTimeout(TimeValue.timeValueMinutes(10));
        request.setRefresh(true);
        try{
            BulkByScrollResponse response = restHighLevelClient.deleteByQuery(request, RequestOptions.DEFAULT);
            LogUtil.info("Total delete：{} system logs", response.getStatus().getDeleted());
        }catch (Exception e){
            LogUtil.error("Failed delete system logs, {}", e.getMessage());
        }
    }

}
