package com.fit2cloud.commons.server.utils;

public class MetricPromQuery {

    public static String getCpuUsageQuery(String resourceId) {
        return "100 - (avg by (instance) (irate(node_cpu_seconds_total{instance='" + resourceId + "', mode='idle'}[5m])) * 100)";
    }

    public static String getMemoryUsageQuery(String resourceId) {
        return String.format("(node_memory_MemTotal_bytes{instance='%s'} - node_memory_MemFree_bytes{instance='%s'}) / node_memory_MemTotal_bytes{instance='%s'} * 100", resourceId, resourceId, resourceId);
    }

    public static String getDiskUsageQuery(String resourceId) {
        return String.format("100 - node_filesystem_free_bytes{instance='%s',fstype!~'rootfs|selinuxfs|autofs|rpc_pipefs|tmpfs|udev|none|devpts|sysfs|debugfs|fuse.*'} / node_filesystem_size_bytes{instance='%s',fstype!~'rootfs|selinuxfs|autofs|rpc_pipefs|tmpfs|udev|none|devpts|sysfs|debugfs|fuse.*'} * 100", resourceId, resourceId);
    }

    public static String getNetInQuery(String resourceId) {
        return String.format("sum by (instance) (irate(node_network_receive_bytes_total{instance='%s',device!~'bond.*?|lo'}[5m])/128)", resourceId);
    }

    public static String getNetOutQuery(String resourceId) {
        return String.format("sum by (instance) (irate(node_network_transmit_bytes_total{instance='%s',device!~'bond.*?|lo'}[5m])/128)", resourceId);
    }
}
