package com.fit2cloud.commons.server.constants;

public class CloudAccountConstants {
    //云账号状态
    public enum Status {
        VALID, INVALID, DELETED
    }

    public enum SyncStatus {
        pending, success, error, sync
    }

}
