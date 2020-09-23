package com.fit2cloud.plugin.container.sdk.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LabelSelector {

    private Map<String, String> matchLabels;

    private List<LabelSelectorRequirement> matchExpressions = new ArrayList<>();

    public LabelSelector() {
    }

    public LabelSelector(List<LabelSelectorRequirement> matchExpressions, Map<String, String> matchLabels) {
        this.matchExpressions = matchExpressions;
        this.matchLabels = matchLabels;
    }

    public Map<String, String> getMatchLabels() {
        return matchLabels;
    }

    public void setMatchLabels(Map<String, String> matchLabels) {
        this.matchLabels = matchLabels;
    }

    public List<LabelSelectorRequirement> getMatchExpressions() {
        return matchExpressions;
    }

    public void setMatchExpressions(List<LabelSelectorRequirement> matchExpressions) {
        this.matchExpressions = matchExpressions;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
    }
}
