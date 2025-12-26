-- 1. 清除旧表（有外键依赖的先删）
DROP TABLE IF EXISTS `ticket`;
DROP TABLE IF EXISTS `work_order`;
DROP TABLE IF EXISTS `employee`;
DROP TABLE IF EXISTS `company`;

-- 2. 公司表（租户）
CREATE TABLE `company` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL COMMENT '公司名称',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_company_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. 员工表
CREATE TABLE `employee` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL COMMENT '姓名',
  `role` VARCHAR(50) DEFAULT 'user' COMMENT '角色',
  `company_id` BIGINT NOT NULL COMMENT '所属公司ID',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  PRIMARY KEY (`id`),
  KEY `idx_employee_company` (`company_id`),
  CONSTRAINT `fk_employee_company` FOREIGN KEY (`company_id`) REFERENCES `company`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. 工单表
CREATE TABLE `work_order` (
                              `id` BIGINT NOT NULL AUTO_INCREMENT,
                              `title` VARCHAR(255) NOT NULL COMMENT '工单标题',
                              `content` TEXT COMMENT '工单内容',
                              `status` VARCHAR(20) DEFAULT 'OPEN' COMMENT '状态',
                              `creator_id` BIGINT NOT NULL COMMENT '创建者ID',
                              `handler_id` BIGINT DEFAULT NULL COMMENT '处理人ID（可为空：未指派）',
                              `company_id` BIGINT NOT NULL COMMENT '公司ID',

                              `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

                              PRIMARY KEY (`id`),
                              KEY `idx_wo_company` (`company_id`),
                              KEY `idx_wo_creator` (`creator_id`),
                              KEY `idx_wo_handler` (`handler_id`),
                              CONSTRAINT `fk_wo_company` FOREIGN KEY (`company_id`) REFERENCES `company`(`id`),
                              CONSTRAINT `fk_wo_creator` FOREIGN KEY (`creator_id`) REFERENCES `employee`(`id`),
                              CONSTRAINT `fk_wo_handler` FOREIGN KEY (`handler_id`) REFERENCES `employee`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. 工单处理记录表（ticket：流水/评论）
CREATE TABLE `ticket` (
                          `id` BIGINT NOT NULL AUTO_INCREMENT,
                          `work_order_id` BIGINT NOT NULL COMMENT '所属工单ID',
                          `description` TEXT COMMENT '处理描述',

                          `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

                          PRIMARY KEY (`id`),
                          KEY `idx_ticket_work_order_time` (`work_order_id`, `create_time`),
                          CONSTRAINT `fk_ticket_work_order` FOREIGN KEY (`work_order_id`) REFERENCES `work_order`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
