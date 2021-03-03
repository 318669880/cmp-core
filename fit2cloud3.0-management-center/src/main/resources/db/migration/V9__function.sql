DELIMITER $$
DROP FUNCTION IF EXISTS GET_ROOT_ORG_ID$$
CREATE  FUNCTION `GET_ROOT_ORG_ID`(org_id VARCHAR(200)) RETURNS varchar(200) CHARSET utf8mb4
READS SQL DATA
BEGIN
	DECLARE rootid VARCHAR(200);
	DECLARE sid VARCHAR(200);
	SET sid = org_id;
		WHILE sid IS NOT NULL DO
			SET rootid = sid;
			SELECT pid INTO sid from organization where id = sid;
		END WHILE;
	return rootid;
END $$
DELIMITER ;
