package com.fit2cloud.commons.server.model;

public class MetricItem {
    private Long id;
    private String resourceId;
    private String syncYear;
    private String syncMonth;
    private String syncDay;
    private String syncHour;
    private String syncMin;
    private Long syncTimestamp;
    private String metric;
    private Double maxValue;
    private Double averageValue;
    private Double minValue;
    private String source;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId == null ? null : resourceId.trim();
    }

    public String getSyncYear() {
        return syncYear;
    }

    public void setSyncYear(String syncYear) {
        this.syncYear = syncYear == null ? null : syncYear.trim();
    }

    public String getSyncMonth() {
        return syncMonth;
    }

    public void setSyncMonth(String syncMonth) {
        this.syncMonth = syncMonth == null ? null : syncMonth.trim();
    }

    public String getSyncDay() {
        return syncDay;
    }

    public void setSyncDay(String syncDay) {
        this.syncDay = syncDay == null ? null : syncDay.trim();
    }

    public String getSyncHour() {
        return syncHour;
    }

    public void setSyncHour(String syncHour) {
        this.syncHour = syncHour == null ? null : syncHour.trim();
    }

    public String getSyncMin() {
        return syncMin;
    }

    public void setSyncMin(String syncMin) {
        this.syncMin = syncMin == null ? null : syncMin.trim();
    }

    public Long getSyncTimestamp() {
        return syncTimestamp;
    }

    public void setSyncTimestamp(Long syncTimestamp) {
        this.syncTimestamp = syncTimestamp;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric == null ? null : metric.trim();
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
    }

    public Double getAverageValue() {
        return averageValue;
    }

    public void setAverageValue(Double averageValue) {
        this.averageValue = averageValue;
    }

    public Double getMinValue() {
        return minValue;
    }

    public void setMinValue(Double minValue) {
        this.minValue = minValue;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source == null ? null : source.trim();
    }
}