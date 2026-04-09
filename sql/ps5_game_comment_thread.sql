-- 已有 ps5_game_comment 表时执行：扩展楼中楼字段
-- 若表是按旧脚本建的（无 parent_id），执行本脚本

ALTER TABLE `ps5_game_comment`
  ADD COLUMN `parent_id` bigint(20) DEFAULT NULL COMMENT '父评论ID，NULL=主楼' AFTER `user_name`,
  ADD COLUMN `root_id` bigint(20) DEFAULT NULL COMMENT '楼根评论ID' AFTER `parent_id`,
  ADD COLUMN `reply_to_user_id` bigint(20) DEFAULT NULL COMMENT '被回复用户ID' AFTER `root_id`,
  ADD COLUMN `reply_to_user_name` varchar(64) DEFAULT '' COMMENT '被回复用户显示名快照' AFTER `reply_to_user_id`;

-- 历史数据：全部视为主楼，root_id 自指
UPDATE `ps5_game_comment` SET `root_id` = `comment_id` WHERE `parent_id` IS NULL;

ALTER TABLE `ps5_game_comment`
  ADD KEY `idx_game_parent` (`game_id`, `parent_id`),
  ADD KEY `idx_game_root` (`game_id`, `root_id`);
