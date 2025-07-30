-- insert initial user into the database
SET @admin_salt = REPLACE(UUID(), '-', '');
INSERT IGNORE INTO `user` (`dep_id`, `code`, `password`, `name`, `role`, `salt`, `create_time`, `update_time`, `create_user`, `update_user`)
VALUES ('0', 'admin', MD5(CONCAT('admin', @admin_salt)), 'admin', 3, @admin_salt, NOW(), NOW(), 1, 1);

-- insert initial notices into the database
INSERT IGNORE INTO `notice` (`com_id`, `content`, `role`, `time`, `title`, `create_time`, `update_time`, `create_user`, `update_user`)
VALUES
(1, '欢迎大家参加本次活动，请按时到达指定地点。活动将于明天上午9点正式开始，请大家准时参加。', 1, '2024-01-01 10:30:00', '活动通知 - 已发布', NOW(), NOW(), 1, 1),
(2, '关于下周学术讲座的安排通知，具体时间和地点将另行通知，请大家关注后续公告。', 1, '2099-12-31 10:30:00', '学术讲座通知 - 草稿', NOW(), NOW(), 1, 1);

-- insert initial file into the database
INSERT IGNORE INTO `file` (`id`, `com_id`, `user_code`, `input`, `url`)
VALUES (1, 1, '1', '1', 'list/list2/text2.txt');
