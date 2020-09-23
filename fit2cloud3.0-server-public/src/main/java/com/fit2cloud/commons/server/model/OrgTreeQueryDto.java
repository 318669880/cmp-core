package com.fit2cloud.commons.server.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/9/16 9:23 下午
 * @Description: Please Write notes scientifically
 */
public class OrgTreeQueryDto implements Serializable {

    @ApiModelProperty(value = "根节点ID")
    private String rootId;
    @ApiModelProperty(value = "是否排除工作空间" ,required = false, dataType = "Boolean")
    private Boolean excludeWs;
    @ApiModelProperty(value = "节点名称")
    private String orgName;

    public String getRootId() {
        return rootId;
    }

    public void setRootId(String rootId) {
        this.rootId = rootId;
    }

    public Boolean getExcludeWs() {
        return excludeWs;
    }

    public void setExcludeWs(Boolean excludeWs) {
        this.excludeWs = excludeWs;
    }

    public OrgTreeQueryDto(String rootId, Boolean excludeWs) {
        this.rootId = rootId;
        this.excludeWs = excludeWs;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
}
