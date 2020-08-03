package com.fit2cloud.commons.server.constants;

public class ModuleConstants {

    /**
     * 模块类型
     */
    public enum Type {
        standard, //标准模块
        extension,//扩展模块
        link //不继承自系统模块
    }


    /**
     * 运行状态
     */
    public enum RunningStatus {
        running,//运行中
        section_running,//部分运行
        stopped//停止
    }

    public enum LicenseStatus {
        active,
        inactive
    }
}
