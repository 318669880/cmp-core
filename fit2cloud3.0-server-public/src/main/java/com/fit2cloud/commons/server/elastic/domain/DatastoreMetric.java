package com.fit2cloud.commons.server.elastic.domain;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "metric_datastore", type = "metric")
public class DatastoreMetric extends BeaseMetric {

    @Field(type = FieldType.Double)
    private double dataStoreTotalSpace;
    @Field(type = FieldType.Double)
    private double dataStoreSpaceUsed;
    @Field(type = FieldType.Double)
    private double dataStoreFreeSize;
    @Field(type = FieldType.Double)
    private double spaceUsage;
    @Field(type = FieldType.Double)
    private double provisionedTotal;

    public double getDataStoreTotalSpace() {
        return dataStoreTotalSpace;
    }

    public void setDataStoreTotalSpace(double dataStoreTotalSpace) {
        this.dataStoreTotalSpace = dataStoreTotalSpace;
    }

    public double getDataStoreSpaceUsed() {
        return dataStoreSpaceUsed;
    }

    public void setDataStoreSpaceUsed(double dataStoreSpaceUsed) {
        this.dataStoreSpaceUsed = dataStoreSpaceUsed;
    }

    public double getDataStoreFreeSize() {
        return dataStoreFreeSize;
    }

    public void setDataStoreFreeSize(double dataStoreFreeSize) {
        this.dataStoreFreeSize = dataStoreFreeSize;
    }

    public double getSpaceUsage() {
        return spaceUsage;
    }

    public void setSpaceUsage(double spaceUsage) {
        this.spaceUsage = spaceUsage;
    }

    public double getProvisionedTotal() {
        return provisionedTotal;
    }

    public void setProvisionedTotal(double provisionedTotal) {
        this.provisionedTotal = provisionedTotal;
    }
}
