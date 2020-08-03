package com.fit2cloud.commons.server.constants;

public enum DiskMetricName {
    IOPSRead("I/OPs 读",  F2CMetricName.DISK_IOPS_READ),
    IOPSWrite("I/OPs 写",  F2CMetricName.DISK_IOPS_WRITE),
    BPSRead("BPs 读",  F2CMetricName.DISK_BPS_READ),
    BPSWrite("BPs 写",  F2CMetricName.DISK_BPS_WRITE);

    private String description;

    private F2CMetricName f2CMetricName;

    DiskMetricName(String description,  F2CMetricName f2CMetricName) {
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
