-- 撤销 shop_merchant 相关结构（在已执行过 shop_merchant 脚本的数据库上执行）
-- 若某步报错（例如列已不存在），可注释掉对应语句后重试。

DROP TABLE IF EXISTS `shop_merchant`;

ALTER TABLE `ps5_game` DROP COLUMN `merchant_id`;
