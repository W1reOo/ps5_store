-- 单独执行：为已有商城库增加评论表（若已在 ps5_shop.sql 全量建库可跳过）
CREATE TABLE IF NOT EXISTS `ps5_game_comment` (
  `comment_id` bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `game_id`    bigint(20)   NOT NULL COMMENT '游戏ID',
  `user_id`    bigint(20)   NOT NULL COMMENT '用户ID',
  `user_name`  varchar(64)  NOT NULL DEFAULT '' COMMENT '显示名称（发表时快照）',
  `parent_id`  bigint(20)   DEFAULT NULL COMMENT '父评论ID，NULL=主楼',
  `root_id`    bigint(20)   DEFAULT NULL COMMENT '楼根评论ID',
  `reply_to_user_id`   bigint(20)   DEFAULT NULL COMMENT '被回复用户ID',
  `reply_to_user_name` varchar(64)  DEFAULT '' COMMENT '被回复用户显示名快照',
  `content`    varchar(500) NOT NULL COMMENT '评论内容',
  `status`     char(1)      DEFAULT '0' COMMENT '状态（0正常 1隐藏）',
  `create_time` datetime    DEFAULT NULL COMMENT '发表时间',
  PRIMARY KEY (`comment_id`),
  KEY `idx_game_status_time` (`game_id`, `status`, `create_time`),
  KEY `idx_game_parent` (`game_id`, `parent_id`),
  KEY `idx_game_root` (`game_id`, `root_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='PS5游戏评论（支持楼中楼）';
