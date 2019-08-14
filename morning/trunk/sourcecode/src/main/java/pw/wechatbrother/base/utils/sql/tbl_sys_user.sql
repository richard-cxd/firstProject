CREATE TABLE yn_user (
  id int(20) NOT NULL AUTO_INCREMENT,
  organization_id int(20) DEFAULT NULL,
  organized varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  username varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  password varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  salt varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  role_ids varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  locked tinyint(1) DEFAULT 0,
  type varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '"用户类型"',
  Live_data varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  status int(10) DEFAULT NULL,
  reg_time datetime DEFAULT NULL,
  last_login_time datetime DEFAULT NULL,
  eid varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '企业ID',
  full_name varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  sex varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  user_type varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  qq varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  email varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  tel varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  phone varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  checkMsg varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '验证码',
  checkMsgTime varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '验证码存入时间',
  created_by bigint(20) DEFAULT NULL,
  created_date datetime DEFAULT NULL,
  enable_tag int(11) DEFAULT NULL,
  remove_tag int(11) DEFAULT NULL,
  last_updated_by bigint(20) DEFAULT NULL,
  last_updated_date datetime DEFAULT NULL,
  key_str varchar(150) DEFAULT NULL,
  lock_str varchar(150) DEFAULT NULL,
  approle int(11) DEFAULT NULL COMMENT 'app登陆类型 1巡检；2，营销；999巡检+营销',
  zoneid int(11) DEFAULT NULL COMMENT '区域ID,如(城北公司)',
  PRIMARY KEY (id),
  INDEX idx_tbl_sys_user_organization_id (organization_id),
  UNIQUE INDEX idx_tbl_sys_user_username (username),
  INDEX usertype0002 (user_type)
)
ENGINE = INNODB
AUTO_INCREMENT = 404
AVG_ROW_LENGTH = 265
CHARACTER SET utf8
COLLATE utf8_general_ci;

SET NAMES 'utf8';

INSERT INTO yn_user(id, organization_id, organized, username, password, salt, role_ids, locked, type, Live_data, status, reg_time, last_login_time, eid, full_name, sex, user_type, qq, email, tel, phone, checkMsg, checkMsgTime, approle, zoneid,remove_tag) VALUES
(1, 1, '1', 'admin', '009b931295aedb014426bec3555422cc', '2776547ed36a21e864a74bcb79f2f138', '1,', 0, '1', NULL, NULL, NULL, NULL, NULL, '系统管理员', 'man', NULL, '99999', '3@1.com', '12312312312', '13527827690', NULL, NULL, NULL, 1,1);