package com.fit2cloud.commons.server.elastic.domain;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "metric_host", type = "metric")
public class HostMetric extends BeaseMetric {

    @Field(type = FieldType.Double)
    private double cpuUsage;
    @Field(type = FieldType.Double)
    private double memoryUsage;
    @Field(type = FieldType.Integer)
    private int hostCpuInMhb;
    @Field(type = FieldType.Integer)
    private int hostMemoryInMb;
    @Field(type = FieldType.Integer)
    private int cpuUsageInMhz;
    @Field(type = FieldType.Integer)
    private int memoryUsageInMhz;
    @Field(type = FieldType.Integer)
    private int cpuFreeSizeInMhz;
    @Field(type = FieldType.Integer)
    private int memoryFreeSizeInMhz;

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

    public int getHostCpuInMhb() {
        return hostCpuInMhb;
    }

    public void setHostCpuInMhb(int hostCpuInMhb) {
        this.hostCpuInMhb = hostCpuInMhb;
    }

    public int getHostMemoryInMb() {
        return hostMemoryInMb;
    }

    public void setHostMemoryInMb(int hostMemoryInMb) {
        this.hostMemoryInMb = hostMemoryInMb;
    }

    public int getCpuUsageInMhz() {
        return cpuUsageInMhz;
    }

    public void setCpuUsageInMhz(int cpuUsageInMhz) {
        this.cpuUsageInMhz = cpuUsageInMhz;
    }

    public int getMemoryUsageInMhz() {
        return memoryUsageInMhz;
    }

    public void setMemoryUsageInMhz(int memoryUsageInMhz) {
        this.memoryUsageInMhz = memoryUsageInMhz;
    }

    public int getCpuFreeSizeInMhz() {
        return cpuFreeSizeInMhz;
    }

    public void setCpuFreeSizeInMhz(int cpuFreeSizeInMhz) {
        this.cpuFreeSizeInMhz = cpuFreeSizeInMhz;
    }

    public int getMemoryFreeSizeInMhz() {
        return memoryFreeSizeInMhz;
    }

    public void setMemoryFreeSizeInMhz(int memoryFreeSizeInMhz) {
        this.memoryFreeSizeInMhz = memoryFreeSizeInMhz;
    }
}
