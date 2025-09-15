CREATE DATABASE IF NOT EXISTS `review`
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE `review`;

-- create user table
CREATE TABLE IF NOT EXISTS `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID编号',
  `dep_id` int(11) DEFAULT NULL COMMENT '部门(学院)编号',
  `name` varchar(50) DEFAULT NULL COMMENT '用户姓名',
  `code` varchar(50) NOT NULL COMMENT '用户学号/代码',
  `password` varchar(100) NOT NULL COMMENT '用户密码',
  `role` int(11) DEFAULT NULL COMMENT '角色：0=学生，1=审批，2=评委，3=管理员',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建用户ID',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新用户ID',
  `salt` varchar(50) NOT NULL COMMENT '密码加盐值',
  `major` varchar(50) NOT NULL COMMENT '专业',
  `contact` varchar(50) NOT NULL COMMENT '手机号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- create file table
CREATE TABLE IF NOT EXISTS `file` (
  `id` int NOT NULL,
  `com_id` int NULL DEFAULT NULL,
  `user_code` varchar(255),
  `input` varchar(255),
  `url` varchar(255),
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT='文件表';

-- create notice table
CREATE TABLE IF NOT EXISTS `notice` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '公告id编号',
  `com_id` int(11) DEFAULT NULL COMMENT '活动id编号',
  `content` text COMMENT '公告内容',
  `role` bigint(20) DEFAULT NULL COMMENT '角色',
  `time` varchar(255) DEFAULT NULL COMMENT '发出公告的时间',
  `title` varchar(255) DEFAULT NULL COMMENT '公告标题',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建用户',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新用户',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- create competition table
CREATE TABLE IF NOT EXISTS `competition` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '活动ID编号',

  `cover` VARCHAR(255) DEFAULT NULL COMMENT '封面url',
  `introduce` TEXT COMMENT '比赛介绍',

  `is_review` TINYINT(1) DEFAULT NULL COMMENT '是否已经审批（0=已审批，1=未审批）',

  `max_team_members` INT(11) DEFAULT NULL COMMENT '团队人数上限',
  `min_team_members` INT(11) DEFAULT NULL COMMENT '团队人数下限',

  `name` VARCHAR(255) NOT NULL COMMENT '比赛名称',

  `reg_begin_time` DATETIME DEFAULT NULL COMMENT '报名开始时间',
  `reg_end_time` DATETIME DEFAULT NULL COMMENT '报名结束时间',

  `review_begin_time` DATETIME DEFAULT NULL COMMENT '评审开始时间',
  `review_end_time` DATETIME DEFAULT NULL COMMENT '评审结束时间',

  `review_settings` TEXT COMMENT '评审配置',

  `submit_begin_time` DATETIME DEFAULT NULL COMMENT '提交开始时间',
  `submit_end_time` DATETIME DEFAULT NULL COMMENT '提交结束时间',

  `table` TEXT COMMENT '表单 schema',

  `type` TINYINT(1) DEFAULT NULL COMMENT '比赛类型（0=团队，1=个人）',

  `user_code` VARCHAR(50) DEFAULT NULL COMMENT '活动负责人学号',

  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

  `create_user` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
  `update_user` VARCHAR(50) DEFAULT NULL COMMENT '更新人',

  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='比赛表';

-- create department table
CREATE TABLE IF NOT EXISTS `department` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '学院id',
  `name` VARCHAR(255) NOT NULL COMMENT '学院名称',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` VARCHAR(255) DEFAULT NULL COMMENT '创建人',
  `update_user` VARCHAR(255) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学院表';

-- create work table
CREATE TABLE IF NOT EXISTS `work` (
    `id` BIGINT AUTO_INCREMENT COMMENT '作品ID',
    `com_id` BIGINT NOT NULL COMMENT '比赛ID',
    `user_code` VARCHAR(50) NOT NULL COMMENT '队长学号',
    `work_name` VARCHAR(255) NOT NULL COMMENT '作品名称',
    `schema_content` TEXT COMMENT '由XRender渲染的表单数据',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `create_user` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `update_user` BIGINT DEFAULT NULL COMMENT '更新人ID',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='参赛作品表';

-- create judge table
CREATE TABLE IF NOT EXISTS `judge` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '评委id',
    `com_id` BIGINT NOT NULL COMMENT '活动id',
    `user_id` INT DEFAULT NULL COMMENT '授权人id',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_user` BIGINT DEFAULT NULL COMMENT '创建人',
    `update_user` BIGINT DEFAULT NULL COMMENT '更新人',
    `judge_code` VARCHAR(64) DEFAULT NULL COMMENT '评委学号',
    `captain_code` VARCHAR(64) DEFAULT NULL COMMENT '队长学号',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评委表';

-- review表中增加userCode
CREATE TABLE IF NOT EXISTS `review` (
    `id` VARCHAR(64) NOT NULL COMMENT '主键ID',
    `com_id` INT(11) DEFAULT NULL COMMENT '活动id',
    `user_id` INT(11) DEFAULT NULL COMMENT '队长id',
    `judge_id` INT(11) DEFAULT NULL COMMENT '审批人id',
    `accept` TINYINT(1) DEFAULT NULL COMMENT '是否通过，true=通过，false=不通过',
    `opinion` VARCHAR(255) DEFAULT NULL COMMENT '审批意见',
    `user_code` VARCHAR(50) DEFAULT NULL COMMENT '队长学号/用户编码',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_user` BIGINT(20) DEFAULT NULL COMMENT '创建用户ID',
    `update_user` BIGINT(20) DEFAULT NULL COMMENT '更新用户ID',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作品审核表';

CREATE TABLE IF NOT EXISTS `team` (
  `com_id` INT NOT NULL COMMENT '比赛ID',
  `name` VARCHAR(100) NOT NULL COMMENT '队伍名称',
  `captain` VARCHAR(20) NOT NULL COMMENT '队长学号',
  `member` JSON NOT NULL COMMENT '队员学号列表',
  `teacher` JSON NOT NULL COMMENT '指导老师列表',
  PRIMARY KEY (`com_id`, `captain`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='参赛队伍表';
