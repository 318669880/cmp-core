package com.fit2cloud.sdk.model;

public class F2CVmDatastoreUsage {
    private String datastoreUniqueId;
    private long commitedSize;
    private long uncommitedSize;

    public String getDatastoreUniqueId() {
        return datastoreUniqueId;
    }

    public void setDatastoreUniqueId(String datastoreUniqueId) {
        this.datastoreUniqueId = datastoreUniqueId;
    }

    public long getCommitedSize() {
        return commitedSize;
    }

    public void setCommitedSize(long commitedSize) {
        this.commitedSize = commitedSize;
    }

    public long getUncommitedSize() {
        return uncommitedSize;
    }

    public void setUncommitedSize(long uncommitedSize) {
        this.uncommitedSize = uncommitedSize;
    }
}
