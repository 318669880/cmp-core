package com.fit2cloud.commons.server.model;

import com.fit2cloud.commons.server.base.domain.FlowLinkValue;

import java.util.List;

public class LinkValueDTO extends FlowLinkValue {
    private List<TreeNode> treeNodes;

    public List<TreeNode> getTreeNodes() {
        return treeNodes;
    }

    public void setTreeNodes(List<TreeNode> treeNodes) {
        this.treeNodes = treeNodes;
    }
}
