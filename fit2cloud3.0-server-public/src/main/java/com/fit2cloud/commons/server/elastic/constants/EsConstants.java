package com.fit2cloud.commons.server.elastic.constants;

public class EsConstants {

    public enum IndexName{
        SERVER_METRIC("metric_server"),
        HOST_METRIC("metric_host"),
        CLUSTER_METRIC("metric_cluster"),
        DATASTORE_METRIC("metric_datastore");

        private final String indexName;

        private IndexName(String indexName){
            this.indexName = indexName;
        }

        public String getIndexName(){
            return indexName;
        }
    }

}
