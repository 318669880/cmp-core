package com.fit2cloud.commons.server.model;


import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class DynamicMetricData {
    private String uniqueLabel;
    private String seriesName;
    @ApiModelProperty("监控值")
    private List<Double> values;
    @ApiModelProperty("时间戳")
    private List<Long> timestamps;

    public String getUniqueLabel() {
        return uniqueLabel;
    }

    public void setUniqueLabel(String uniqueLabel) {
        this.uniqueLabel = uniqueLabel;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public List<Double> getValues() {
        return values;
    }

    public void setValues(List<Double> values) {
        this.values = values;
    }

    public List<Long> getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(List<Long> timestamps) {
        this.timestamps = timestamps;
    }
}
