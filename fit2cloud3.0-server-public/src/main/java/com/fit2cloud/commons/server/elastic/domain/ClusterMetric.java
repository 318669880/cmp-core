package com.fit2cloud.commons.server.elastic.domain;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "metric_cluster", type = "metric")
public class ClusterMetric extends BeaseMetric {

    @Field(type = FieldType.Double)
    private double cpuUsage;
    @Field(type = FieldType.Double)
    private double memoryUsage;

    public double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public double getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(double memoryUsage) {
        this.memoryUsage = memoryUsage;
    }
}
