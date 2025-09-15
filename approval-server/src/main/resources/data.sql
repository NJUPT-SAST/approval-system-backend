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

-- 插入多条比赛数据
INSERT IGNORE INTO `competition` (
    `cover`, `introduce`, `is_review`, `max_team_members`, `min_team_members`,
    `user_code`, `name`, `reg_begin_time`, `reg_end_time`, `review_begin_time`,
    `review_end_time`, `review_settings`, `submit_begin_time`, `submit_end_time`,
    `table`, `type`, `create_user`, `update_user`
) VALUES
-- 团队赛 - 已审批
(
    'https://example.com/covers/programming-contest.jpg',
    '年度编程大赛，面向全校学生开放，旨在培养学生的编程能力和团队协作精神。比赛将评选出最佳算法实现、最佳界面设计和最佳创新项目。',
    1, 5, 3, 'admin', '2024年度编程大赛',
    '2024-03-01 00:00:00', '2024-03-31 23:59:59',
    '2024-04-01 00:00:00', '2024-04-15 23:59:59',
    '{"初级评审": "技术评审", "高级评审": "综合评审", "终审": "评委会"}',
    '2024-03-15 00:00:00', '2024-04-10 23:59:59',
    '{"fields": ["项目名称", "项目描述", "技术栈", "团队成员", "项目亮点"]}', 0, 1, 1
),
-- 个人赛 - 已审批
(
    'https://example.com/covers/design-contest.jpg',
    '校园平面设计大赛，鼓励学生发挥创意，设计出具有视觉冲击力和传播力的平面作品。主题为"校园生活"。',
    1, 1, 1, 'review', '校园平面设计大赛',
    '2024-04-01 00:00:00', '2024-04-20 23:59:59',
    '2024-04-21 00:00:00', '2024-04-30 23:59:59',
    '{"设计评审": "创意与美学", "技术评审": "制作工艺"}',
    '2024-04-15 00:00:00', '2024-04-25 23:59:59',
    '{"fields": ["作品名称", "设计理念", "使用工具", "作品格式"]}', 1, 1, 1
),
-- 团队赛 - 未审批
(
    'https://example.com/covers/business-plan.jpg',
    '大学生创业计划大赛，鼓励学生团队提出创新的商业想法，并制定完整的商业计划书。获奖团队将获得创业基金支持。',
    0, 6, 2, 'judge', '大学生创业计划大赛',
    '2024-05-01 00:00:00', '2024-05-31 23:59:59',
    '2024-06-01 00:00:00', '2024-06-15 23:59:59',
    '{"商业评审": "商业模式", "技术评审": "可行性", "投资评审": "市场潜力"}',
    '2024-05-20 00:00:00', '2024-06-10 23:59:59',
    '{"fields": ["项目名称", "商业模式", "市场分析", "团队介绍", "资金需求"]}', 0, 1, 1
),
-- 个人赛 - 进行中
(
    'https://example.com/covers/essay-contest.jpg',
    '校园征文比赛，主题为"我的大学故事"。鼓励学生分享自己在大学期间的成长经历和感悟。',
    1, 1, 1, 'student', '校园征文比赛',
    '2024-04-10 00:00:00', '2024-05-10 23:59:59',
    '2024-05-11 00:00:00', '2024-05-20 23:59:59',
    '{"文学评审": "文笔与结构", "内容评审": "思想深度"}',
    '2024-05-01 00:00:00', '2024-05-15 23:59:59',
    '{"fields": ["文章标题", "文章内容", "作者简介"]}', 1, 1, 1
),
-- 团队赛 - 已结束
(
    'https://example.com/covers/app-development.jpg',
    '移动应用开发大赛，鼓励学生团队开发解决实际问题的移动应用。评选标准包括创新性、实用性和技术实现。',
    1, 4, 2, 'admin', '移动应用开发大赛',
    '2024-01-15 00:00:00', '2024-02-15 23:59:59',
    '2024-02-16 00:00:00', '2024-02-28 23:59:59',
    '{"技术评审": "代码质量", "设计评审": "用户体验", "创新评审": "创意价值"}',
    '2024-02-01 00:00:00', '2024-02-20 23:59:59',
    '{"fields": ["应用名称", "解决的问题", "目标用户", "技术架构", "特色功能"]}', 0, 1, 1
),
-- 个人赛 - 未来赛事
(
    'https://example.com/covers/photography-contest.jpg',
    '校园摄影大赛，主题为"四季校园"。捕捉校园四季变化的美景，展现校园的自然与人文风貌。',
    0, 1, 1, 'review', '校园摄影大赛',
    '2024-09-01 00:00:00', '2024-09-30 23:59:59',
    '2024-10-01 00:00:00', '2024-10-10 23:59:59',
    '{"技术评审": "摄影技巧", "艺术评审": "构图与美感", "主题评审": "主题表达"}',
    '2024-09-15 00:00:00', '2024-10-05 23:59:59',
    '{"fields": ["作品标题", "拍摄设备", "拍摄地点", "创作理念"]}', 1, 1, 1
);