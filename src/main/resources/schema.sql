-- 1. 彻底清除旧表（顺序很重要，先删有外键依赖的）
DROP TABLE IF EXISTS `ticket`;
DROP TABLE IF EXISTS `work_order`;
DROP TABLE IF EXISTS `employee`;
DROP TABLE IF EXISTS `company`;

-- 2. 创建公司表 🏢
CREATE TABLE `company` (
                           `id` BIGINT NOT NULL AUTO_INCREMENT,
                           `name` VARCHAR(255) NOT NULL COMMENT '公司名称',
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. 创建员工表 👨‍💼（现在包含手机号了）
CREATE TABLE `employee` (
                            `id` BIGINT NOT NULL AUTO_INCREMENT,
                            `name` VARCHAR(100) NOT NULL COMMENT '姓名',
                            `role` VARCHAR(50) DEFAULT 'user' COMMENT '角色',
                            `company_id` BIGINT NOT NULL COMMENT '所属公司ID',
                            `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. 创建工单表 🎫
CREATE TABLE `work_order` (
                              `id` BIGINT NOT NULL AUTO_INCREMENT,
                              `title` VARCHAR(255) NOT NULL COMMENT '工单标题',
                              `content` TEXT COMMENT '工单内容',
                              `status` VARCHAR(20) DEFAULT 'OPEN' COMMENT '状态',
                              `creator_id` BIGINT NOT NULL COMMENT '创建者ID',
                              `company_id` BIGINT NOT NULL COMMENT '公司ID',
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. 创建工单详情记录表 🎟️
CREATE TABLE `ticket` (
                          `id` BIGINT NOT NULL AUTO_INCREMENT,
                          `work_order_id` BIGINT NOT NULL COMMENT '所属工单ID',
                          `description` TEXT COMMENT '处理描述',
                          `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;