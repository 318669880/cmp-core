package com.fit2cloud.commons.server.process.dto;

public class ActivityDTO {
    private String activityId;
    private Integer step;
    private String name;
    private String linkType;
    private String linkKey;
    private String assigneeType;
    private String assignee;
    private Object assigneeValue;
    private String rangeRole;
    private String url;
    private boolean end;
    private boolean auto;
    private boolean jump;

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    public String getLinkKey() {
        return linkKey;
    }

    public void setLinkKey(String linkKey) {
        this.linkKey = linkKey;
    }

    public String getAssigneeType() {
        return assigneeType;
    }

    public void setAssigneeType(String assigneeType) {
        this.assigneeType = assigneeType;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public Object getAssigneeValue() {
        return assigneeValue;
    }

    public void setAssigneeValue(Object assigneeValue) {
        this.assigneeValue = assigneeValue;
    }

    public String getRangeRole() {
        return rangeRole;
    }

    public void setRangeRole(String rangeRole) {
        this.rangeRole = rangeRole;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isEnd() {
        return end;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    public boolean isAuto() {
        return auto;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    public boolean isJump() {
        return jump;
    }

    public void setJump(boolean jump) {
        this.jump = jump;
    }
}
