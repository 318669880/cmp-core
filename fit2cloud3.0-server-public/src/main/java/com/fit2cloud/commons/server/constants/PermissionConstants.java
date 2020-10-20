package com.fit2cloud.commons.server.constants;

/**
 * 公共的权限 常量类
 */
public class PermissionConstants {

    //start
    /**
     * 虚拟机查看
     */
    public static final String CLOUD_SERVER_READ = "CLOUD_SERVER:READ";
    //end

    //start
    /**
     * 虚拟机查看
     */
    public static final String METRIC_READ = "METRIC:READ";
    //end

    /**
     * 流程管理
     */
    public static final String FLOW_MANAGER = "FLOW:MANAGER";

    // start 标签相关权限
    public static final String TAG_READ = "TAG:READ";
    public static final String TAG_CREATE = "TAG:READ+CREATE";
    public static final String TAG_EDIT = "TAG:READ+EDIT";
    public static final String TAG_DELETE = "TAG:READ+DELETE";
    public static final String TAG_VALUE_READ = "TAG:READ+TAG_VALUE:READ";
    public static final String TAG_VALUE_CREATE = "TAG:READ+TAG_VALUE:READ+CREATE";
    public static final String TAG_VALUE_EDIT = "TAG:READ+TAG_VALUE:READ+EDIT";
    public static final String TAG_VALUE_DELETE = "TAG:READ+TAG_VALUE:READ+DELETE";
    public static final String TAG_VALUE_IMPORT = "TAG:READ+TAG_VALUE:READ+IMPORT";
    // end 标签相关权限
}
