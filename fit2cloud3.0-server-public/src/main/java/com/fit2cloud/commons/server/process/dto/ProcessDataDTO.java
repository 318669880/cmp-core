package com.fit2cloud.commons.server.process.dto;

import io.swagger.annotations.ApiModelProperty;

public class ProcessDataDTO {

    @ApiModelProperty(value = "流程变量名", required = true)
    private String dataName;

    @ApiModelProperty(value = "流程变量值")
    private String dataValue;

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public String getDataValue() {
        return dataValue;
    }

    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }
}
