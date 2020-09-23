package com.fit2cloud.plugin.container.sdk.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liqiang on 2018/9/11.
 */
public class BaseObject {

    private String id;

    private String projectId;

    private String name;

    private String kind;

    private Long createTime;

    private String yml;

    private Map<String, String> label = new HashMap<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getYml() {
        return yml;
    }

    public void setYml(String yml) {
        this.yml = yml;
    }

    public Map<String, String> getLabel() {
        return label;
    }

    public void putLabel(Map<String, String> label) {
        if (label != null) {
            this.label.putAll(label);
        }
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
    }
}
