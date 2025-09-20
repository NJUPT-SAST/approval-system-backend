-- 插入测试数据脚本
-- 此脚本用于在review数据库中插入测试数据，支持apifox测试表单功能

-- 连接到review数据库
USE review;

-- 步骤1: 确保有可用的用户数据
-- 如果用户表已有数据，可跳过此步骤
-- 如果需要，可以插入新的测试用户
INSERT IGNORE INTO `user` (
    `dep_id`, 
    `code`, 
    `password`, 
    `name`, 
    `role`, 
    `salt`, 
    `create_time`, 
    `update_time`, 
    `create_user`, 
    `update_user`, 
    `major`, 
    `contact`
) VALUES (
    '1', 
    'test_student_1', 
    MD5(CONCAT('123456', 'test_salt_2025')), 
    '测试学生1', 
    0, 
    'test_salt_2025', 
    NOW(), 
    NOW(), 
    1, 
    1, 
    '计算机科学与技术', 
    '13800138000'
);

-- 步骤2: 插入一个测试比赛（团队赛）
INSERT IGNORE INTO `competition` (
    `cover`, 
    `introduce`, 
    `is_review`, 
    `max_team_members`, 
    `min_team_members`, 
    `name`, 
    `reg_begin_time`, 
    `reg_end_time`, 
    `review_begin_time`, 
    `review_end_time`, 
    `submit_begin_time`, 
    `submit_end_time`, 
    `table`, 
    `type`, 
    `user_code`, 
    `create_time`, 
    `update_time`, 
    `create_user`, 
    `update_user`
) VALUES (
    'https://example.com/cover.jpg', 
    '这是一个测试比赛，用于测试表单功能', 
    0, -- 已审批
    5, -- 最大团队人数
    2, -- 最小团队人数
    '测试比赛-团队赛', 
    '2024-01-01 00:00:00', 
    '2024-12-31 23:59:59', 
    '2025-01-01 00:00:00', 
    '2025-01-15 23:59:59', 
    '2024-11-01 00:00:00', 
    '2024-11-30 23:59:59', 
    '{"title":"团队赛报名表","fields":[{"name":"teamName","type":"string","required":true,"label":"团队名称"},{"name":"members","type":"array","required":true,"label":"团队成员","items":{"type":"object","properties":{"code":"string","name":"string"}}},{"name":"teachers","type":"array","required":false,"label":"指导老师","items":{"type":"object","properties":{"name":"string","school":"string"}}}]}', 
    0, -- 团队赛
    1, 
    NOW(), 
    NOW(), 
    1, 
    1
);

-- 步骤3: 插入一个测试比赛（个人赛）
INSERT IGNORE INTO `competition` (
    `cover`, 
    `introduce`, 
    `is_review`, 
    `max_team_members`, 
    `min_team_members`, 
    `name`, 
    `reg_begin_time`, 
    `reg_end_time`, 
    `review_begin_time`, 
    `review_end_time`, 
    `submit_begin_time`, 
    `submit_end_time`, 
    `table`, 
    `type`, 
    `user_code`, 
    `create_time`, 
    `update_time`, 
    `create_user`, 
    `update_user`
) VALUES (
    'https://example.com/cover2.jpg', 
    '这是一个测试比赛，用于测试表单功能', 
    0, -- 已审批
    1, -- 最大团队人数
    1, -- 最小团队人数
    '测试比赛-个人赛', 
    '2024-01-01 00:00:00', 
    '2024-12-31 23:59:59', 
    '2025-01-01 00:00:00', 
    '2025-01-15 23:59:59', 
    '2024-11-01 00:00:00', 
    '2024-11-30 23:59:59', 
    '{"title":"个人赛报名表","fields":[{"name":"personalInfo","type":"object","required":true,"label":"个人信息","properties":{"studentId":"string","name":"string","class":"string"}}]}', 
    1, -- 个人赛
    1, 
    NOW(), 
    NOW(), 
    1, 
    1
);

-- 步骤4: 查询插入的比赛ID，以便在测试中使用
SELECT id, name, type FROM `competition` WHERE name LIKE '测试比赛%';

-- 步骤5: 插入一个已报名的测试团队（可选，用于测试查看已有报名数据）
-- 注意：这里的com_id需要替换为上面查询到的团队赛ID
-- INSERT INTO `team` (
--     `com_id`, 
--     `name`, 
--     `captain`, 
--     `member`, 
--     `teacher`, 
--     `create_time`, 
--     `update_time`, 
--     `create_user`, 
--     `update_user`
-- ) VALUES (
--     1, -- 替换为实际的比赛ID
--     '测试团队1', 
--     'test_student_1', 
--     '[{"code":"student002","name":"学生2"},{"code":"student003","name":"学生3"}]', 
--     '[{"name":"张老师","school":"计算机学院"}]', 
--     NOW(), 
--     NOW(), 
--     1, 
--     1
-- );

-- 使用说明：
-- 1. 执行此脚本后，数据库中将包含两个测试比赛（一个团队赛，一个个人赛）
-- 2. 记下查询结果中的比赛ID，在apifox测试时使用
-- 3. 使用test_student_1/123456或已有的学生账号登录系统
-- 4. 可以使用这些数据进行报名、查看表单等接口测试