-- insert initial user into the database
-- 生成 salt
SET @admin_salt = REPLACE(UUID(), '-', '');
SET @tourist_salt = REPLACE(UUID(), '-', '');
SET @student_salt = REPLACE(UUID(), '-', '');
SET @review_salt = REPLACE(UUID(), '-', '');
SET @judge_salt = REPLACE(UUID(), '-', '');

-- 插入用户
INSERT IGNORE INTO `user` (`dep_id`, `code`, `password`, `name`, `role`, `salt`,
                           `create_time`, `update_time`, `create_user`, `update_user`,
                           `major`, `contact`)
VALUES
-- 管理员
('0', 'admin', MD5(CONCAT('admin', @admin_salt)), 'admin', 3, @admin_salt, NOW(), NOW(), 1, 1, 'txgc', '1112232'),

-- 游客
('0', 'tourist', MD5(CONCAT('tourist', @tourist_salt)), 'tourist', -1, @tourist_salt, NOW(), NOW(), 1, 1, 'txgc',
 '1112232'),

-- 学生
('0', 'student', MD5(CONCAT('student', @student_salt)), 'student', 0, @student_salt, NOW(), NOW(), 1, 1, 'txgc',
 '1112232'),

-- 评审
('0', 'review', MD5(CONCAT('review', @review_salt)), 'review', 1, @review_salt, NOW(), NOW(), 1, 1, 'txgc', '1112232'),

-- 裁判
('0', 'judge', MD5(CONCAT('judge', @judge_salt)), 'judge', 2, @judge_salt, NOW(), NOW(), 1, 1, 'txgc', '1112232');

-- 插入比赛数据
INSERT IGNORE INTO `competition` (`id`, `cover`, `introduce`, `is_review`, `max_team_members`, `min_team_members`,
                                `name`, `reg_begin_time`, `reg_end_time`, `review_begin_time`, `review_end_time`,
                                `review_settings`, `submit_begin_time`, `submit_end_time`, `table`, `type`,
                                `user_code`, `create_time`, `update_time`, `create_user`, `update_user`)
VALUES 
-- 已审批的团队比赛
(1, 'https://example.com/cover1.jpg', '这是一个面向全校学生的科技创新大赛，旨在激发学生的创新思维和实践能力。比赛分为初赛、复赛和决赛三个阶段，将邀请行业专家担任评委。', 
 0, 5, 2, '校园科技创新大赛', '2024-01-01 00:00:00', '2024-02-15 23:59:59', 
 '2024-02-20 00:00:00', '2024-02-28 23:59:59', NULL, 
 '2024-03-01 00:00:00', '2024-03-15 23:59:59', '{"fields": [{"name": "teamName", "label": "团队名称", "type": "text", "required": true}, {"name": "projectDesc", "label": "项目描述", "type": "textarea", "required": true}]}', 
 0, 1, NOW(), NOW(), 1, 1),

-- 已审批的个人比赛
(2, 'https://example.com/cover2.jpg', '这是一个编程技能挑战赛，考察参赛者的算法设计和编程能力。比赛采用在线编程的形式，题目难度从基础到高级不等。', 
 0, 1, 1, '程序设计挑战赛', '2024-01-10 00:00:00', '2024-03-01 23:59:59', 
 '2024-03-05 00:00:00', '2024-03-10 23:59:59', NULL, 
 '2024-03-15 00:00:00', '2024-03-20 23:59:59', '{"fields": [{"name": "githubUrl", "label": "代码仓库地址", "type": "text", "required": true}, {"name": "programDesc", "label": "程序说明", "type": "textarea", "required": true}]}', 
 1, 1, NOW(), NOW(), 1, 1),

-- 未审批的团队比赛
(3, 'https://example.com/cover3.jpg', '这是一个大数据分析竞赛，参赛者需要利用提供的数据集完成指定的分析任务。比赛将评估分析方法的创新性、结果的准确性和可视化效果。', 
 1, 4, 2, '大数据分析竞赛', '2024-02-01 00:00:00', '2024-03-15 23:59:59', 
 '2024-03-20 00:00:00', '2024-03-25 23:59:59', NULL, 
 '2024-04-01 00:00:00', '2024-04-10 23:59:59', '{"fields": [{"name": "teamName", "label": "团队名称", "type": "text", "required": true}, {"name": "analysisReport", "label": "分析报告", "type": "file", "required": true}]}', 
 0, 1, NOW(), NOW(), 1, 1),

-- 即将开始的团队比赛
(4, 'https://example.com/cover4.jpg', '这是一个人工智能应用创意大赛，鼓励参赛者将AI技术应用到实际问题中。比赛将重点考察项目的创新性、技术实现和应用价值。', 
 0, 6, 3, 'AI应用创意大赛', '2024-03-01 00:00:00', '2024-04-15 23:59:59', 
 '2024-04-20 00:00:00', '2024-04-30 23:59:59', NULL, 
 '2024-05-01 00:00:00', '2024-05-15 23:59:59', '{"fields": [{"name": "teamName", "label": "团队名称", "type": "text", "required": true}, {"name": "demoVideo", "label": "演示视频", "type": "file", "required": true}, {"name": "aiMethod", "label": "AI方法说明", "type": "textarea", "required": true}]}', 
 0, 1, NOW(), NOW(), 1, 1);

-- insert initial notices into the database
INSERT IGNORE INTO `notice` (`id`, `com_id`, `content`, `role`, `time`, `title`, `create_time`, `update_time`,
                             `create_user`, `update_user`)
VALUES ('1', 1, '欢迎大家参加本次活动，请按时到达指定地点。活动将于明天上午9点正式开始，请大家准时参加。', 1,
        '2024-01-01 10:30:00', '活动通知 - 已发布', NOW(), NOW(), 1, 1),
       ('2', 2, '关于下周学术讲座的安排通知，具体时间和地点将另行通知，请大家关注后续公告。', 1, '2099-12-31 10:30:00',
        '学术讲座通知 - 草稿', NOW(), NOW(), 1, 1),
       ('3', 1, '面向学生的报名提醒，请在本周内完成报名手续，逾期将无法参加。', 0, '2024-01-01 10:30:00',
        '学生报名提醒 - 已发布', NOW(), NOW(), 1, 1);

-- insert initial file into the database
INSERT IGNORE INTO `file` (`id`, `com_id`, `user_code`, `input`, `url`)
VALUES (1, 1, 'student', '1', 'list/list2/text2.txt');
INSERT IGNORE INTO `file` (`id`, `com_id`, `user_code`, `input`, `url`)
VALUES (2, 1, 'admin', '1', '文本.txt');

-- 插入评委数据 (关联用户表中的judge用户)
INSERT IGNORE INTO `judge` (`com_id`, `user_id`, `create_time`, `update_time`, `create_user`, `update_user`)
SELECT 1  AS com_id,
       id AS user_id,
       NOW(),
       NOW(),
       1,
       1
FROM `user`
WHERE `code` = 'judge';

-- 插入评审数据 (关联比赛、用户和评委)
INSERT IGNORE INTO `review` (`id`, `accept`, `com_id`, `code`, `judge_id`, `opinion`, `user_id`, `create_time`,
                             `update_time`, `create_user`, `update_user`)
SELECT UUID()                                           AS id,
       1                                                AS accept,
       1                                                AS com_id,
       'student'                                        AS code,
       j.id                                             AS judge_id,
       '作品创意很好，技术实现完整，建议通过'             AS opinion,
       (SELECT id FROM `user` WHERE `code` = 'student') AS user_id,
       NOW(),
       NOW(),
       1,
       1
FROM `judge` j
WHERE j.user_id = (SELECT id FROM `user` WHERE `code` = 'judge')
LIMIT 1;

-- 插入评分数据 (关联比赛、评委和用户)
INSERT IGNORE INTO `score` (`id`, `com_id`, `judge_id`, `option`, `score`, `user_id`, `create_time`, `update_time`,
                            `create_user`, `update_user`)
SELECT UUID()                           AS id,
       1                                AS com_id,
       j.id                             AS judge_id,
       '技术实现完整，创意独特，表现优秀' AS `option`,
       95                               AS score,
       'student'                        AS user_id,
       NOW(),
       NOW(),
       1,
       1
FROM `judge` j
WHERE j.user_id = (SELECT id FROM `user` WHERE `code` = 'judge')
LIMIT 1;

-- 插入队伍数据 (关联比赛和队长)
INSERT IGNORE INTO `team` (`com_id`, `name`, `captain`, `member`, `teacher`, `create_time`, `update_time`,
                           `create_user`, `update_user`)
VALUES (1, '创新团队', 'student', '["member1", "member2"]', '["teacher1", "teacher2"]', NOW(), NOW(), 1, 1),
       (2, '科研小组', 'admin', '["member3", "member4"]', '["teacher3"]', NOW(), NOW(), 1, 1);

-- 插入作品数据 (关联比赛和队长)
INSERT IGNORE INTO `work` (`com_id`, `user_code`, `work_name`, `schema_content`, `create_time`, `update_time`,
                           `create_user`, `update_user`)
VALUES (1, 'student', '智能识别系统', '{"fields": ["图像识别", "机器学习"]}', NOW(), NOW(), 1, 1),
       (2, 'admin', '数据分析平台', '{"fields": ["大数据", "可视化"]}', NOW(), NOW(), 1, 1);

-- 插入更多公告数据 (关联比赛)
INSERT IGNORE INTO `notice` (`com_id`, `content`, `role`, `time`, `title`, `create_time`, `update_time`, `create_user`,
                             `update_user`)
VALUES (1, '比赛提交截止日期延长至本月底，请各队伍抓紧时间完成作品。', 0, '2024-01-15 14:00:00', '提交截止日期延长通知',
        NOW(), NOW(), 1, 1),
       (2, '评审结果将于下周公布，请各位参赛者关注官方网站通知。', 0, '2024-02-01 09:00:00', '评审结果公布通知', NOW(),
        NOW(), 1, 1),
       (1, '颁奖典礼将于下月举行，请获奖团队准备好展示材料。', 2, '2024-02-15 10:00:00', '颁奖典礼通知', NOW(), NOW(), 1,
        1);

-- 插入更多文件数据 (关联比赛和用户)
INSERT IGNORE INTO `file` (`com_id`, `user_code`, `input`, `url`)
VALUES (1, 'student', '项目报告', 'reports/project_report.pdf'),
       (2, 'admin', '演示文稿', 'presentations/demo.pptx'),
       (1, 'student', '源代码', 'code/main.zip');