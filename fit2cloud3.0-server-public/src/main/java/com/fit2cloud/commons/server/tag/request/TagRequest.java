package com.fit2cloud.commons.server.tag.request;

import com.fit2cloud.commons.annotation.FuzzyQuery;
import io.swagger.annotations.ApiModelProperty;

public class TagRequest {

    @ApiModelProperty("标签Key,模糊匹配")
    @FuzzyQuery
    private String tagKey;

    @ApiModelProperty("标签别名,模糊匹配")
    @FuzzyQuery
    private String tagAlias;

    @ApiModelProperty(hidden = true)
    private String sort;

    public String getTagKey() {
        return tagKey;
    }

    public void setTagKey(String tagKey) {
        this.tagKey = tagKey;
    }

    public String getTagAlias() {
        return tagAlias;
    }

    public void setTagAlias(String tagAlias) {
        this.tagAlias = tagAlias;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
