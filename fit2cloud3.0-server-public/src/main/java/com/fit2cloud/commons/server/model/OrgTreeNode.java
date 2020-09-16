package com.fit2cloud.commons.server.model;

import java.io.Serializable;
import java.util.List;


/**
 * @Company: FIT2CLOUD 飞致云
 * @Author: Mr.cyw
 * @Machine: chenyawen
 * @Date: 2020/9/16 4:50 下午
 * @Description: Please Write notes scientifically
 * 组织机构和工作空间树节点
 */


public class OrgTreeNode implements Serializable {

    private String nodeId;

    private String nodeName;

    private String nodeType;

    private Integer relativeNum;

    private String description;

    private String parentId;

    private Long createTime;

    private OrgTreeNode parentNode;

    private List<OrgTreeNode> childNodes;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public Integer getRelativeNum() {
        return relativeNum;
    }

    public void setRelativeNum(Integer relativeNum) {
        this.relativeNum = relativeNum;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public OrgTreeNode getParentNode() {
        return parentNode;
    }

    public void setParentNode(OrgTreeNode parentNode) {
        this.parentNode = parentNode;
    }

    public List<OrgTreeNode> getChildNodes() {
        return childNodes;
    }

    public void setChildNodes(List<OrgTreeNode> childNodes) {
        this.childNodes = childNodes;
    }
}
