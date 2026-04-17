-- PS5游戏商城数据库脚本 v1.0

-- ----------------------------
-- 1. 游戏分类表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ps5_category` (
  `category_id`   bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `category_name` varchar(50)  NOT NULL COMMENT '分类名称',
  `icon`          varchar(200) DEFAULT '' COMMENT '分类图标',
  `sort`          int(4)       DEFAULT 0 COMMENT '排序',
  `status`        char(1)      DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by`     varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time`   datetime     COMMENT '创建时间',
  `update_by`     varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time`   datetime     COMMENT '更新时间',
  `remark`        varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 COMMENT='PS5游戏分类表';

-- ----------------------------
-- 2. 游戏信息表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ps5_game` (
  `game_id`        bigint(20)     NOT NULL AUTO_INCREMENT COMMENT '游戏ID',
  `game_name`      varchar(100)   NOT NULL COMMENT '游戏名称',
  `category_id`    bigint(20)     DEFAULT NULL COMMENT '分类ID',
  `publisher`      varchar(100)   DEFAULT '' COMMENT '发行商',
  `developer`      varchar(100)   DEFAULT '' COMMENT '开发商',
  `description`    text           COMMENT '游戏描述',
  `cover_image`    varchar(500)   DEFAULT '' COMMENT '封面图片',
  `price`          decimal(10,2)  NOT NULL DEFAULT 0.00 COMMENT '售价',
  `original_price` decimal(10,2)  DEFAULT 0.00 COMMENT '原价',
  `stock`          int(11)        DEFAULT 0 COMMENT '库存',
  `sales`          int(11)        DEFAULT 0 COMMENT '销量',
  `release_date`   date           COMMENT '发行日期',
  `language`       varchar(50)    DEFAULT '' COMMENT '游戏语言',
  `rating`         varchar(10)    DEFAULT '' COMMENT '评级',
  `status`         char(1)        DEFAULT '0' COMMENT '状态（0上架 1下架）',
  `is_hot`         char(1)        DEFAULT '0' COMMENT '是否热门（0否 1是）',
  `is_new`         char(1)        DEFAULT '1' COMMENT '是否新品（0否 1是）',
  `create_by`      varchar(64)    DEFAULT '' COMMENT '创建者',
  `create_time`    datetime       COMMENT '创建时间',
  `update_by`      varchar(64)    DEFAULT '' COMMENT '更新者',
  `update_time`    datetime       COMMENT '更新时间',
  `remark`         varchar(500)   DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`game_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='PS5游戏表';

-- ----------------------------
-- 3. 购物车表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ps5_cart` (
  `cart_id`     bigint(20) NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
  `user_id`     bigint(20) NOT NULL COMMENT '用户ID',
  `game_id`     bigint(20) NOT NULL COMMENT '游戏ID',
  `quantity`    int(11)    DEFAULT 1 COMMENT '数量',
  `create_time` datetime   COMMENT '创建时间',
  `update_time` datetime   COMMENT '更新时间',
  PRIMARY KEY (`cart_id`),
  UNIQUE KEY `uk_user_game` (`user_id`, `game_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='PS5购物车表';

-- ----------------------------
-- 3b. 商品收藏表（用户维度，同一用户同一游戏仅一条）
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ps5_favorite` (
  `favorite_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id`     bigint(20) NOT NULL COMMENT '用户ID',
  `game_id`     bigint(20) NOT NULL COMMENT '游戏ID',
  `create_time` datetime   DEFAULT NULL COMMENT '收藏时间',
  PRIMARY KEY (`favorite_id`),
  UNIQUE KEY `uk_user_game` (`user_id`, `game_id`),
  KEY `idx_user_time` (`user_id`, `create_time`),
  KEY `idx_game` (`game_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='PS5商品收藏表';

-- ----------------------------
-- 4. 订单表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ps5_order` (
  `order_id`        bigint(20)     NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no`        varchar(30)    NOT NULL COMMENT '订单编号',
  `user_id`         bigint(20)     NOT NULL COMMENT '用户ID',
  `total_amount`    decimal(10,2)  NOT NULL DEFAULT 0.00 COMMENT '订单总金额',
  `status`          char(1)        DEFAULT '0' COMMENT '订单状态（0待付款 1已付款 2已发货 3已完成 4已取消）',
  `receiver_name`   varchar(50)    DEFAULT '' COMMENT '收货人姓名',
  `receiver_phone`  varchar(20)    DEFAULT '' COMMENT '收货人电话',
  `receiver_address` varchar(500)  DEFAULT '' COMMENT '收货地址',
  `pay_time`        datetime       COMMENT '付款时间',
  `deliver_time`    datetime       COMMENT '发货时间',
  `finish_time`     datetime       COMMENT '完成时间',
  `remark`          varchar(500)   DEFAULT '' COMMENT '备注',
  `create_by`       varchar(64)    DEFAULT '' COMMENT '创建者',
  `create_time`     datetime       COMMENT '创建时间',
  `update_by`       varchar(64)    DEFAULT '' COMMENT '更新者',
  `update_time`     datetime       COMMENT '更新时间',
  PRIMARY KEY (`order_id`),
  UNIQUE KEY `uk_order_no` (`order_no`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='PS5订单表';

-- ----------------------------
-- 5. 订单明细表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ps5_order_item` (
  `item_id`     bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `order_id`    bigint(20)    NOT NULL COMMENT '订单ID',
  `game_id`     bigint(20)    NOT NULL COMMENT '游戏ID',
  `game_name`   varchar(100)  NOT NULL COMMENT '游戏名称（快照）',
  `cover_image` varchar(500)  DEFAULT '' COMMENT '封面图片（快照）',
  `price`       decimal(10,2) NOT NULL COMMENT '购买价格（快照）',
  `quantity`    int(11)       NOT NULL DEFAULT 1 COMMENT '购买数量',
  PRIMARY KEY (`item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='PS5订单明细表';

-- ----------------------------
-- 6. 游戏评论表（用户交流）
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ps5_game_comment` (
  `comment_id` bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `game_id`    bigint(20)   NOT NULL COMMENT '游戏ID',
  `user_id`    bigint(20)   NOT NULL COMMENT '用户ID',
  `user_name`  varchar(64)  NOT NULL DEFAULT '' COMMENT '显示名称（发表时快照）',
  `parent_id`  bigint(20)   DEFAULT NULL COMMENT '父评论ID，NULL=主楼',
  `root_id`    bigint(20)   DEFAULT NULL COMMENT '楼根评论ID（同楼内一致）',
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

-- ----------------------------
-- 初始化分类数据
-- ----------------------------
INSERT INTO `ps5_category` VALUES(1,'动作冒险','',1,'0','admin',sysdate(),'',null,null);
INSERT INTO `ps5_category` VALUES(2,'角色扮演','',2,'0','admin',sysdate(),'',null,null);
INSERT INTO `ps5_category` VALUES(3,'体育竞技','',3,'0','admin',sysdate(),'',null,null);
INSERT INTO `ps5_category` VALUES(4,'射击游戏','',4,'0','admin',sysdate(),'',null,null);
INSERT INTO `ps5_category` VALUES(5,'格斗游戏','',5,'0','admin',sysdate(),'',null,null);
INSERT INTO `ps5_category` VALUES(6,'赛车游戏','',6,'0','admin',sysdate(),'',null,null);
INSERT INTO `ps5_category` VALUES(7,'独立游戏','',7,'0','admin',sysdate(),'',null,null);

-- ----------------------------
-- 初始化游戏数据
-- ----------------------------
INSERT INTO `ps5_game` (`game_name`,`category_id`,`publisher`,`developer`,`description`,`cover_image`,`price`,`original_price`,`stock`,`sales`,`release_date`,`language`,`rating`,`status`,`is_hot`,`is_new`,`create_by`,`create_time`) VALUES
('战神：诸神黄昏',1,'索尼互动娱乐','Santa Monica Studio','战神奎托斯与儿子阿特柔斯踏上穿越九界的史诗之旅，面对诸神黄昏的到来。','https://upload-bbs.miyoushe.com/upload/2022/09/14/125766904/a68d67e2a2451af2cbf66f3d4ee13c14_7458218640282741004.jpg',299.00,398.00,100,520,'2022-11-09','中文','M','0','1','0','admin',sysdate()),
('地平线：西之禁地',1,'索尼互动娱乐','Guerrilla Games','艾洛伊继续旅程，前往充满危险的西部禁区，揭开更大的阴谋。','https://image.api.playstation.com/vulcan/ap/rnd/202107/3100/HO8vkO9pfLhwbyCo19O8JCYK.png',249.00,349.00,150,380,'2022-02-18','中文','T','0','1','0','admin',sysdate()),
('最终幻想XVI',2,'史克威尔艾尼克斯','Creative Business Unit III','最终幻想系列全新正统续作，瓦利斯泽亚大陆上演的黑暗奇幻史诗。','https://image.api.playstation.com/vulcan/ap/rnd/202211/0711/kh4MBIw2LKnfm8iJn3DSHkWy.jpg',388.00,458.00,80,210,'2023-06-22','中文','M','0','1','0','admin',sysdate()),
('FIFA 23',3,'EA Sports','EA Vancouver','EA SPORTS FIFA 23融入超现实模拟技术，带来有史以来最真实的足球体验。','',178.00,278.00,200,650,'2022-09-30','中文','E','0','0','0','admin',sysdate()),
('使命召唤：现代战争II',4,'Activision','Infinity Ward','标志性特种部队141小队回归，踏上全球行动追缉危险恐怖分子。','',329.00,418.00,120,430,'2022-10-28','中文','M','0','1','0','admin',sysdate()),
('铁拳8',5,'万代南梦宫','万代南梦宫','铁拳系列第八作，全新引擎打造，带来极具震撼的格斗体验和丰富剧情模式。','',358.00,398.00,90,175,'2024-01-26','中文','T','0','1','1','admin',sysdate()),
('GT赛车7',6,'索尼互动娱乐','Polyphony Digital','赛车游戏巅峰之作，超过400辆汽车，90个赛道，极致真实的驾驶体验。','',298.00,348.00,110,290,'2022-03-04','中文','E','0','0','0','admin',sysdate()),
('蜘蛛侠2',1,'索尼互动娱乐','Insomniac Games','彼得·帕克和迈尔斯·莫拉莱斯联手，迎战克莱文猎人和毒液，保卫纽约。','',398.00,398.00,200,680,'2023-10-20','中文','T','0','1','1','admin',sysdate()),
('艾尔登法环',2,'万代南梦宫','FromSoftware','由宫崎英高与乔治·R·R·马丁共同打造的开放世界动作RPG，探索狭间之地。','',268.00,368.00,130,520,'2022-02-25','中文','M','0','1','0','admin',sysdate()),
('死亡搁浅：导演剪辑版',1,'索尼互动娱乐','Kojima Productions','小岛秀夫野心之作，在美丽而危险的美国废土上建立连接，拯救人类。','',228.00,298.00,75,140,'2022-03-30','中文','M','0','0','0','admin',sysdate());

-- ----------------------------
-- 菜单权限配置（插入到若依菜单表）
-- menu_id,menu_name,parent_id,order_num,path,component,query,route_name,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,update_by,update_time,remark
-- ----------------------------
INSERT INTO sys_menu VALUES(2000,'PS5商城',0,4,'shop',null,null,'',1,0,'M','0','0','','shopping','admin',sysdate(),'',null,'PS5游戏商城');
INSERT INTO sys_menu VALUES(2001,'游戏管理',2000,1,'game','shop/game/index',null,'',1,0,'C','0','0','shop:game:list','gamepad','admin',sysdate(),'',null,'游戏列表');
INSERT INTO sys_menu VALUES(2002,'订单管理',2000,2,'order','shop/order/index',null,'',1,0,'C','0','0','shop:order:list','list','admin',sysdate(),'',null,'订单管理');
INSERT INTO sys_menu VALUES(2003,'商城首页',2000,3,'home','shop/home/index',null,'',1,0,'C','0','0','','home','admin',sysdate(),'',null,'商城首页');
INSERT INTO sys_menu VALUES(2004,'销售额统计',2000,4,'revenue','shop/revenue/index',null,'',1,0,'C','0','0','shop:revenue:list','money','admin',sysdate(),'',null,'销售额统计');

-- 角色菜单映射（确保普通角色/超级管理员都能看到并可跳转）
-- 角色ID：1=超级管理员，2=普通角色（见 ry_20260320.sql）
INSERT INTO sys_role_menu VALUES ('1', '2004');
INSERT INTO sys_role_menu VALUES ('2', '2004');
INSERT INTO sys_role_menu VALUES ('1', '2022');
INSERT INTO sys_role_menu VALUES ('2', '2022');

-- 游戏管理按钮权限
INSERT INTO sys_menu VALUES(2010,'游戏查询',2001,1,'','',null,'',1,0,'F','0','0','shop:game:query','#','admin',sysdate(),'',null,'');
INSERT INTO sys_menu VALUES(2011,'游戏新增',2001,2,'','',null,'',1,0,'F','0','0','shop:game:add','#','admin',sysdate(),'',null,'');
INSERT INTO sys_menu VALUES(2012,'游戏修改',2001,3,'','',null,'',1,0,'F','0','0','shop:game:edit','#','admin',sysdate(),'',null,'');
INSERT INTO sys_menu VALUES(2013,'游戏删除',2001,4,'','',null,'',1,0,'F','0','0','shop:game:remove','#','admin',sysdate(),'',null,'');

-- 订单管理按钮权限
INSERT INTO sys_menu VALUES(2020,'订单查询',2002,1,'','',null,'',1,0,'F','0','0','shop:order:query','#','admin',sysdate(),'',null,'');
INSERT INTO sys_menu VALUES(2021,'订单发货',2002,2,'','',null,'',1,0,'F','0','0','shop:order:deliver','#','admin',sysdate(),'',null,'');
INSERT INTO sys_menu VALUES(2022,'订单退款',2002,3,'','',null,'',1,0,'F','0','0','shop:order:refund','#','admin',sysdate(),'',null,'');

-- ============================================================
-- 可选：销量重算（把之前手填/自定义销量清掉）
-- 规则：统计有效销售（status in 1/2/3/5；已退款 6 不计）
-- ============================================================
-- 1) 先清零
-- UPDATE ps5_game SET sales = 0;
--
-- 2) 再按已付款订单汇总回填
-- UPDATE ps5_game g
-- LEFT JOIN (
--     SELECT oi.game_id, SUM(oi.quantity) AS sold_quantity
--     FROM ps5_order_item oi
--     INNER JOIN ps5_order o ON oi.order_id = o.order_id
--     WHERE o.status IN ('1','2','3','5')
--     GROUP BY oi.game_id
-- ) t ON g.game_id = t.game_id
-- SET g.sales = COALESCE(t.sold_quantity, 0);
