package com.fit2cloud.sdk.model;

import java.util.List;

/**
 * FIT2CLOUD定义的快照
 */
public class F2CSnapshot extends F2CResource {
    private String snapshotId;
    private String instanceId;
    private String instanceUuid;
    private String name;
    private String description;
    private Long createTime;
    private Boolean replaySupport;
    private String state;
    private boolean currentSnapshot;
    private List<F2CSnapshot> children;

    public String getSnapshotId() {
        return snapshotId;
    }

    public void setSnapshotId(String snapshotId) {
        this.snapshotId = snapshotId;
    }

    public Boolean getReplaySupport() {
        return replaySupport;
    }

    public void setReplaySupport(Boolean replaySupport) {
        this.replaySupport = replaySupport;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getInstanceUuid() {
        return instanceUuid;
    }

    public void setInstanceUuid(String instanceUuid) {
        this.instanceUuid = instanceUuid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<F2CSnapshot> getChildren() {
        return children;
    }

    public void setChildren(List<F2CSnapshot> children) {
        this.children = children;
    }

    public boolean isCurrentSnapshot() {
        return currentSnapshot;
    }

    public void setCurrentSnapshot(boolean currentSnapshot) {
        this.currentSnapshot = currentSnapshot;
    }
}
