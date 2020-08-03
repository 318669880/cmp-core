package com.fit2cloud.commons.server.constants;

/**
 * 枚举的字段名是监控方法名的后缀
 */
public enum DatastoreMetricName {
    Provisioned("预置备空间", F2CMetricName.DATASTORE_PROVISIONED),
    SpaceUsed("存储器已用空间", F2CMetricName.DATASTORE_SPACE_USED),
    FreeSize("剩余空间", F2CMetricName.DATASTORE_FREE_SIZE),
    TotalSpace("存储器总容量", F2CMetricName.DATASTORE_TOTAL_SPACE);

    private String description;
    private F2CMetricName f2CMetricName;

    DatastoreMetricName(String description, F2CMetricName f2CMetricName) {
        this.description = description;
        this.f2CMetricName = f2CMetricName;
    }

    public String getDescription() {
        return description;
    }

    public F2CMetricName getF2CMetricName() {
        return f2CMetricName;
    }
}
