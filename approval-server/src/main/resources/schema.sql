-- create user table
CREATE TABLE IF NOT EXISTS `user`
(
    `id`          int(11)      NOT NULL AUTO_INCREMENT COMMENT '用户ID编号',
    `dep_id`      int(11)     DEFAULT NULL COMMENT '部门(学院)编号',
    `name`        varchar(50) DEFAULT NULL COMMENT '用户姓名',
    `code`        varchar(50)  NOT NULL COMMENT '用户学号/代码',
    `password`    varchar(100) NOT NULL COMMENT '用户密码',
    `role`        int(11)     DEFAULT NULL COMMENT '角色：0=学生，1=审批，2=评委，3=管理员',
    `create_time` datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_user` bigint(20)  DEFAULT NULL COMMENT '创建用户ID',
    `update_user` bigint(20)  DEFAULT NULL COMMENT '更新用户ID',
    `salt`        varchar(50)  NOT NULL COMMENT '密码加盐值',
    `major`       varchar(50)  NOT NULL COMMENT '专业',
    `contact`     varchar(50)  NOT NULL COMMENT '手机号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';

-- create file table
CREATE TABLE IF NOT EXISTS `file`
(
    `id`          int NOT NULL,
    `com_id`      int NULL DEFAULT NULL,
    `user_code`   varchar(255),
    `input`       varchar(255),
    `url`         varchar(255),
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_user` BIGINT   DEFAULT NULL COMMENT '创建用户',
    `update_user` BIGINT   DEFAULT NULL COMMENT '更新用户',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4 COMMENT ='文件表';

-- create notice table
CREATE TABLE IF NOT EXISTS `notice`
(
    `id`          int(11) NOT NULL AUTO_INCREMENT COMMENT '公告id编号',
    `com_id`      int(11)      DEFAULT NULL COMMENT '活动id编号',
    `content`     text COMMENT '公告内容',
    `role`        bigint(20)   DEFAULT NULL COMMENT '角色',
    `time`        varchar(255) DEFAULT NULL COMMENT '发出公告的时间',
    `title`       varchar(255) DEFAULT NULL COMMENT '公告标题',
    `create_time` datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_user` bigint(20)   DEFAULT NULL COMMENT '创建用户',
    `update_user` bigint(20)   DEFAULT NULL COMMENT '更新用户',
    `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除（0-未删 1-已删）',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='公告表';

-- create department table
CREATE TABLE IF NOT EXISTS `department`
(
    `id`          int(11)      NOT NULL AUTO_INCREMENT COMMENT '学院id',
    `name`        varchar(255) NOT NULL COMMENT '学院名称',
    `create_time` datetime   DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_user` bigint(20) DEFAULT NULL COMMENT '创建用户',
    `update_user` bigint(20) DEFAULT NULL COMMENT '更新用户',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='学院表';

CREATE TABLE IF NOT EXISTS `judge`
(
    `id`          INT    NOT NULL AUTO_INCREMENT COMMENT '评委ID',
    `com_id`      BIGINT NOT NULL COMMENT '活动ID',
    `user_id`     INT    NOT NULL COMMENT '授权人ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_user` BIGINT   DEFAULT NULL COMMENT '创建用户',
    `update_user` BIGINT   DEFAULT NULL COMMENT '更新用户',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='评委表';

CREATE TABLE IF NOT EXISTS `review`
(
    `id`          VARCHAR(255) NOT NULL COMMENT '评审ID',
    `accept`      TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否通过(0-否,1-是)',
    `com_id`      BIGINT       NOT NULL COMMENT '比赛ID',
    `code`        VARCHAR(255) NOT NULL COMMENT '队长学号',
    `judge_id`    INT          NOT NULL COMMENT '审批人ID',
    `opinion`     TEXT COMMENT '审批意见',
    `user_id`     INT          NOT NULL COMMENT '队长ID',
    `create_time` DATETIME              DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_user` BIGINT                DEFAULT NULL COMMENT '创建用户',
    `update_user` BIGINT                DEFAULT NULL COMMENT '更新用户',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='评审表';

CREATE TABLE IF NOT EXISTS `score`
(
    `id`          VARCHAR(255) NOT NULL COMMENT '评分ID',
    `com_id`      VARCHAR(255) NOT NULL COMMENT '比赛ID编号',
    `judge_id`    INT          NOT NULL COMMENT '评委ID编号',
    `option`      TEXT COMMENT '评审意见',
    `score`       INT      DEFAULT NULL COMMENT '打分',
    `user_id`     VARCHAR(255) NOT NULL COMMENT '团队队长ID或用户ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_user` BIGINT   DEFAULT NULL COMMENT '创建用户',
    `update_user` BIGINT   DEFAULT NULL COMMENT '更新用户',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='评分表';

CREATE TABLE IF NOT EXISTS `team`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '队伍ID',
    `com_id`      BIGINT       NOT NULL COMMENT '比赛ID',
    `name`        VARCHAR(255) DEFAULT NULL COMMENT '队伍名称（仅团队赛）',
    `captain`     VARCHAR(255) NOT NULL COMMENT '队长学号',
    `member`      JSON         DEFAULT NULL COMMENT '成员JSON，不含队长',
    `teacher`     JSON         DEFAULT NULL COMMENT '指导老师JSON',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_user` BIGINT       DEFAULT NULL COMMENT '创建用户',
    `update_user` BIGINT       DEFAULT NULL COMMENT '更新用户',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='队伍表';

CREATE TABLE IF NOT EXISTS `work`
(
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '作品ID',
    `com_id`         BIGINT       NOT NULL COMMENT '比赛ID',
    `user_code`      VARCHAR(255) NOT NULL COMMENT '队长学号',
    `work_name`      VARCHAR(255) NOT NULL COMMENT '作品名称',
    `schema_content` TEXT COMMENT '由XRender渲染的表单数据',
    `create_time`    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_user`    BIGINT   DEFAULT NULL COMMENT '创建用户',
    `update_user`    BIGINT   DEFAULT NULL COMMENT '更新用户',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='作品表';

-- 插入学院数据
INSERT IGNORE INTO `department` (`name`, `create_time`, `update_time`, `create_user`, `update_user`)
VALUES
    ('计算机科学与技术学院', NOW(), NOW(), 1, 1),
    ('电子信息工程学院', NOW(), NOW(), 1, 1),
    ('机械工程与自动化学院', NOW(), NOW(), 1, 1),
    ('经济管理学院', NOW(), NOW(), 1, 1),
    ('外国语学院', NOW(), NOW(), 1, 1),
    ('法学院', NOW(), NOW(), 1, 1),
    ('艺术学院', NOW(), NOW(), 1, 1),
    ('医学院', NOW(), NOW(), 1, 1),
    ('理学院', NOW(), NOW(), 1, 1),
    ('土木工程学院', NOW(), NOW(), 1, 1);