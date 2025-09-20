-- 重置用户表密码和盐值的SQL脚本
-- 此脚本用于解决user表中密码和盐值为空的问题

-- 步骤1: 连接到review数据库
USE review;

-- 步骤2: 查看当前用户表状态
SELECT 
  id, 
  code, 
  name, 
  CASE WHEN password IS NULL OR password = '' THEN '空' ELSE '已设置' END AS password_status, 
  CASE WHEN salt IS NULL OR salt = '' THEN '空' ELSE '已设置' END AS salt_status, 
  role 
FROM `user`;

-- 步骤3: 为密码或盐值为空的用户设置新的密码和盐值

-- 管理员账号 - 用户名: admin, 密码: admin
UPDATE `user` 
SET 
  password = MD5(CONCAT('admin', 'admin_fixed_salt_2025')), 
  salt = 'admin_fixed_salt_2025' 
WHERE code = 'admin' AND (password IS NULL OR password = '' OR salt IS NULL OR salt = '');

-- 游客账号 - 用户名: tourist, 密码: tourist
UPDATE `user` 
SET 
  password = MD5(CONCAT('tourist', 'tourist_fixed_salt_2025')), 
  salt = 'tourist_fixed_salt_2025' 
WHERE code = 'tourist' AND (password IS NULL OR password = '' OR salt IS NULL OR salt = '');

-- 学生账号 - 用户名: student, 密码: student
UPDATE `user` 
SET 
  password = MD5(CONCAT('student', 'student_fixed_salt_2025')), 
  salt = 'student_fixed_salt_2025' 
WHERE code = 'student' AND (password IS NULL OR password = '' OR salt IS NULL OR salt = '');

-- 评审账号 - 用户名: review, 密码: review
UPDATE `user` 
SET 
  password = MD5(CONCAT('review', 'review_fixed_salt_2025')), 
  salt = 'review_fixed_salt_2025' 
WHERE code = 'review' AND (password IS NULL OR password = '' OR salt IS NULL OR salt = '');

-- 裁判账号 - 用户名: judge, 密码: judge
UPDATE `user` 
SET 
  password = MD5(CONCAT('judge', 'judge_fixed_salt_2025')), 
  salt = 'judge_fixed_salt_2025' 
WHERE code = 'judge' AND (password IS NULL OR password = '' OR salt IS NULL OR salt = '');

-- 步骤4: 验证修改结果
SELECT 
  id, 
  code, 
  name, 
  LENGTH(password) AS password_length, 
  salt, 
  role 
FROM `user`;

-- 步骤5: 如果需要，创建检查触发器（可选）
/*
-- 创建一个触发器，防止密码或盐值被设置为空
DELIMITER //
CREATE TRIGGER before_user_update
BEFORE UPDATE ON `user`
FOR EACH ROW
BEGIN
  IF NEW.password IS NULL OR NEW.password = '' THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '密码不能为空';
  END IF;
  IF NEW.salt IS NULL OR NEW.salt = '' THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '盐值不能为空';
  END IF;
END;
//
DELIMITER ;
*/

-- 使用说明：
-- 1. 执行此脚本后，所有用户账号的密码将被重置为与用户名相同
-- 2. 例如：使用账号admin和密码admin可以登录系统
-- 3. 登录后请及时修改默认密码，提高系统安全性
-- 4. 如果需要禁用触发器，请删除步骤5中的注释符号

-- 注意事项：
-- 此脚本适用于开发环境，生产环境中请使用更复杂的密码策略
-- 执行前请确保已备份数据库，以防止数据丢失