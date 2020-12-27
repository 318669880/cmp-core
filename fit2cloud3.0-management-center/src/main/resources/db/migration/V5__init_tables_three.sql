CREATE TABLE IF NOT EXISTS `model_manager` (
  `uuid` varchar(64) NOT NULL COMMENT '模块环境唯一标识',
  `model_address` varchar(255) NOT NULL COMMENT '索引服务域名',
  `on_line` tinyint(1) NOT NULL COMMENT '是否在线服务',
  `env` varchar(100) NOT NULL COMMENT '运行环境',
  `docker_registry` longtext CHARACTER SET utf8mb4 COMMENT 'Docker registry 信息',
  `validate` int(10) DEFAULT NULL COMMENT '验证结果',
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS  `model_basic` (
  `model_uuid` varchar(64) NOT NULL COMMENT '模块唯一标识',
  `name` varchar(100) DEFAULT NULL COMMENT '模块名称',
  `icon` varchar(255) DEFAULT NULL COMMENT '模块图标',
  `last_revision` varchar(50) DEFAULT NULL COMMENT '安装版本',
  `module` varchar(255) DEFAULT NULL COMMENT '模块',
  `overview` varchar(255) DEFAULT NULL COMMENT '概诉',
  `summary` varchar(255) DEFAULT NULL COMMENT '总结',
  `current_status` varchar(50) DEFAULT NULL COMMENT '当前状态',
  `pod_num` int(2) DEFAULT 0 COMMENT 'k8s环境pod数量',
  `custom_data` longtext CHARACTER SET utf8mb4 COMMENT '部署信息',
  PRIMARY KEY (`model_uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `model_version` (
  `model_version_uuid` varchar(64) NOT NULL COMMENT '模块版本唯一标识',
  `created` bigint(13) DEFAULT NULL COMMENT '创建时间',
  `desctiption` varchar(255) DEFAULT NULL COMMENT '描述',
  `download_url` varchar(255) NOT NULL COMMENT '模块下载地址',
  `revision` varchar(10) NOT NULL COMMENT '模块版本',
  `model_basic_uuid` varchar(64) NOT NULL COMMENT '所属模块',
  `install_time` bigint(13) NOT NULL COMMENT '安装时间',
  PRIMARY KEY (`model_version_uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `t_lock` (
  `lock_key` varchar(200) NOT NULL COMMENT '锁唯一标志',
  `request_id` varchar(64) NOT NULL DEFAULT '' COMMENT '用来标识请求对象的',
  `lock_count` int(11) NOT NULL DEFAULT '0' COMMENT '当前上锁次数',
  `timeout` bigint(20) NOT NULL DEFAULT '0' COMMENT '锁超时时间',
  `version` int(11) NOT NULL DEFAULT '0' COMMENT '版本号，每次更新+1',
  PRIMARY KEY (`lock_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='锁信息表';

CREATE TABLE IF NOT EXISTS `model_node` (
  `model_node_uuid` varchar(64) NOT NULL COMMENT '节点主键',
  `model_basic_uuid` varchar(64) DEFAULT NULL COMMENT '所属模块',
  `node_host` varchar(100) NOT NULL COMMENT '节点host',
  `node_ip` varchar(200) DEFAULT NULL COMMENT '节点ip',
  `node_status` varchar(50) DEFAULT NULL COMMENT '节点状态',
  `node_create_time` bigint(13) DEFAULT NULL COMMENT '节点创建时间',
  `is_mc` tinyint(1) NOT NULL COMMENT '是否管理中心',
  `mc_node_uuid` varchar(64) DEFAULT NULL COMMENT '所属管理中心节点',
  `update_time` bigint(13) NOT NULL COMMENT '节点更新时间',
  PRIMARY KEY (`model_node_uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS `config_properties` (
  `id` varchar(100) COLLATE utf8_bin NOT NULL COMMENT '配置唯一ID',
  `confk` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '配置项键',
  `confv` varchar(500) COLLATE utf8_bin DEFAULT NULL COMMENT '配置项值',
  `application` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '配置应用名称',
  `profile` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '配置对应profile',
  `label` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '配置分支',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE DEFINER=`root`@`%` FUNCTION `GET_ROOT_ORG_ID`(org_id VARCHAR(200)) RETURNS varchar(200) CHARSET utf8mb4
BEGIN
	DECLARE rootid VARCHAR(200);
	DECLARE sid VARCHAR(200);
	SET sid = org_id;
		WHILE sid IS NOT NULL DO
			SET rootid = sid;
			SELECT pid INTO sid from organization where id = sid;
		END WHILE;
	return rootid;
END