package com.fit2cloud.mc.dto.request;

import com.fit2cloud.commons.annotation.FuzzyQuery;
import io.swagger.annotations.ApiModelProperty;

public class OrganizationRequest {

    @ApiModelProperty("组织ID")
    private String id;

    @ApiModelProperty("组织名称,模糊匹配")
    @FuzzyQuery
    private String name;

    @ApiModelProperty(value = "排序Key", hidden = true)
    private String sort;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
