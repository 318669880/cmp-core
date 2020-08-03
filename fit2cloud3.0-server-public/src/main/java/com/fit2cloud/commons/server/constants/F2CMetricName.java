package com.fit2cloud.commons.server.constants;

public enum F2CMetricName {
    SERVER_CPU_USAGE("fit2cloud_server_cpu_usage_percent", "cloud server cpu usage", MetricUnit.PERCENT),
    SERVER_CPU_USAGE_IN_MHZ("fit2cloud_server_cpu_usage_mhz", "cloud server cpu metric in mhz", MetricUnit.MHz),
    SERVER_MEMORY_USAGE("fit2cloud_server_memory_usage_percent", "cloud server memory usage", MetricUnit.PERCENT),
    SERVER_MEMORY_USAGE_IN_MB("fit2cloud_server_memory_usage_mb", "cloud server memory usage in mb", MetricUnit.MB),
    SERVER_MEMORY_ALLOCATED_SIZE("fit2cloud_server_memory_allocated_size_mb", "cloud server memory allocated size", MetricUnit.MB),
    SERVER_DISK_ALLOCATED_SIZE("fit2cloud_server_disk_allocated_size_mb", "cloud server disk allocated size", MetricUnit.MB),
    SERVER_CPU_ALLOCATED_SIZE("fit2cloud_server_disk_allocated_size_mhz", "cloud server cpu allocated size", MetricUnit.MHz),
    SERVER_BYTES_RECEIVED_PER_SECOND("fit2cloud_server_bytes_received_per_second_bps", "cloud server bytes received per second", MetricUnit.KBPS),
    SERVER_BYTES_TRANSMITTED_PER_SECOND("fit2cloud_server_bytes_transmitted_per_second_bps", "cloud server bytes transmitted per second", MetricUnit.KBPS),
    SERVER_DISK_READ_AVERAGE("fit2cloud_server_disk_read_average_kbps", "cloud server disk read average", MetricUnit.KBPS),
    SERVER_DISK_WRITE_AVERAGE("fit2cloud_server_disk_write_average_kbps", "cloud server disk write average", MetricUnit.KBPS),
    SERVER_VIRTUAL_DISK_READ_LATENCY_AVERAGE("fit2cloud_server_virtual_disk_read_latency_average_bkps", "cloud server virtual disk read latency average", MetricUnit.KBPS),
    SERVER_VIRTUAL_DISK_WRITE_LATENCY_AVERAGE("fit2cloud_server_virtual_disk_write_latency_average_bkps", "cloud server virtual disk write latency average", MetricUnit.KBPS),
    SERVER_DISK_USAGE("fit2cloud_server_disk_usage", "cloud server disk write average", MetricUnit.PERCENT),
    HOST_CPU_USAGE("fit2cloud_host_cpu_usage_percent", "cloud host cpu usage", MetricUnit.PERCENT),
    HOST_CPU_USAGE_IN_MHZ("fit2cloud_host_cpu_usage_mhz", "cloud host cpu metric in mhz", MetricUnit.MHz),
    HOST_CPU_TOTAL_IN_MHZ("fit2cloud_host_cpu_total_mhz", "cloud host cpu total in mhz", MetricUnit.MHz),
    HOST_MEMORY_USAGE("fit2cloud_host_memory_usage_percent", "cloud host memory usage", MetricUnit.PERCENT),
    HOST_MEMORY_USAGE_IN_MB("fit2cloud_host_memory_usage_mb", "cloud host memory usage metric in mb", MetricUnit.MB),
    HOST_MEMORY_TOTAL_IN_MB("fit2cloud_host_memory_total_mb", "cloud host memory total mb", MetricUnit.MB),
    DISK_IOPS_READ("fit2cloud_disk_iops_read_ops", "cloud disk iops read", MetricUnit.KBPS),
    DISK_IOPS_WRITE("fit2cloud_disk_iops_write_ops", "cloud disk iops write", MetricUnit.KBPS),
    DISK_BPS_READ("fit2cloud_disk_bps_read_bps", "cloud disk bps read", MetricUnit.BPS),
    DISK_BPS_WRITE("fit2cloud_disk_bps_write_bps", "cloud disk bps write", MetricUnit.BPS),
    DATASTORE_PROVISIONED("fit2cloud_datastore_provisioned_gb", "cloud datastore provisioned", MetricUnit.GB),
    DATASTORE_SPACE_USED("fit2cloud_datastore_space_used_gb", "cloud datastore space used", MetricUnit.GB),
    DATASTORE_FREE_SIZE("fit2cloud_datastore_free_size_gb", "cloud datastore free size", MetricUnit.GB),
    DATASTORE_TOTAL_SPACE("fit2cloud_datastore_total_space_gb", "cloud datastore total space", MetricUnit.GB),

    SYS_CPU_USAGE("fit2cloud_sys_stats_cpu_usage", "sys stat cpu usage", MetricUnit.PERCENT),
    SYS_MEMORY_USAGE("fit2cloud_sys_stats_memory_usage", "sys stat memory usage", MetricUnit.PERCENT),
    SYS_DISK_USAGE("fit2cloud_sys_stats_disk_usage", "sys stat disk usage", MetricUnit.PERCENT),

    MYSQL_USE_CONNECTIONS("mysql_global_status_threads_connected", "the count of mysql used connections", MetricUnit.NONE),
    MYSQL_ABORTED_CONNECTS("mysql_global_status_aborted_connects", "the count of mysql aborted connections", MetricUnit.NONE),
    MYSQL_AVAILABLE_CONNECTIONS("mysql_global_variables_max_connections - mysql_global_status_threads_connected", "the count of mysql avaliable connections", MetricUnit.NONE),
    MYSQL_COMMANDS_TOTAL("mysql_global_status_commands_total", "the count of command", MetricUnit.NONE),
    MYSQL_BYTES_RECEIVED("mysql_global_status_bytes_received", "bytes received", MetricUnit.NONE),
    MYSQL_BYTES_SENT("mysql_global_status_bytes_sent", "bytes sent", MetricUnit.NONE);

    private String name;
    private String help;
    private MetricUnit unit;

    F2CMetricName(String name, String help, MetricUnit unit) {
        this.name = name;
        this.help = help;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public String getHelp() {
        return help;
    }

    public MetricUnit getUnit() {
        return unit;
    }
}
