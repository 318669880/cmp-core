package com.fit2cloud.commons.server.tag.request;

import com.fit2cloud.commons.annotation.FuzzyQuery;
import io.swagger.annotations.ApiModelProperty;

public class TagValueRequest {

    @ApiModelProperty("标签ID")
    private String tagId;

    @ApiModelProperty("标签Key")
    private String tagKey;

    @ApiModelProperty("标签值别名,模糊匹配")
    @FuzzyQuery
    private String tagValueAlias;

    @ApiModelProperty("标签值,模糊匹配")
    @FuzzyQuery
    private String tagValue;

    @ApiModelProperty(hidden = true)
    private String sort;

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getTagKey() {
        return tagKey;
    }

    public void setTagKey(String tagKey) {
        this.tagKey = tagKey;
    }

    public String getTagValueAlias() {
        return tagValueAlias;
    }

    public void setTagValueAlias(String tagValueAlias) {
        this.tagValueAlias = tagValueAlias;
    }

    public String getTagValue() {
        return tagValue;
    }

    public void setTagValue(String tagValue) {
        this.tagValue = tagValue;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
