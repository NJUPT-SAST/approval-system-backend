-- 创建用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID编号',
  `dep_id` int(11) DEFAULT NULL COMMENT '部门编号',
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
