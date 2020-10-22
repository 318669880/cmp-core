CREATE TABLE IF NOT EXISTS `cloud_account` (
  `id` varchar(50) CHARACTER SET utf8mb4 NOT NULL DEFAULT '' COMMENT '云账号ID',
  `name` varchar(64) CHARACTER SET utf8mb4 NOT NULL DEFAULT '' COMMENT '云账号名称',
  `credential` VARCHAR(2000) CHARSET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL  COMMENT '云账号凭证',
  `plugin_name` varchar(50) CHARACTER SET utf8mb4 NOT NULL DEFAULT '' COMMENT '插件名称',
  `status` varchar(10) CHARACTER SET utf8mb4 NOT NULL DEFAULT 'NEW' COMMENT '云账号',
  `sync_status` varchar(10) CHARACTER SET utf8mb4 DEFAULT 'pending' COMMENT '同步状态',
  `create_time` bigint(13) NOT NULL COMMENT '创建时间',
  `update_time` bigint(13) DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNQ_NAME` (`name`),
  KEY `IDX_PLUGIN` (`plugin_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `cloud_alarm` (
  `id` varchar(50) NOT NULL DEFAULT '',
  `account_id` varchar(50) DEFAULT NULL COMMENT '云账号 ID',
  `resource_id` varchar(255) NULL DEFAULT NULL COMMENT '资源 ID' ,
  `resource_type` varchar(32) DEFAULT NULL COMMENT '资源类型',
  `description` varchar(512) DEFAULT NULL COMMENT '描述信息',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `confirmed_time` bigint(20) DEFAULT NULL COMMENT '确认时间',
  `status` varchar(32) DEFAULT NULL COMMENT '状态',
  `level` varchar(32) DEFAULT NULL COMMENT '告警级别',
  `sync_week` varchar(7) DEFAULT NULL COMMENT '周',
  `sync_day` varchar(10) DEFAULT NULL COMMENT '日',
  `last_sync_timestamp` bigint(20) DEFAULT NULL COMMENT '时间戳',
  `sync_hour` varchar(13) DEFAULT NULL COMMENT '时',
  `sync_year` varchar(4) DEFAULT NULL COMMENT '年',
  `sync_month` varchar(7) DEFAULT NULL COMMENT '月',
  PRIMARY KEY (`id`),
  KEY `IDX_ACCOUNT_ID` (`account_id`),
  KEY `IDX_SYNC_DAY` (`sync_day`),
  KEY `IDX_SYNC_TIMESPACE` (`last_sync_timestamp`),
  KEY `IDX_SYNC_HOUR` (`sync_hour`),
  KEY `IDX_SYNC_YEAR` (`sync_year`),
  KEY `IDX_SYNC_MONTH` (`sync_month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `cloud_cluster` (
  `id` varchar(50) NOT NULL DEFAULT '',
  `account_id` varchar(50) NOT NULL DEFAULT '' COMMENT '云账号 ID',
  `region` varchar(255) DEFAULT NULL COMMENT '数据中心',
  `zone` varchar(255) DEFAULT NULL COMMENT '集群',
  `host_count` int(11) DEFAULT NULL COMMENT '宿主机数量',
  `vm_running_count` int(11) DEFAULT NULL COMMENT '运行中虚拟机数',
  `vm_stopped_count` int(11) DEFAULT NULL COMMENT '已停止虚拟机数',
  `cpu_total` bigint(20) DEFAULT NULL COMMENT 'CPU 总量',
  `cpu_allocated` bigint(20) DEFAULT NULL COMMENT 'CPU 已分配',
  `memory_total` bigint(20) DEFAULT NULL COMMENT '内存总量',
  `memory_allocated` bigint(20) DEFAULT NULL COMMENT '内存已分配',
  `last_sync_timestamp` bigint(13) NOT NULL COMMENT '上次同步时间',
  PRIMARY KEY (`id`),
  KEY `IDX_CLOUD_CLUSTER_ACCOUNT_ID` (`account_id`),
  KEY `IDX_CLOUD_CLUSTER_REGION` (`region`),
  KEY `IDX_CLOUD_CLUSTER_ZONE` (`zone`),
  KEY `IDX_CLOUD_CLUSTER_HOST_COUNT` (`host_count`),
  KEY `IDX_CLOUD_CLUSTER_VM_RUNNING_COUNT` (`vm_running_count`),
  KEY `IDX_CLOUD_CLUSTER_VM_STOPPED_COUNT` (`vm_stopped_count`),
  KEY `IDX_CLOUD_CLUSTER_CPU_TOTAL` (`cpu_total`),
  KEY `IDX_CLOUD_CLUSTER_MEMORY_TOTAL` (`memory_total`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `cloud_datastore` (
  `id` varchar(50) NOT NULL DEFAULT '',
  `account_id` varchar(50) NOT NULL DEFAULT '' COMMENT '云账号 ID',
  `last_sync_timestamp` bigint(13) DEFAULT NULL COMMENT '上次同步时间',
  `datastore_id` varchar(256) DEFAULT NULL COMMENT '存储 ID',
  `datastore_name` varchar(256) DEFAULT NULL COMMENT '存储名称',
  `capacity` bigint(20) DEFAULT NULL COMMENT '容量',
  `free_space` bigint(20) DEFAULT NULL COMMENT '剩余',
  `status` varchar(45) DEFAULT NULL COMMENT '状态',
  `type` varchar(45) DEFAULT NULL COMMENT '存储类型',
  `region` varchar(256) DEFAULT NULL COMMENT '数据中心',
  `zone` varchar(256) DEFAULT NULL COMMENT '集群',
  PRIMARY KEY (`id`),
  KEY `IDX_ACCOUNT` (`account_id`),
  KEY `IDX_DATASTORE` (`datastore_id`),
  KEY `IDX_REGION` (`region`),
  KEY `IDX_STATUS` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `cloud_disk` (
  `id` varchar(50) NOT NULL DEFAULT '',
  `region` varchar(256) DEFAULT NULL COMMENT '数据中心/区域',
  `zone` varchar(256) DEFAULT NULL COMMENT '集群/可用区',
  `disk_id` varchar(128) DEFAULT NULL COMMENT '磁盘UUID',
  `disk_name` varchar(128) DEFAULT NULL COMMENT '磁盘名称',
  `disk_type` varchar(45) DEFAULT NULL COMMENT '磁盘置备方式',
  `category` varchar(45) DEFAULT NULL COMMENT '磁盘类型',
  `status` varchar(45) DEFAULT NULL COMMENT '磁盘状态',
  `disk_charge_type` varchar(45) DEFAULT NULL COMMENT '计费方式',
  `description` varchar(45) DEFAULT NULL COMMENT '描述',
  `size` bigint(16) DEFAULT NULL COMMENT '大小(GB)',
  `device` varchar(255) DEFAULT NULL COMMENT '挂载点',
  `create_time` bigint(14) DEFAULT NULL COMMENT '创建时间',
  `account_id` varchar(50) NOT NULL DEFAULT '' COMMENT '云账号ID',
  `datastore_id` varchar(255) DEFAULT NULL COMMENT '存储器ID',
  `last_sync_timestamp` bigint(13) DEFAULT NULL COMMENT '上次同步时间',
  `instance_uuid` varchar(128) DEFAULT NULL COMMENT '关联虚拟机UUID',
  `delete_with_instance` VARCHAR(50) NULL DEFAULT NULL COMMENT '随实例释放',
  PRIMARY KEY (`id`),
  KEY `IDX_ACCOUNT` (`account_id`),
  KEY `IDX_INS_UUID` (`instance_uuid`),
  KEY `IDX_DATASTORE` (`datastore_id`),
  KEY `IDX_DISK_ID` (`disk_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `cloud_disk`
ADD COLUMN `workspace_id` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '工作空间ID' AFTER `instance_uuid`,
ADD COLUMN `project_id` VARCHAR(50) NULL DEFAULT NULL COMMENT 'Project ID' AFTER `workspace_id`,
ADD COLUMN `bootable` TINYINT(1) NULL DEFAULT 0 COMMENT '是否启动盘' AFTER `project_id`,
ADD COLUMN `image_id` VARCHAR(50) NULL DEFAULT NULL COMMENT '镜像ID' AFTER `bootable`;

CREATE TABLE IF NOT EXISTS `cloud_event` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_id` varchar(50) DEFAULT NULL COMMENT '云账号 ID',
  `region` varchar(64) DEFAULT NULL COMMENT '数据中心',
  `zone` varchar(64) DEFAULT NULL COMMENT '集群',
  `host_name` varchar(128) DEFAULT NULL COMMENT '主机名',
  `virtual_machine_name` varchar(256) DEFAULT NULL COMMENT '虚拟机名',
  `network_name` varchar(128) DEFAULT NULL COMMENT '网络名',
  `distributed_virtual_switch_name` varchar(128) DEFAULT NULL COMMENT '交换机名',
  `user_name` varchar(128) DEFAULT NULL COMMENT '用户名',
  `level` varchar(64) DEFAULT NULL COMMENT '事件级别',
  `event_type` varchar(64) DEFAULT NULL COMMENT '事件类型',
  `message` varchar(512) DEFAULT NULL COMMENT '消息内容',
  `sync_year` varchar(4) DEFAULT NULL COMMENT '年',
  `sync_month` varchar(7) DEFAULT NULL COMMENT '月',
  `sync_week` varchar(7) DEFAULT NULL COMMENT '周',
  `sync_day` varchar(10) DEFAULT NULL COMMENT '日',
  `sync_hour` varchar(13) DEFAULT NULL COMMENT '时',
  `last_sync_timestamp` bigint(13) DEFAULT NULL COMMENT '时间戳',
  `create_timestamp` bigint(13) NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `IDX_CLOUD_EVENT_ACCOUNT_ID` (`account_id`),
  KEY `IDX_CLOUD_EVENT_DATACENTER_NAME` (`region`),
  KEY `IDX_CLOUD_EVENT_CLUSTER_NAME` (`zone`),
  KEY `IDX_CLOUD_EVENT_EVENT_LEVEL` (`level`),
  KEY `IDX_CLOUD_EVENT_EVENT_TYPE` (`event_type`),
  KEY `IDX_CLOUD_EVENT_SYNC_TIMESTAMP` (`last_sync_timestamp`)
) ENGINE=InnoDB AUTO_INCREMENT=1860 DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `cloud_host` (
  `id` varchar(50) NOT NULL DEFAULT '',
  `account_id` varchar(50) NOT NULL DEFAULT '' COMMENT '云账号 ID',
  `region` varchar(256) DEFAULT NULL COMMENT '数据中心',
  `zone` varchar(256) DEFAULT NULL COMMENT '集群',
  `host_id` varchar(256) DEFAULT NULL COMMENT '主机ID',
  `host_name` varchar(256) DEFAULT NULL COMMENT '主机名',
  `cpu_total` bigint(20) DEFAULT NULL COMMENT 'CPU 总量',
  `cpu_allocated` bigint(20) DEFAULT NULL COMMENT 'CPU 已分配',
  `memory_total` bigint(20) DEFAULT NULL COMMENT '内存总量',
  `memory_allocated` bigint(20) DEFAULT NULL COMMENT '内存已分配',
  `vm_total` bigint(20) DEFAULT NULL COMMENT '虚拟机总数',
  `vm_running` bigint(20) DEFAULT NULL COMMENT '运行中虚拟机数',
  `vm_stopped` bigint(20) DEFAULT NULL COMMENT '已停止虚拟机数',
  `host_ip` varchar(45) DEFAULT NULL COMMENT '主机 IP',
  `status` varchar(45) DEFAULT NULL COMMENT '主机状态',
  `hypervisor_type` varchar(64) DEFAULT NULL COMMENT '虚拟化类型',
  `hypervisor_version` varchar(64) DEFAULT NULL COMMENT '虚拟化版本',
  `host_vendor` varchar(128) DEFAULT NULL COMMENT '主机提供商',
  `host_model` varchar(128) DEFAULT NULL COMMENT '主机型号',
  `cpu_model` varchar(128) DEFAULT NULL COMMENT 'CPU 型号',
  `cpu_mHz_per_one_core` int(11) DEFAULT '0' COMMENT '单核 CPU 容量',
  `num_cpu_cores` int(11) DEFAULT '0' COMMENT 'CPU 总数',
  `last_sync_timestamp` bigint(13) DEFAULT NULL COMMENT '上次同步时间',
  `maintenance_timestamp` bigint(20) DEFAULT '0' COMMENT '维保日期',
  `vm_cpu_cores` int(8) DEFAULT '0' COMMENT '虚拟核数',
  PRIMARY KEY (`id`),
  KEY `IDX_ACCOUNT` (`account_id`),
  KEY `IDX_REGION` (`region`),
  KEY `IDX_HOST` (`host_id`),
  KEY `IDX_STATUS` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS `cloud_image` (
  `id` varchar(50) NOT NULL DEFAULT '',
  `account_id` varchar(50) NOT NULL DEFAULT '' COMMENT '云账号 ID',
  `image_id` varchar(512) DEFAULT NULL COMMENT '镜像 ID',
  `region` varchar(100) DEFAULT NULL COMMENT '数据中心/区域',
  `image_name` VARCHAR(512) CHARSET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT ''  NOT NULL  COMMENT '镜像名',
  `description` text COMMENT '描述',
  `os` varchar(50) DEFAULT NULL COMMENT '操作系统',
  `create_time` bigint(13) DEFAULT NULL COMMENT '创建时间',
  `workspace_id` varchar(50) NOT NULL DEFAULT '' COMMENT '工作空间 ID',
  `last_sync_timestamp` bigint(13) NOT NULL COMMENT '上次同步时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNQ_IMAGE` (`image_id`,`account_id`,`region`),
  KEY `IDX_ACCOUNT` (`account_id`),
  KEY `IDX_REGION` (`region`),
  KEY `IDX_IMAGE` (`image_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `cloud_server` (
  `id` varchar(50) NOT NULL DEFAULT '',
  `instance_uuid` varchar(50) DEFAULT NULL COMMENT '实例唯一标识',
  `workspace_id` varchar(50) NOT NULL DEFAULT '' COMMENT '工作空间 ID',
  `account_id` varchar(50) NOT NULL COMMENT '云账号 ID',
  `instance_id` varchar(256) DEFAULT NULL COMMENT '实例 ID',
  `instance_name` varchar(256) DEFAULT NULL COMMENT '实例名称',
  `image_id` varchar(128) DEFAULT NULL COMMENT '镜像 ID',
  `instance_status` varchar(45) DEFAULT NULL COMMENT '实例状态',
  `instance_type` varchar(64) DEFAULT NULL COMMENT '实例类型',
  `instance_type_description` varchar(64) DEFAULT NULL COMMENT '实例类型描述',
  `region` varchar(256) DEFAULT NULL COMMENT '数据中心/区域',
  `zone` varchar(256) DEFAULT NULL COMMENT '可用区/集群',
  `host` varchar(256) DEFAULT NULL COMMENT '宿主机',
  `remote_ip` varchar(64) DEFAULT NULL COMMENT '公网 IP',
  `ip_array` text COMMENT 'IP 地址',
  `os` varchar(50) DEFAULT NULL COMMENT '操作系统Key',
  `os_version` varchar(50) DEFAULT NULL COMMENT '操作系统版本',
  `cpu` int(8) DEFAULT '0' COMMENT 'CPU 核数',
  `memory` int(8) DEFAULT '0' COMMENT '内存容量',
  `disk` int(16) DEFAULT '0' COMMENT '磁盘容量',
  `create_time` bigint(13) DEFAULT NULL COMMENT '创建时间',
  `last_sync_timestamp` bigint(13) NOT NULL COMMENT '上次同步时间',
  `hostname` varchar(256) DEFAULT NULL COMMENT '主机名',
  `management_ip` varchar(64) DEFAULT NULL COMMENT '管理 IP',
  `management_port` int(11) DEFAULT NULL COMMENT '管理端口',
  `os_info` varchar(128) DEFAULT NULL COMMENT '云平台操作系统信息',
  `remark` varchar(128) DEFAULT NULL COMMENT '备注',
  `network` varchar(255) DEFAULT NULL COMMENT '网络',
  `vpc_id` varchar(255) DEFAULT NULL COMMENT 'VPC ID',
  `subnet_id` varchar(255) DEFAULT NULL COMMENT '子网 ID',
  `network_interface_id` varchar(255) DEFAULT NULL COMMENT '网络接口 ID',
  `management_ipv6` varchar(64) DEFAULT NULL COMMENT '管理ip:ipv6',
  `remote_ipv6` varchar(64) DEFAULT NULL COMMENT '公网ipv6',
  `local_ipv6` varchar(64) DEFAULT NULL COMMENT '内网ipv6',
  `ip_type` varchar(50) DEFAULT NULL COMMENT 'ip类型: ipv4、 ipv6 、DualStack',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNIQUE_SERVER` (`instance_uuid`,`account_id`),
  KEY `IDX_WORKSPACE` (`workspace_id`),
  KEY `IDX_ACCOUNT` (`account_id`),
  KEY `IDX_STATUS` (`instance_status`),
  KEY `IDX_REGION` (`region`),
  KEY `IDX_ZONE` (`zone`),
  KEY `IDX_HOST` (`host`),
  KEY `IDX_UUID` (`instance_uuid`),
  KEY `IDX_LAST_SYNC` (`last_sync_timestamp`),
  KEY `IDX_REMARK` (`remark`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `cloud_server`
ADD COLUMN `project_id` VARCHAR(50) NULL DEFAULT NULL COMMENT 'Project ID' AFTER `workspace_id`;

CREATE TABLE IF NOT EXISTS `cloud_server_credential` (
  `id` varchar(50) NOT NULL,
  `username` varchar(64) DEFAULT NULL COMMENT '主机登录用户名',
  `password` varchar(64) DEFAULT NULL COMMENT '主机登录密码',
  `secret_key` text COMMENT '主机登录密钥',
  `cloud_server_id` varchar(50) DEFAULT NULL COMMENT '云主机ID',
  `create_time` bigint(13) DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `IDX_SERVER_ID` (`cloud_server_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `dashboard` (
  `id` varchar(50) NOT NULL,
  `user_id` varchar(50) NOT NULL COMMENT '用户ID',
  `card_id` varchar(50) DEFAULT NULL COMMENT 'card_Id',
  `position` varchar(10) DEFAULT NULL COMMENT '位置',
  `sort` int(10) DEFAULT NULL COMMENT '排序',
  `display` tinyint(1) DEFAULT NULL COMMENT '是否显示',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNQ_USERID_CARDID` (`user_id`,`card_id`) USING BTREE,
  KEY `IDX_USER` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `dictionary` (
  `id` varchar(50) NOT NULL DEFAULT '',
  `category` varchar(60) NOT NULL DEFAULT '' COMMENT '分组',
  `dictionary_key` varchar(60) NOT NULL DEFAULT '' COMMENT '键值对-键',
  `dictionary_value` varchar(255) NOT NULL DEFAULT '' COMMENT '键值对-值',
  `long_value` longblob COMMENT '长字段内容',
  `description` varchar(60) DEFAULT NULL,
  `dictionary_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `IDX_CATEGORY` (`category`),
  KEY `IDX_CATEGORY_KEY` (`dictionary_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `extra_user` (
  `id` varchar(64) NOT NULL DEFAULT '' COMMENT 'Id',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '名称',
  `display_name` varchar(255) DEFAULT NULL COMMENT '展示名称',
  `phone` varchar(11) DEFAULT NULL COMMENT '手机号码',
  `type` varchar(64) NOT NULL DEFAULT '' COMMENT '用户类型',
  `sync_time` bigint(13) NOT NULL COMMENT '同步时间时间戳',
  PRIMARY KEY (`id`),
  KEY `IDX_EMAIL` (`email`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `extra_user` ADD UNIQUE `IDX_NAME` USING BTREE (`name`) comment '';

CREATE TABLE  IF NOT EXISTS `file_store` (
  `id` varchar(64) NOT NULL,
  `business_key` varchar(64) DEFAULT NULL COMMENT '文件分类',
  `name` varchar(64) NOT NULL COMMENT '名称',
  `size` bigint(13) NOT NULL COMMENT '文件大小',
  `file` longblob COMMENT '文件',
  `create_time` bigint(13) NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `IDX_BUSINESS` (`business_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `flow_deploy` (
  `deploy_id` varchar(36) CHARACTER SET utf8mb4 NOT NULL DEFAULT '' COMMENT '流程模型部署ID',
  `deploy_content` mediumtext CHARACTER SET utf8mb4 COMMENT '部署内容',
  `deploy_version` bigint(13) NOT NULL COMMENT '部署版本',
  `deploy_time` bigint(13) DEFAULT NULL COMMENT '部署时间',
  `model_id` varchar(64) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '流程模型ID',
  PRIMARY KEY (`deploy_id`),
  KEY `IDX_MODEL_ID` (`model_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE  IF NOT EXISTS `flow_event` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '事件ID',
  `name` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '事件名称',
  `executor` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '执行器',
  `model_id` varchar(36) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '模型ID',
  `step` int(10) DEFAULT NULL COMMENT '环节ID',
  `type` varchar(30) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '事件类型',
  `position` varchar(36) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '触发时机',
  `operation` varchar(36) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '触发操作',
  `arguments` mediumtext CHARACTER SET utf8mb4 COMMENT '参数',
  `module` varchar(50) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '模块ID',
  PRIMARY KEY (`id`),
  KEY `IDX_MODEL_ID` (`model_id`) USING BTREE,
  KEY `IDX_STEP` (`step`) USING BTREE,
  KEY `IDX_MODULE` (`module`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

ALTER TABLE `flow_event` ADD COLUMN `activity_id` varchar(36) COMMENT '环节ID' AFTER `model_id`, ADD INDEX `IDX_ACTIVITY_ID` (`activity_id`) comment '';


CREATE TABLE IF NOT EXISTS `flow_model` (
  `model_id` varchar(64) CHARACTER SET utf8mb4 NOT NULL DEFAULT '' COMMENT '流程模型ID',
  `model_name` varchar(100) CHARACTER SET utf8mb4 NOT NULL DEFAULT '' COMMENT '模型名称',
  `model_content` mediumtext CHARACTER SET utf8mb4 NOT NULL COMMENT '模型内容',
  `model_version` bigint(13) NOT NULL COMMENT '版本',
  `model_creator` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '创建者',
  `model_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `deploy_id` varchar(36) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '部署ID',
  `module` varchar(50) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '模块ID',
  PRIMARY KEY (`model_id`),
  KEY `IDX_MODEL_NAME` (`model_name`) USING BTREE,
  KEY `IDX_MODULE` (`module`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=COMPACT;

CREATE TABLE IF NOT EXISTS `flow_notification_config` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '消息配置ID',
  `model_id` varchar(36) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '流程模型ID',
  `step` int(10) DEFAULT NULL COMMENT '环节顺序号',
  `process_type` varchar(30) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '流程类型',
  `sms_type` varchar(30) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '消息类型',
  `position` varchar(36) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '触发时机',
  `operation` varchar(36) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '触发操作',
  `template` longtext CHARACTER SET utf8mb4 COMMENT '消息模板',
  `module` varchar(50) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '模块ID',
  PRIMARY KEY (`id`),
  KEY `IDX_MODEL_ID` (`model_id`) USING BTREE,
  KEY `IDX_STEP` (`step`) USING BTREE,
  KEY `IDX_MODULE` (`module`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

ALTER TABLE `flow_notification_config` ADD COLUMN `activity_id` varchar(36) COMMENT '环节ID' AFTER `model_id`, ADD INDEX `IDX_ACTIVITY_ID` (`activity_id`) comment '';


CREATE TABLE IF NOT EXISTS `flow_notification_inbox` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `receiver` varchar(50) CHARACTER SET utf8mb4 NOT NULL COMMENT '收件人',
  `process_type` varchar(30) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '流程类型',
  `sms_type` varchar(30) CHARACTER SET utf8mb4 NOT NULL COMMENT '消息类型',
  `title` varchar(255) CHARACTER SET utf8mb4 NOT NULL COMMENT '标题',
  `receive_time` bigint(13) NOT NULL COMMENT '发送时间',
  `content` longtext CHARACTER SET utf8mb4 NOT NULL COMMENT '消息内容',
  `status` varchar(10) CHARACTER SET utf8mb4 NOT NULL COMMENT '消息状态',
  PRIMARY KEY (`id`),
  KEY `IDX_EMAIL` (`receiver`),
  KEY `IDX_TYPE` (`sms_type`),
  KEY `IDX_STATUS` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `flow_process` (
  `process_id` varchar(36) CHARACTER SET utf8mb4 NOT NULL DEFAULT '' COMMENT '流程ID',
  `process_name` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '流程名称',
  `process_status` varchar(30) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '流程状态',
  `process_creator` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '发起人',
  `process_start_time` bigint(13) DEFAULT NULL COMMENT '开始时间',
  `process_end_time` bigint(13) DEFAULT NULL COMMENT '结束时间',
  `deploy_id` varchar(36) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '部署ID',
  `business_key` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '关联业务ID',
  `business_type` varchar(50) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '关联业务类型',
  `workspace_id` varchar(50) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '工作空间ID',
  `module` varchar(50) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '模块ID',
  PRIMARY KEY (`process_id`),
  KEY `IDX_DEPLOY_ID` (`deploy_id`) USING BTREE,
  KEY `IDX_BUSINESS_KEY` (`business_key`) USING BTREE,
  KEY `IDX_MODULE` (`module`) USING BTREE,
  KEY `IDX_WORKSPACE_ID` (`workspace_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `flow_process_data` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '流程参数ID',
  `process_id` varchar(36) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '流程ID',
  `task_id` varchar(36) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '任务ID',
  `task_step` int(10) DEFAULT NULL COMMENT '任务顺序号',
  `data_name` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '参数名',
  `data_value` longtext CHARACTER SET utf8mb4 COMMENT '参数值',
  PRIMARY KEY (`id`),
  KEY `IDX_TASK` (`task_id`),
  KEY `IDX_PROCESS_ID` (`process_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `flow_process_log` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '流程日志ID',
  `process_id` varchar(36) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '流程ID',
  `task_id` varchar(36) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '任务ID',
  `task_step` int(10) DEFAULT NULL COMMENT '任务顺序号',
  `task_name` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '任务名称',
  `task_executor` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '任务执行人',
  `task_start_time` bigint(13) DEFAULT NULL COMMENT '任务开始时间',
  `task_end_time` bigint(13) DEFAULT NULL COMMENT '任务结束时间',
  `task_remarks` longtext CHARACTER SET utf8mb4 COMMENT '审批备注',
  `business_key` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '关联业务ID',
  PRIMARY KEY (`id`),
  KEY `IDX_PROCESS_ID` (`process_id`) USING BTREE,
  KEY `IDX_BUSINESS_KEY` (`business_key`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=173 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `flow_role` (
  `role_key` varchar(100) CHARACTER SET utf8mb4 NOT NULL DEFAULT '' COMMENT '角色ID',
  `role_name` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '角色名称',
  `module` varchar(50) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '模块ID',
  PRIMARY KEY (`role_key`),
  KEY `IDX_MODULE` (`module`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `flow_role_user` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '角色成员ID',
  `user_id` varchar(50) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '用户ID',
  `role_key` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`),
  KEY `IDX_USER` (`user_id`),
  KEY `IDX_ROLE` (`role_key`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `flow_task` (
  `task_id` varchar(36) CHARACTER SET utf8mb4 NOT NULL DEFAULT '' COMMENT '任务ID',
  `task_step` int(10) DEFAULT NULL COMMENT '环节顺序',
  `task_name` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '任务名称',
  `task_status` varchar(30) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '任务状态',
  `task_assignee` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '任务指定执行人',
  `task_executor` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '任务实际执行人',
  `task_start_time` bigint(13) DEFAULT NULL COMMENT '任务开始时间',
  `task_end_time` bigint(13) DEFAULT NULL COMMENT '任务结束时间',
  `task_remarks` longtext CHARACTER SET utf8mb4 COMMENT '备注',
  `task_form_url` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '任务页面地址',
  `task_activity` varchar(36) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '环节ID',
  `process_id` varchar(36) CHARACTER SET utf8mb4 NOT NULL COMMENT '流程ID',
  `workspace_id` varchar(50) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '工作空间ID',
  `business_type` varchar(50) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '关联业务类型',
  `business_key` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '关联业务ID',
  `module` varchar(50) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '模块',
  PRIMARY KEY (`task_id`),
  KEY `IDX_TASK_STATUS` (`task_status`) USING BTREE,
  KEY `IDX_TASK_ASSIGNEE` (`task_assignee`) USING BTREE,
  KEY `IDX_PROCESS_ID` (`process_id`) USING BTREE,
  KEY `IDX_BUSINESS_KEY` (`business_key`) USING BTREE,
  KEY `IDX_MODULE` (`module`) USING BTREE,
  KEY `IDX_WORKSPACE_ID` (`workspace_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


CREATE TABLE IF NOT EXISTS `mc_sys_stats` (
  `id` varchar(50) NOT NULL,
  `stat_key` varchar(50) NOT NULL COMMENT '宿主机Key',
  `stats` longtext NOT NULL COMMENT '监控信息',
  `update_time` bigint(13) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNQ_ID_KEY` (`id`,`stat_key`),
  KEY `IDX_KEY` (`stat_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `license` (
  `id` varchar(50) NOT NULL,
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `license` longtext COMMENT 'license',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `menu_preference` (
  `id` varchar(50) NOT NULL,
  `user_id` varchar(50) DEFAULT NULL COMMENT '用户ID',
  `module_id` varchar(50) DEFAULT NULL COMMENT '模块ID',
  `menu_id` varchar(50) DEFAULT NULL COMMENT '菜单ID',
  `sort` int(4) DEFAULT '100' COMMENT '排序',
  PRIMARY KEY (`id`),
  KEY `IDX_USER` (`user_id`),
  KEY `IDX_MENU` (`menu_id`),
  KEY `IDX_MODULE` (`module_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单收藏表';

CREATE TABLE IF NOT EXISTS `module` (
  `id` varchar(50) NOT NULL COMMENT '模块ID',
  `name` varchar(50) NOT NULL COMMENT '模块名称',
  `type` varchar(20) DEFAULT NULL COMMENT '模块类型',
  `license` varchar(50) DEFAULT NULL,
  `auth` tinyint(1) DEFAULT '1',
  `summary` varchar(128) DEFAULT NULL COMMENT '模块概要',
  `module_url` varchar(255) DEFAULT NULL COMMENT '模块跳转URL',
  `port` bigint(10) DEFAULT NULL COMMENT '模块端口',
  `status` varchar(20) DEFAULT NULL COMMENT '模块状态',
  `active` tinyint(1) DEFAULT '1' COMMENT '是否启用',
  `icon` varchar(255) DEFAULT 'link' COMMENT '模块icon',
  `sort` int(10) DEFAULT '0' COMMENT '排序',
  `open` varchar(20) DEFAULT 'current' COMMENT '模块打开方式',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `ext1` varchar(255) DEFAULT NULL,
  `ext2` varchar(255) DEFAULT NULL COMMENT '是否授权和license ',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

ALTER TABLE `module`
  ADD COLUMN `version` VARCHAR(100) NULL  COMMENT '版本' AFTER `open`;


CREATE TABLE IF NOT EXISTS `notification` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `type` varchar(30) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '通知类型',
  `receiver` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '接收人',
  `title` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '标题',
  `content` longtext CHARACTER SET utf8mb4 COMMENT '内容',
  `status` varchar(30) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '状态',
  `create_time` bigint(13) DEFAULT NULL COMMENT '更新时间',
  `uuid` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_RECEIVER` (`receiver`) USING BTREE,
  KEY `IDX_UUID` (`uuid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `organization` (
  `id` varchar(50) NOT NULL COMMENT '组织 id',
  `name` varchar(64) NOT NULL COMMENT '组织名称',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `create_time` bigint(13) NOT NULL COMMENT '创建时间时间戳',
  `pid` varchar(50) DEFAULT NULL COMMENT '上级组织id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNIQUE_NAME` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS `operation_log` (
  `id` varchar(64) NOT NULL,
  `workspace_id` varchar(64) NOT NULL DEFAULT '' COMMENT '工作空间ID',
  `workspace_name` varchar(100) NOT NULL DEFAULT '' COMMENT '工作空间名称',
  `resource_user_id` varchar(64) NOT NULL DEFAULT '' COMMENT '资源拥有者ID',
  `resource_user_name` varchar(100) NOT NULL DEFAULT '' COMMENT '资源拥有者名称',
  `resource_type` varchar(45) NOT NULL DEFAULT '' COMMENT '资源类型',
  `resource_id` varchar(64) DEFAULT NULL COMMENT '资源ID',
  `resource_name` VARCHAR(128) CHARSET utf8mb4 COLLATE utf8mb4_general_ci NULL  COMMENT '资源名称',
  `operation` varchar(45) NOT NULL DEFAULT '' COMMENT '操作',
  `time` bigint(13) NOT NULL COMMENT '操作时间',
  `message` mediumtext COMMENT '操作信息',
  `module` varchar(20) DEFAULT 'management-center' COMMENT '模块',
  `source_ip` varchar(15) DEFAULT NULL COMMENT '操作方IP',
  PRIMARY KEY (`id`),
  KEY `IDX_OWNER_ID` (`workspace_id`),
  KEY `IDX_USER_ID` (`resource_user_id`),
  KEY `IDX_OP` (`operation`),
  KEY `IDX_RES_ID` (`resource_id`),
  KEY `IDX_RES_NAME` (`resource_name`),
  KEY `IDX_USER_NAME` (`resource_user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

alter table operation_log modify module varchar(50) default 'management-center' null comment '模块';

CREATE TABLE IF NOT EXISTS `plugin` (
  `id` varchar(50) NOT NULL COMMENT '插件ID',
  `name` varchar(100) NOT NULL COMMENT '名称',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `version` varchar(45) NOT NULL COMMENT '插件版本',
  `owner` varchar(45) NOT NULL COMMENT '作者',
  `status` varchar(45) DEFAULT NULL COMMENT '状态',
  `current_file` mediumtext NOT NULL COMMENT '插件地址',
  `plugin_type` varchar(45) NOT NULL COMMENT '插件类型',
  `icon` mediumtext,
  `update_time` bigint(13) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `IDX_PLUGIN_TYPE` (`plugin_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

CREATE TABLE IF NOT EXISTS `role` (
    `id` varchar(50) CHARACTER SET utf8mb4 NOT NULL COMMENT 'id',
  `name` varchar(50) CHARACTER SET utf8mb4 NOT NULL COMMENT '名称',
  `description` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '描述',
  `type` varchar(20) CHARACTER SET utf8mb4 NOT NULL COMMENT '类型 original/inherit/custom',
  `parent_id` varchar(50) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '父类角色 ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNQ_NAME` (`name`),
  KEY `IDX_PARENT` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `role_permission` (
  `id` varchar(50) NOT NULL,
  `role_id` varchar(50) NOT NULL COMMENT '角色ID',
  `permission_id` varchar(128) NOT NULL COMMENT '权限ID',
  `module_id` varchar(50) NOT NULL COMMENT '模块ID',
  PRIMARY KEY (`id`),
  KEY `UNIQUE_RP` (`role_id`,`permission_id`),
  KEY `IDX_MODULE` (`module_id`),
  KEY `INX_ROLE` (`role_id`),
  KEY `IDX_PERMISSION` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `system_parameter` (
  `param_key` varchar(64) CHARACTER SET utf8mb4 NOT NULL COMMENT '参数名称',
  `param_value` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '参数值',
  `type` varchar(100) DEFAULT 'text' NOT NULL  COMMENT '参数类型',
  `sort` int(5) DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`param_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `tag` (
  `tag_id` varchar(50) NOT NULL,
  `tag_type` varchar(10) NOT NULL COMMENT '标签类型，从云平台同步过来的，或者是系统内创建的',
  `tag_key` varchar(100) DEFAULT NULL COMMENT '标签标识',
  `tag_alias` varchar(100) DEFAULT NULL COMMENT '标签别名',
  `enable` tinyint(1) DEFAULT '1' COMMENT '是否启用',
  `create_time` bigint(13) DEFAULT NULL COMMENT '创建时间',
  `required` tinyint(1) DEFAULT '0' COMMENT '是否必选',
  `scope` varchar(50) NOT NULL DEFAULT 'ADMIN' COMMENT '作用域',
  `resource_id` varchar (64) NOT NULL DEFAULT '' COMMENT 'ws or org id',
  PRIMARY KEY (`tag_id`),
  UNIQUE KEY `IDX_TAG_KEY` (`tag_key`,`scope`,`resource_id`),
  KEY `UNQ_TAGS` (`tag_type`,`tag_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `tag_mapping` (
  `id` varchar(50) NOT NULL,
  `resource_id` varchar(64) NOT NULL DEFAULT '' COMMENT '资源ID',
  `resource_type` varchar(20) NOT NULL DEFAULT '' COMMENT '资源类型',
  `tag_key` varchar(50) NOT NULL DEFAULT '' COMMENT '标签标识',
  `tag_value_id` varchar(64) DEFAULT NULL COMMENT '标签值ID',
  `create_time` bigint(13) NOT NULL COMMENT '创建时间',
  `tag_id` varchar (50) NOT NULL DEFAULT '' COMMENT '标签ID',
  PRIMARY KEY (`id`),
  KEY `IDX_RES_ID` (`resource_id`),
  KEY `IDX_RES_TYPE` (`resource_type`),
  KEY `IDX_TAG_KEY` (`tag_key`),
  KEY `IDX_TAG_VAL` (`tag_value_id`),
  KEY `IDX_RESOURCE_TYPE_ID` (`resource_id`,`resource_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `tag_value` (
  `id` varchar(64) NOT NULL,
  `tag_key` varchar(100) DEFAULT NULL COMMENT '标签标识',
  `tag_value` varchar(100) DEFAULT NULL COMMENT '标签值',
  `tag_value_alias` varchar(100) DEFAULT NULL COMMENT '标签值别名',
  `create_time` bigint(13) DEFAULT NULL COMMENT '创建时间',
  `tag_id` varchar(50) NOT NULL DEFAULT '' COMMENT '标签ID',
  PRIMARY KEY (`id`),
  KEY `IDX_ALIAS_KEY` (`tag_key`),
  KEY `IDX_ALIAS_VALUE` (`tag_value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `user` (
  `id` varchar(50) CHARACTER SET utf8mb4 NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '用户名',
  `email` varchar(100) COLLATE utf8mb4_bin DEFAULT '' COMMENT '邮箱',
  `source` varchar(20) COLLATE utf8mb4_bin NOT NULL DEFAULT 'local' COMMENT '用户类型',
  `password` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '用户密码',
  `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '用户状态',
  `phone` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '电话',
  `lang` varchar(20) NULL COMMENT '语言' ,
  `create_time` bigint(13) NOT NULL COMMENT '创建时间',
  `last_source_id` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '上一次访问资源ID',
  PRIMARY KEY (`id`),
  KEY `IDX_NAME` (`name`),
  KEY `IDX_EMAIL` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE IF NOT EXISTS `user_key` (
  `id` varchar(50) NOT NULL DEFAULT '' COMMENT 'user_key ID',
  `user_id` varchar(50) NOT NULL COMMENT '用户ID',
  `access_key` varchar(50) NOT NULL COMMENT 'access_key',
  `secret_key` varchar(50) NOT NULL COMMENT 'secret key',
  `create_time` bigint(13) NOT NULL COMMENT '创建时间',
  `status` varchar(10) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_AK` (`access_key`),
  KEY `IDX_USER_ID` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `user_role` (
  `id` varchar(50) NOT NULL COMMENT 'id',
  `user_id` varchar(50) NOT NULL COMMENT '用户 id',
  `role_id` varchar(50) NOT NULL COMMENT '角色 id',
  `source_id` varchar(50) DEFAULT NULL COMMENT '资源 id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNIQUE_UR` (`user_id`,`role_id`,`source_id`),
  KEY `IDX_USER_ID` (`user_id`),
  KEY `IDX_ROLE_ID` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `workspace` (
  `id` varchar(50) CHARACTER SET utf8mb4 NOT NULL COMMENT '工作空间ID',
  `organization_id` varchar(50) CHARACTER SET utf8mb4 NOT NULL COMMENT '组织ID',
  `name` varchar(64) CHARACTER SET utf8mb4 NOT NULL COMMENT '工作空间名称',
  `description` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '描述',
  `create_time` bigint(13) NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNQ_WN` (`organization_id`,`name`),
  KEY `IDX_ORG` (`organization_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE i18n (
  `id` varchar(50) NOT NULL COMMENT '模块 ID',
  `web_bundles` longtext NULL COMMENT 'web国际化 json',
  `server_bundles` longtext NULL COMMENT 'server国际化 json',
  `update_time` bigint(13) NULL COMMENT '入库时间',
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `flow_link` (
  `link_id` varchar(50) NOT NULL,
  `link_key` varchar(100) DEFAULT NULL COMMENT '环节标识',
  `link_type` varchar(100) DEFAULT NULL COMMENT '环节类型',
  `link_alias` varchar(100) DEFAULT NULL COMMENT '环节名称',
  `enable` tinyint(1) DEFAULT '1' COMMENT '是否启用',
  `create_time` bigint(13) DEFAULT NULL COMMENT '创建时间',
  `module` varchar(50) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '模块ID',
  PRIMARY KEY (`link_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `flow_link_value` (
  `id` varchar(64) NOT NULL,
  `link_key` varchar(100) DEFAULT NULL COMMENT '环节标识',
  `link_value` varchar(100) DEFAULT NULL COMMENT '环节值',
  `link_value_alias` varchar(100) DEFAULT NULL COMMENT '环节值别名',
  `link_value_priority` BIGINT DEFAULT NULL COMMENT '优先级',
  `assignee_type` varchar(100) DEFAULT NULL COMMENT '环节处理人类型',
  `assignee_value` mediumtext DEFAULT NULL COMMENT '环节处理人',
  `assignee` mediumtext DEFAULT NULL COMMENT '环节处理人',
  `create_time` bigint(13) DEFAULT NULL COMMENT '创建时间',
  `permission_mode` varchar(20) DEFAULT NULL COMMENT '模式',
  `module` varchar(50) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '模块ID',
  PRIMARY KEY (`id`),
  KEY `IDX_ALIAS_KEY` (`link_key`),
  KEY `IDX_ALIAS_VALUE` (`link_value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `flow_link_value_scope` (
  `id` varchar(50) NOT NULL,
  `link_value_id` varchar(50) NOT NULL DEFAULT '' COMMENT '环节项ID',
  `workspace_id` varchar(50) NOT NULL DEFAULT '' COMMENT '工作空间ID',
  `module` varchar(50) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '模块ID',
  `type` varchar(50) DEFAULT NULL COMMENT '类型',
  PRIMARY KEY (`id`),
  KEY `IDX_PG_ID` (`link_value_id`),
  KEY `IDX_G_ID` (`workspace_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


ALTER TABLE `cloud_image`
  ADD  INDEX `IDX_LAST_SYC` (`last_sync_timestamp`);

ALTER TABLE `cloud_host`
  ADD  INDEX `IDX_LAST_SYC` (`last_sync_timestamp`);

ALTER TABLE `cloud_disk`
  ADD  INDEX `IDX_LAST_SYNC` (`last_sync_timestamp`);

ALTER TABLE `cloud_datastore`
  ADD  INDEX `IDX_LAST_SYNC` (`last_sync_timestamp`);

ALTER TABLE `cloud_cluster`
  ADD  INDEX `IDX_LAST_SYNC` (`last_sync_timestamp`);

