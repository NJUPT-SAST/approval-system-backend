-- 插入默认管理员用户
SET @admin_salt = REPLACE(UUID(), '-', '');
INSERT IGNORE INTO `user` (`code`, `password`, `name`, `role`, `salt`, `create_time`, `update_time`) 
VALUES ('admin', MD5(CONCAT('admin', @admin_salt)), 'admin', 3, @admin_salt, NOW(), NOW());