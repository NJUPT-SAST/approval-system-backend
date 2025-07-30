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
