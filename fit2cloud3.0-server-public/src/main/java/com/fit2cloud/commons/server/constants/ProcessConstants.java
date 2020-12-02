package com.fit2cloud.commons.server.constants;

public class ProcessConstants {

    public static final String ROLE = "ROLE:";

    public static final String OWNER = "OWNER";

    public static final String SYSTEM = "SYSTEM";

    public enum ProcessStatus {
        PENDING, COMPLETED, TERMINATED, CANCEL
    }

    public enum TaskStatus {
        PENDING, COMPLETED, TERMINATED, CANCEL
    }

    public enum AssigneeType {
        CREATOR, USER, SYSTEM_ROLE, PROCESS_ROLE, VARIABLES
    }

    /**
     * 事件类型
     */
    public enum EventType {
        TASK, PROCESS
    }

    /**
     * 触发操作
     */
    public enum EventOperation {
        PENDING, COMPLETE, TERMINATE, CANCEL
    }

    /**
     * 消息种类
     */
    public enum SmsType {
        EMAIL, SMS, ANNOUNCEMENT, DINGTALK, WECHAT
    }

    /**
     * 流程类型
     */
    public enum MessageProcessType {
        TASK, PROCESS
    }

    /**
     * 触发操作
     */
    public enum MessageOperation {
        PENDING, COMPLETE, TERMINATE, BUSINESS_COMPLETE, CANCEL
    }

    /**
     * 消息状态
     */
    public enum MessageStatus {
        READ, UNREAD, SUCCESS, ERROR
    }

    /**
     * 模型中环节类型
     */
    public enum ModelLinkType {
        CUSTOMIZE, PREDEFINE
    }

    /**
     * 环节类型
     */
    public enum LinkType {
        CUSTOM, SYSTEM
    }


}
