package com.fit2cloud.mc.dto.request;

import com.fit2cloud.commons.annotation.FuzzyQuery;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class WorkspaceRequest {

    @ApiModelProperty("工作空间ID")
    private String id;

    @ApiModelProperty("工作空间名称,模糊匹配")
    @FuzzyQuery
    private String name;

    @ApiModelProperty("组织ID")
    private List<String> organizationIds;

    @ApiModelProperty(hidden = true)
    private List<String> resourceIds;

    @ApiModelProperty(hidden = true)
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

    /*public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }*/

    public List<String> getOrganizationIds() {
        return organizationIds;
    }

    public void setOrganizationIds(List<String> organizationIds) {
        this.organizationIds = organizationIds;
    }

    public List<String> getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(List<String> resourceIds) {
        this.resourceIds = resourceIds;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
