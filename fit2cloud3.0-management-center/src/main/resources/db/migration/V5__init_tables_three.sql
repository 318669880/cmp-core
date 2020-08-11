CREATE TABLE IF NOT EXISTS `model_manager` (
  `model_address` varchar(255) NOT NULL,
  `on_line` tinyint(1) NOT NULL,
  `env` varchar(100) NOT NULL,
  `validate` int(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS  `model_basic` (
  `model_uuid` varchar(64) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `icon` varchar(200) DEFAULT NULL,
  `last_revision` varchar(50) DEFAULT NULL,
  `module` varchar(100) DEFAULT NULL,
  `overview` varchar(255) DEFAULT NULL,
  `summary` varchar(255) DEFAULT NULL,
  `current_status` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`model_uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `model_version` (
  `model_version_uuid` varchar(64) NOT NULL,
  `created` bigint(13) DEFAULT NULL,
  `desctiption` varchar(255) DEFAULT NULL,
  `download_url` varchar(100) NOT NULL,
  `revision` varchar(10) NOT NULL,
  `model_basic_uuid` varchar(64) NOT NULL,
  `install_time` bigint(13) NOT NULL,
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