-- 1. 插入初始化公司
INSERT INTO `company` (`id`, `name`) VALUES (1, '超级科技有限责任公司');

-- 2. 插入初始化员工 (注意包含 phone 字段)
INSERT INTO `employee` (`id`, `name`, `role`, `company_id`, `phone`)
VALUES (1, '李俊辰', 'admin', 1, '13800138000');

INSERT INTO `employee` (`id`, `name`, `role`, `company_id`, `phone`)
VALUES (2, '陈聪', 'user', 1, '13900139000');