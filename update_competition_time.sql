-- 更新比赛时间为当前可报名范围
USE review;

-- 查询当前比赛的时间信息
SELECT id, name, reg_begin_time, reg_end_time, is_review FROM `competition`;

-- 更新所有已审批比赛的报名时间为当前时间前后30天
-- 这样可以确保比赛处于可报名状态
UPDATE `competition`
SET 
  reg_begin_time = DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 30 DAY), '%Y-%m-%d %H:%i:%s'),
  reg_end_time = DATE_FORMAT(DATE_ADD(NOW(), INTERVAL 30 DAY), '%Y-%m-%d %H:%i:%s'),
  update_time = NOW()
WHERE is_review = 0; -- 0表示已审批

-- 再次查询确认时间已更新
SELECT id, name, reg_begin_time, reg_end_time, is_review FROM `competition`;

-- 查询结果说明:
-- 1. reg_begin_time 应该是30天前的时间
-- 2. reg_end_time 应该是30天后的时间
-- 这样可以确保当前时间处于报名时间范围内