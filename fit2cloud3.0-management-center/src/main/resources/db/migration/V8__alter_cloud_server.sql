ALTER TABLE  `cloud_server`
ADD COLUMN `snap_shot` INT(8) NULL DEFAULT 0 COMMENT '快照';

UPDATE cloud_server set snap_shot=0;