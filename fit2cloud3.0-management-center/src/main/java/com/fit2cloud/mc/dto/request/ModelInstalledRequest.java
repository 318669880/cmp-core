package com.fit2cloud.mc.dto.request;

import com.fit2cloud.commons.annotation.FuzzyQuery;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/8/7 7:02 下午
 * @Description: Please Write notes scientifically
 */
public class ModelInstalledRequest {

    @ApiModelProperty("名称,模糊匹配")
    @FuzzyQuery
    private String name;


    @ApiModelProperty("模块名称")
    @FuzzyQuery
    private String mudule;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMudule() {
        return mudule;
    }

    public void setMudule(String mudule) {
        this.mudule = mudule;
    }
}
