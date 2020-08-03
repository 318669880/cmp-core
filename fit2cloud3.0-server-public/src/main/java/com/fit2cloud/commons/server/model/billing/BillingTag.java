package com.fit2cloud.commons.server.model.billing;

import java.io.Serializable;

/**
 * Created by liqiang on 2018/12/11.
 */
public class BillingTag implements Serializable {

    private String tagKey;
    private String tagValueId;
    private String tagMappingId;

    public String getTagKey() {
        return tagKey;
    }

    public void setTagKey(String tagKey) {
        this.tagKey = tagKey;
    }

    public String getTagValueId() {
        return tagValueId;
    }

    public void setTagValueId(String tagValueId) {
        this.tagValueId = tagValueId;
    }

    public String getTagMappingId() {
        return tagMappingId;
    }

    public void setTagMappingId(String tagMappingId) {
        this.tagMappingId = tagMappingId;
    }
}
