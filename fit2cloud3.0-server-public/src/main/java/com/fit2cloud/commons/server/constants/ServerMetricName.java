package com.fit2cloud.commons.server.constants;

/**
 * 枚举的字段名是监控方法名的后缀
 */
public enum ServerMetricName {
    CpuUsage("CPU使用率", F2CMetricName.SERVER_CPU_USAGE),
    CpuUsageInMhz("CPU使用量", F2CMetricName.SERVER_CPU_USAGE_IN_MHZ),
    MemoryUsage("内存使用率", F2CMetricName.SERVER_MEMORY_USAGE),
    MemoryUsageInMB("内存使用量", F2CMetricName.SERVER_MEMORY_USAGE_IN_MB),
    VmMemoryAllocatedSize("内存分配值", F2CMetricName.SERVER_MEMORY_ALLOCATED_SIZE),
    VmDiskAllocatedSize("磁盘分配值", F2CMetricName.SERVER_DISK_ALLOCATED_SIZE),
    VmCpuMHZAllocatedSize("CPU分配值", F2CMetricName.SERVER_CPU_ALLOCATED_SIZE),
    BytesReceivedPerSecond("网络输入", F2CMetricName.SERVER_BYTES_RECEIVED_PER_SECOND),
    BytesTransmittedPerSecond("网络输出", F2CMetricName.SERVER_BYTES_TRANSMITTED_PER_SECOND),
    DiskReadAverage("磁盘读", F2CMetricName.SERVER_DISK_READ_AVERAGE),
    DiskWriteAverage("磁盘写", F2CMetricName.SERVER_DISK_WRITE_AVERAGE),
    VirtualDiskReadLatencyAverage("虚拟机磁盘延迟读取", F2CMetricName.SERVER_VIRTUAL_DISK_READ_LATENCY_AVERAGE),
    VirtualDiskWriteLatencyAverage("虚拟机磁盘延迟写", F2CMetricName.SERVER_VIRTUAL_DISK_WRITE_LATENCY_AVERAGE);

    private String description;
    private F2CMetricName f2CMetricName;

    ServerMetricName(String description, F2CMetricName f2CMetricName) {
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
