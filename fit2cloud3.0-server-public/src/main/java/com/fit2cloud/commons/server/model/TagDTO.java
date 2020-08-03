package com.fit2cloud.commons.server.model;

import com.fit2cloud.commons.server.base.domain.Tag;
import com.fit2cloud.commons.server.base.domain.TagValue;

import java.util.List;

public class TagDTO extends Tag {
    private List<TagValue> tagValues;

    public List<TagValue> getTagValues() {
        return tagValues;
    }

    public void setTagValues(List<TagValue> tagValues) {
        this.tagValues = tagValues;
    }
}
