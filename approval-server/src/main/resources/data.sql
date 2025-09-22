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





