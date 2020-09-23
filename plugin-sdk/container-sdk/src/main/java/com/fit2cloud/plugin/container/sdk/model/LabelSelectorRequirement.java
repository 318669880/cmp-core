package com.fit2cloud.plugin.container.sdk.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

public class LabelSelectorRequirement {

    public LabelSelectorRequirement() {
    }

    public LabelSelectorRequirement(String key, String operator, List<String> values) {
        this.key = key;
        this.operator = operator;
        this.values = values;
    }

    private String key;

    private String operator;

    private List<String> values = new ArrayList<>();

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
    }
}
