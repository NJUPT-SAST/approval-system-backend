-- 插入默认管理员用户
SET @admin_salt = REPLACE(UUID(), '-', '');
INSERT IGNORE INTO `user` (`code`, `password`, `name`, `role`, `salt`, `create_time`, `update_time`)
VALUES ('admin', MD5(CONCAT('admin', @admin_salt)), 'admin', 3, @admin_salt, NOW(), NOW());

-- 随便插个文件罢
INSERT IGNORE INTO `file` (`id`, `com_id`, `user_code`, `input`, `url`)
VALUES (1, 1, '1', '1', 'list/list2/text2.txt');
