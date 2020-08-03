package com.fit2cloud.commons.server.constants;

/**
 * 枚举的字段名是监控方法名的后缀
 */
public enum HostMetricName {
    CpuUsage("CPU使用率", F2CMetricName.HOST_CPU_USAGE),
    CpuUsageInMhz("CPU使用量", F2CMetricName.HOST_CPU_USAGE_IN_MHZ),
    HostCpuInMHZ("CPU总量", F2CMetricName.HOST_CPU_TOTAL_IN_MHZ),
    MemoryUsage("内存使用率", F2CMetricName.HOST_MEMORY_USAGE),
    MemoryUsageInMB("内存使用量", F2CMetricName.HOST_MEMORY_USAGE_IN_MB),
    HostMemoryInMB("内存总量", F2CMetricName.HOST_MEMORY_TOTAL_IN_MB);

    private String description;
    private F2CMetricName f2CMetricName;

    HostMetricName(String description, F2CMetricName f2CMetricName) {
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
