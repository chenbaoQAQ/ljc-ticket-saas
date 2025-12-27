-- 1. 插入初始化公司
INSERT INTO `company` (`id`, `name`) VALUES (1, '超级科技有限责任公司');

-- 2. 插入初始化员工
-- 明文密码：123456
INSERT INTO `employee` (`id`, `name`, `username`, `password_hash`, `role`, `company_id`, `phone`)
VALUES (1, '李俊辰', 'lijunchen', '$2a$10$fgPbgra95150E9ydNUQH/O0KPpnrRnRMYuoOlMTXPX1gpXjWwCfaW', 'admin', 1, '13800138000');

INSERT INTO `employee` (`id`, `name`, `username`, `password_hash`, `role`, `company_id`, `phone`)
VALUES (2, '陈聪', 'chencong', '$2a$10$fgPbgra95150E9ydNUQH/O0KPpnrRnRMYuoOlMTXPX1gpXjWwCfaW', 'user', 1, '13900139000');

-- 3. 插入初始化工单
INSERT INTO `work_order`
(`id`,`title`,`content`,`status`,`creator_id`,`handler_id`,`company_id`)
VALUES
    (1,'测试工单','for ticket test','OPEN',1,NULL,1);

