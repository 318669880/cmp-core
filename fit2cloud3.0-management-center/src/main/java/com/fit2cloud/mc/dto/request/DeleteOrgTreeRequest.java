package com.fit2cloud.mc.dto.request;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/9/16 4:50 下午
 * @Description: Please Write notes scientifically
 * 组织机构和工作空间树节点
 */
public class DeleteOrgTreeRequest implements Serializable {
    @ApiModelProperty(value = "节点ID", required = true)
    private String nodeId;
    @ApiModelProperty(value = "节点类型", required = true ,allowableValues = "wks, org")
    private String nodeType;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }
}
