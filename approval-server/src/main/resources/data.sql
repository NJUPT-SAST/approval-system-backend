-- insert initial user into the database
-- 生成 salt
SET @admin_salt = REPLACE(UUID(), '-', '');
SET @tourist_salt = REPLACE(UUID(), '-', '');
SET @student_salt = REPLACE(UUID(), '-', '');
SET @review_salt = REPLACE(UUID(), '-', '');
SET @judge_salt = REPLACE(UUID(), '-', '');

-- 插入用户
INSERT IGNORE INTO `user` (
  `dep_id`, `code`, `password`, `name`, `role`, `salt`,
  `create_time`, `update_time`, `create_user`, `update_user`,
  `major`, `contact`
)
VALUES
-- 管理员
('1', 'admin', MD5(CONCAT('admin', @admin_salt)), 'admin', 3, @admin_salt, NOW(), NOW(), 1, 1, 'txgc', '1112232'),

-- 游客
('2', 'tourist', MD5(CONCAT('tourist', @tourist_salt)), 'tourist', -1, @tourist_salt, NOW(), NOW(), 1, 1, 'txgc', '1112232'),

-- 学生
('3', 'student', MD5(CONCAT('student', @student_salt)), 'student', 0, @student_salt, NOW(), NOW(), 1, 1, 'txgc', '1112232'),

-- 评审
('4', 'review', MD5(CONCAT('review', @review_salt)), 'review', 1, @review_salt, NOW(), NOW(), 1, 1, 'txgc', '1112232'),

-- 裁判
('5', 'judge', MD5(CONCAT('judge', @judge_salt)), 'judge', 2, @judge_salt, NOW(), NOW(), 1, 1, 'txgc', '1112232');


-- insert initial notices into the database
INSERT IGNORE INTO `notice` (`id`, `com_id`, `content`, `role`, `time`, `title`, `create_time`, `update_time`, `create_user`, `update_user`)
VALUES
('1' ,1, '欢迎大家参加本次活动，请按时到达指定地点。活动将于明天上午9点正式开始，请大家准时参加。', 1, '2024-01-01 10:30:00', '活动通知 - 已发布', NOW(), NOW(), 1, 1),
('2' ,2, '关于下周学术讲座的安排通知，具体时间和地点将另行通知，请大家关注后续公告。', 1, '2099-12-31 10:30:00', '学术讲座通知 - 草稿', NOW(), NOW(), 1, 1),
('3' ,1, '面向学生的报名提醒，请在本周内完成报名手续，逾期将无法参加。', 0, '2024-01-01 10:30:00', '学生报名提醒 - 已发布', NOW(), NOW(), 1, 1);

-- insert initial file into the database
INSERT IGNORE INTO `file` (`id`, `com_id`, `user_code`, `input`, `url`)
VALUES (1, 1, 'student', '1', 'list/list2/text2.txt');
INSERT IGNORE INTO `file` (`id`, `com_id`, `user_code`, `input`, `url`)
VALUES (2, 1, 'admin', '1', '文本.txt');

-- insert initial competitions into the database
-- insert or update initial competitions
INSERT INTO `competition` (
  `id`, `cover`, `introduce`, `is_review`, `max_team_members`, `min_team_members`,
  `name`, `reg_begin_time`, `reg_end_time`, `review_begin_time`, `review_end_time`,
  `review_settings`, `submit_begin_time`, `submit_end_time`, `table`, `type`,
  `user_code`, `create_time`, `update_time`, `create_user`, `update_user`
)
VALUES
(1,
 'https://example.com/covers/contest1.png',
 '本次竞赛旨在锻炼同学们的编程能力和团队协作能力。',
 1, 5, 1,
 '程序设计竞赛',
 '2024-01-01 09:00:00', '2024-01-10 23:59:59',
 '2024-01-15 09:00:00', '2024-01-20 23:59:59',
 '{"judge":"code_quality,efficiency,innovation"}',
 '2024-01-11 00:00:00', '2024-01-14 23:59:59',
 '{"fields":[{"name":"作品链接","type":"text"},{"name":"团队简介","type":"textarea"}]}',
 0,
 'admin',
 NOW(), NOW(), 'admin', 'admin'),

(2,
 'https://example.com/covers/contest2.png',
 '通过真实问题的数学建模，培养学生建模和分析问题的能力。',
 1, 3, 1,
 '数学建模大赛',
 '2024-02-01 09:00:00', '2024-02-07 23:59:59',
 '2024-02-10 09:00:00', '2024-02-15 23:59:59',
 '{"judge":"model_accuracy,report_quality,presentation"}',
 '2024-02-08 00:00:00', '2024-02-09 23:59:59',
 '{"fields":[{"name":"建模报告","type":"file"},{"name":"代码附件","type":"file"}]}',
 0,
 'review',
 NOW(), NOW(), 'admin', 'review'),

(3,
 'https://example.com/covers/contest3.png',
 '锻炼学生的英语口语表达能力与临场发挥能力。',
 0, 1, 1,
 '英语演讲比赛',
 '2024-03-01 09:00:00', '2024-03-05 23:59:59',
 '2024-03-06 09:00:00', '2024-03-06 23:59:59',
 '{"judge":"fluency,content,pronunciation"}',
 '2024-03-06 00:00:00', '2024-03-06 23:59:59',
 '{"fields":[{"name":"演讲视频链接","type":"text"}]}',
 1,
 'judge',
 NOW(), NOW(), 'review', 'judge')

ON DUPLICATE KEY UPDATE
  `cover` = VALUES(`cover`),
  `introduce` = VALUES(`introduce`),
  `is_review` = VALUES(`is_review`),
  `max_team_members` = VALUES(`max_team_members`),
  `min_team_members` = VALUES(`min_team_members`),
  `name` = VALUES(`name`),
  `reg_begin_time` = VALUES(`reg_begin_time`),
  `reg_end_time` = VALUES(`reg_end_time`),
  `review_begin_time` = VALUES(`review_begin_time`),
  `review_end_time` = VALUES(`review_end_time`),
  `review_settings` = VALUES(`review_settings`),
  `submit_begin_time` = VALUES(`submit_begin_time`),
  `submit_end_time` = VALUES(`submit_end_time`),
  `table` = VALUES(`table`),
  `type` = VALUES(`type`),
  `user_code` = VALUES(`user_code`),
  `update_time` = NOW(),
  `update_user` = VALUES(`update_user`);


-- 学院表

-- 初始化部门数据
INSERT IGNORE INTO department (id, name, create_user, update_user) VALUES
(1, '计算机学院、软件学院、网络空间安全学院', NULL, NULL),
(2, '通信与信息工程学院', NULL, NULL),
(3, '电子与光学工程学院、柔性电子（未来技术）学院', NULL, NULL),
(4, '集成电路科学与工程学院', NULL, NULL),
(5, '自动化学院、人工智能学院', NULL, NULL),
(6, '材料科学与工程学院', NULL, NULL);

-- 作品表
-- 插入参赛作品数据
INSERT INTO `work` (`id`, `com_id`, `user_code`, `work_name`, `schema_content`, `create_user`, `update_user`)
VALUES
(1, 1, '2025001', '智能家居控制系统', '{"功能":"远程控制家电","技术":"ESP8266"}', 1001, 1001),
(2, 1, '2025002', '校园导航小程序', '{"功能":"地图导航","技术":"微信小程序"}', 1002, 1002),
(3, 1, '2025003', '基于LoRa的环境监测', '{"功能":"空气质量检测","技术":"LoRa+MQTT"}', 1003, 1003),
(4, 2, '2025004', '智能语音助手', '{"功能":"语音交互","技术":"NLP"}', 1004, 1004),
(5, 2, '2025005', '无人机编队控制系统', '{"功能":"路径规划","技术":"ROS"}', 1005, 1005)
ON DUPLICATE KEY UPDATE
  work_name = VALUES(work_name),
  schema_content = VALUES(schema_content),
  update_user = VALUES(update_user),
  update_time = CURRENT_TIMESTAMP;

-- 评委分配表


-- 审批状态表
INSERT IGNORE INTO `review`
(`id`, `com_id`, `user_id`, `judge_id`, `accept`, `opinion`,
 `create_time`, `update_time`, `create_user`, `update_user`)
VALUES
-- 作品1（智能家居控制系统），分配给评委2001
('rev1', 1, 2025001, 2001, 1, '', NOW(), NOW(), 1, 1),

-- 作品2（校园导航小程序），分配给评委2001
('rev2', 1, 2025002, 2001, 1, '', NOW(), NOW(), 1, 1),

-- 作品3（基于LoRa的环境监测），分配给评委2001
('rev3', 1, 2025003, 2001, 0, '', NOW(), NOW(), 1, 1),

-- 作品4（智能语音助手），分配给评委2001
('rev4', 2, 2025004, 2001, 0, '', NOW(), NOW(), 1, 1),

-- 作品5（无人机编队控制系统），分配给评委2001
('rev5', 2, 2025005, 2001, 1, '', NOW(), NOW(), 1, 1);

-- 添加缺失的学生用户
INSERT IGNORE INTO `user` (`dep_id`, `code`, `password`, `name`, `role`, `salt`, `create_time`, `update_time`, `create_user`, `update_user`, `major`, `contact`)
VALUES
-- work 1 队长
('1', '2025001', MD5(CONCAT('2025001','123456')), '学生1', 0, '123456', NOW(), NOW(), 1, 1, 'txgc', '1112232'),
-- work 2 队长
('1', '2025002', MD5(CONCAT('2025002','123456')), '学生2', 0, '123456', NOW(), NOW(), 1, 1, 'txgc', '1112232'),
-- work 3 队长
('1', '2025003', MD5(CONCAT('2025003','123456')), '学生3', 0, '123456', NOW(), NOW(), 1, 1, 'txgc', '1112232'),
-- work 4 队长
('2', '2025004', MD5(CONCAT('2025004','123456')), '学生4', 0, '123456', NOW(), NOW(), 1, 1, 'txgc', '1112232'),
-- work 5 队长
('2', '2025005', MD5(CONCAT('2025005','123456')), '学生5', 0, '123456', NOW(), NOW(), 1, 1, 'txgc', '1112232');


INSERT INTO `team` (com_id, name, captain, member, teacher) VALUES
(1, '智能家居队', 'B21010001', '["B21010011","B21010012"]', '["张老师"]'),
(1, '校园导航队', 'B21010002', '["B21010021","B21010022"]', '["李老师"]'),
(2, '环境监测队', 'B21020001', '["B21020011","B21020012"]', '["王老师"]'),
(2, '无人机队',   'B21020002', '["B21020021","B21020022"]', '["赵老师"]')
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  member = VALUES(member),
  teacher = VALUES(teacher);
