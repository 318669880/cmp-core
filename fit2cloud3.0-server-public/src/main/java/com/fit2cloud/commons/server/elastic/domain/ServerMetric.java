package com.fit2cloud.commons.server.elastic.domain;


import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "metric_server", type = "metric")
public class ServerMetric extends BeaseMetric {

    @Field(type = FieldType.Double)
    private double cpuUsage;
    @Field(type = FieldType.Double)
    private double memoryUsage;
    @Field(type = FieldType.Double)
    private int netIoKbps;
    @Field(type = FieldType.Double)
    private int diskIoKbps;

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

    public int getNetIoKbps() {
        return netIoKbps;
    }

    public void setNetIoKbps(int netIoKbps) {
        this.netIoKbps = netIoKbps;
    }

    public int getDiskIoKbps() {
        return diskIoKbps;
    }

    public void setDiskIoKbps(int diskIoKbps) {
        this.diskIoKbps = diskIoKbps;
    }
}