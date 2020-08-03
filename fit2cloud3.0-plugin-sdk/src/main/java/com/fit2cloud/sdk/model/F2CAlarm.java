package com.fit2cloud.sdk.model;


public class F2CAlarm {
    private String alarmId;
    private String resourceId;
    private F2CEntityType resourceType;
    private String level;
    private Long confirmedTime;
    private String description;
    private long createTime;

    public String getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(String alarmId) {
        this.alarmId = alarmId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public F2CEntityType getResourceType() {
        return resourceType;
    }

    public void setResourceType(F2CEntityType resourceType) {
        this.resourceType = resourceType;
    }

    public Long getConfirmedTime() {
        return confirmedTime;
    }

    public void setConfirmedTime(Long confirmedTime) {
        this.confirmedTime = confirmedTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
