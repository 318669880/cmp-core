package com.fit2cloud.mc.dto.request;

import com.fit2cloud.commons.annotation.FuzzyQuery;
import io.swagger.annotations.ApiModelProperty;

public class ExtraUserRequest {

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("名称,模糊匹配")
    @FuzzyQuery
    private String name;

    @ApiModelProperty("邮箱,模糊匹配")
    @FuzzyQuery
    private String email;

    @ApiModelProperty("展示名称,模糊匹配")
    @FuzzyQuery
    private String displayName;

    @ApiModelProperty(value = "排序key", hidden = true)
    private String sort;

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
