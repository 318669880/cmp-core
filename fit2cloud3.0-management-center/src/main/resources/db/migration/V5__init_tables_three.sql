CREATE TABLE IF NOT EXISTS `model_manager` (
  `model_address` varchar(255) NOT NULL,
  `status` int(10) DEFAULT NULL,
  `type` int(10) DEFAULT NULL,
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